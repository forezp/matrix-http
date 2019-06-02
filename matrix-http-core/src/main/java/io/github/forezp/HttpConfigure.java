package io.github.forezp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static io.github.forezp.HttpConstants.*;

/**
 * Created by forezp on 2019/5/29.
 */
@Configuration
public class HttpConfigure {

    @Value("${"+HTTPCLIENT_CONNCT_TIMEOUT+":5000}")
    Integer httpConnectTimeout;

    @Value("${"+HTTPCLIENT_CONNCT_REQUEST_TIMEOUT+":5000}")
    Integer httpRequestTimeout;

    @Value("${"+HTTPCLIENT_SOCKET_TIMEOUT+":5000}")
    Integer httpSocketTimeout;

    @Value("${"+HTTPCLIENT_SEDBUFSIZE+":65536}")
    Integer httpSendBufSize;


    @Value("${"+HTTPCLIENT_RCV_BUFSIZE+":65536}")
    Integer httpRcvBufSize;

    @Value("${"+HTTPCLIENT_BACK_LOG_SIZE+":128}")
    Integer httpBackLogSize;

    @Value("${"+HTTPCLIENT_MAX_TOTAL+":64}")
    Integer httpMaxTotal;

    @Conditional(HttpSyncEnable.class)
    @Bean
    public ApacheSyncClientExecutor apacheSyncClientExecutor() throws Exception {
        ClientConfigEntity entity=new ClientConfigEntity();
        entity.setHttpConnectTimeout(httpConnectTimeout);
        entity.setHttpRequestTimeout(httpRequestTimeout);
        entity.setHttpSocketTimeout(httpSocketTimeout);
        entity.setHttpSendBufSize(httpSendBufSize);
        entity.setHttpRcvBufSize(httpRcvBufSize);
        entity.setHttpMaxTotal(httpMaxTotal);
        entity.setHttpBackLogSize(httpBackLogSize);
        ApacheSyncClientExecutor apacheSyncClientExecutor=new ApacheSyncClientExecutor(entity);
        apacheSyncClientExecutor.initialize();
        return apacheSyncClientExecutor;
    }

    @Conditional(HttpAsyncEnable.class)
    @Bean
    public ApacheAsyncClientExecutor apacheAsyncClientExecutor() throws Exception{
        ClientConfigEntity entity=new ClientConfigEntity();
        entity.setHttpConnectTimeout(httpConnectTimeout);
        entity.setHttpRequestTimeout(httpRequestTimeout);
        entity.setHttpSocketTimeout(httpSocketTimeout);
        entity.setHttpSendBufSize(httpSendBufSize);
        entity.setHttpRcvBufSize(httpRcvBufSize);
        entity.setHttpMaxTotal(httpMaxTotal);
        entity.setHttpBackLogSize(httpBackLogSize);
        ApacheAsyncClientExecutor apacheAsyncClientExecutor=new ApacheAsyncClientExecutor(entity);
        apacheAsyncClientExecutor.initialize();
        return apacheAsyncClientExecutor;
    }
}