package io.github.forezp;


import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Timer;
import java.util.TimerTask;

import static io.github.forezp.HttpConstants.*;

/**
 * Created by forezp on 2019/5/29.
 */
@Configuration
public class HttpConfigure {

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

        @Bean
        public ApacheSyncClientExecutor apacheSyncClientExecutor(ConnectionKeepAliveStrategy connectionKeepAliveStrategy,
                                                                 HttpClientConnectionManager httpClientConnectionManager) throws Exception {
            ApacheSyncClientExecutor apacheSyncClientExecutor =
                    new ApacheSyncClientExecutor(entity, connectionKeepAliveStrategy, httpClientConnectionManager);
            apacheSyncClientExecutor.initialize(entity, false);
            return apacheSyncClientExecutor;
        }
    }


    @Conditional(HttpAsyncEnable.class)
    @Bean
    public ApacheAsyncClientExecutor apacheAsyncClientExecutor(ClientConfigEntity entity) throws Exception {

        ApacheAsyncClientExecutor apacheAsyncClientExecutor = new ApacheAsyncClientExecutor(entity);
        apacheAsyncClientExecutor.initialize();
        return apacheAsyncClientExecutor;
    }
}