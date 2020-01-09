package io.github.forezp;


import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import static io.github.forezp.HttpConstants.*;

/**
 * Created by forezp on 2019/5/29.
 */
@Configuration
public class HttpConfigure {

    private static Logger logger = LoggerFactory.getLogger(HttpConfigure.class);

    @Value("${" + HTTPCLIENT_CONNCT_TIMEOUT + ":5000}")
    Integer httpConnectTimeout;

    @Value("${" + HTTPCLIENT_CONNCT_REQUEST_TIMEOUT + ":5000}")
    Integer httpRequestTimeout;

    @Value("${" + HTTPCLIENT_SOCKET_TIMEOUT + ":5000}")
    Integer httpSocketTimeout;

    @Value("${" + HTTPCLIENT_SEDBUFSIZE + ":65536}")
    Integer httpSendBufSize;


    @Value("${" + HTTPCLIENT_RCV_BUFSIZE + ":65536}")
    Integer httpRcvBufSize;

    @Value("${" + HTTPCLIENT_BACK_LOG_SIZE + ":128}")
    Integer httpBackLogSize;

    @Value("${" + HTTPCLIENT_MAX_TOTAL + ":64}")
    Integer httpMaxTotal;
    @Value("${" + HTTPCLIENT_CONNECT_TIMER_REPEAT_ + ":120000}")
    Integer connectionTimerRepeat;

    @Value("${" + HTTPS_ENABLE + ":false}")
    Boolean httpsEnable;


    @Bean
    public ClientConfigEntity clientConfigEntity() {
        ClientConfigEntity entity = new ClientConfigEntity();
        entity.setHttpConnectTimeout(httpConnectTimeout);
        entity.setHttpRequestTimeout(httpRequestTimeout);
        entity.setHttpSocketTimeout(httpSocketTimeout);
        entity.setHttpSendBufSize(httpSendBufSize);
        entity.setHttpRcvBufSize(httpRcvBufSize);
        entity.setHttpMaxTotal(httpMaxTotal);
        entity.setHttpBackLogSize(httpBackLogSize);
        entity.setConnectionTimerRepeat(connectionTimerRepeat);
        entity.setHttpsEnable(httpsEnable);
        return entity;
    }


    @Conditional(HttpSyncEnable.class)
    @Configuration
    protected static class ApacheSyncClientExecutorConfiguration {

        private final Timer connectionManagerTimer = new Timer(
                "ApacheSyncClientExecutorConfiguration.connectionManagerTimer", true);

        @Autowired
        ClientConfigEntity entity;

        //开启keep-alive
        @Bean
        public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
            ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    HeaderElementIterator it = new BasicHeaderElementIterator
                            (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase
                                ("timeout")) {
                            return Long.parseLong(value) * 1000;
                        }
                    }
                    return 60 * 1000;//如果没有约定，则默认定义时长为60s
                }
            };
            return myStrategy;
        }

        //链接管理
        @Bean
        public HttpClientConnectionManager httpClientConnectionManager() {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(500);
            connectionManager.setDefaultMaxPerRoute(50);//例如默认每路由最高50并发，具体依据业务来定
            this.connectionManagerTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    connectionManager.closeExpiredConnections();
                }
            }, 30000, entity.getConnectionTimerRepeat());
            return connectionManager;
        }

        //请求失败时,进行请求重试
        @Bean
        public HttpRequestRetryHandler httpRequestRetryHandler() {

            HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                    if (i > 3) {
                        //重试超过3次,放弃请求
                        logger.error("retry has more than 3 time, give up request");
                        return false;
                    }
                    if (e instanceof NoHttpResponseException) {
                        //服务器没有响应,可能是服务器断开了连接,应该重试
                        logger.error("receive no response from server, retry");
                        return true;
                    }
                    if (e instanceof SSLHandshakeException) {
                        // SSL握手异常
                        logger.error("SSL hand shake exception");
                        return false;
                    }
                    if (e instanceof InterruptedIOException) {
                        //超时
                        logger.error("InterruptedIOException");
                        return false;
                    }
                    if (e instanceof UnknownHostException) {
                        // 服务器不可达
                        logger.error("server host unknown");
                        return false;
                    }
                    if (e instanceof ConnectTimeoutException) {
                        // 连接超时
                        logger.error("Connection Time out");
                        return false;
                    }
                    if (e instanceof SSLException) {
                        logger.error("SSLException");
                        return false;
                    }
                    HttpClientContext context = HttpClientContext.adapt(httpContext);
                    HttpRequest request = context.getRequest();
                    if (!(request instanceof HttpEntityEnclosingRequest)) {
                        //如果请求不是关闭连接的请求
                        return true;
                    }
                    return false;
                }
            };
            return httpRequestRetryHandler;
        }

        @Bean
        public HttpClientExecutor httpClientExecutor(ConnectionKeepAliveStrategy connectionKeepAliveStrategy,
                                                     HttpClientConnectionManager httpClientConnectionManager, HttpRequestRetryHandler httpRequestRetryHandler) throws Exception {
            HttpClientExecutor httpClientExecutor =
                    new HttpClientExecutor(entity, connectionKeepAliveStrategy, httpClientConnectionManager, httpRequestRetryHandler);
            httpClientExecutor.initialize(entity);
            return httpClientExecutor;
        }

    }


    @Conditional(HttpAsyncEnable.class)
    @Bean
    public HttpAsyncClientExecutor httpAsyncClientExecutor(ClientConfigEntity entity) throws Exception {

        HttpAsyncClientExecutor httpAsyncClientExecutor = new HttpAsyncClientExecutor(entity);
        httpAsyncClientExecutor.initialize();
        return httpAsyncClientExecutor;
    }
}