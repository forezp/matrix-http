package io.github.forezp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by forezp on 2019/5/29.
 */
public class ApacheSyncClientExecutor extends AbstractClientExcutor {
    private static final Logger LOG = LoggerFactory.getLogger(ApacheSyncClientExecutor.class);

    private CloseableHttpClient httpSyncClient;
    private ClientConfigEntity configEntity;
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;
    private HttpClientConnectionManager httpClientConnectionManager;

    public ApacheSyncClientExecutor(ClientConfigEntity configEntity,
                                    ConnectionKeepAliveStrategy connectionKeepAliveStrategy,
                                    HttpClientConnectionManager httpClientConnectionManager) {
        this.configEntity = configEntity;
        this.connectionKeepAliveStrategy = connectionKeepAliveStrategy;
        this.httpClientConnectionManager = httpClientConnectionManager;
    }

    protected void initialize() throws Exception {
        initialize(configEntity);
    }

    protected void initialize(ClientConfigEntity configEntity) throws Exception {
        initialize(configEntity, false);
    }

    protected void initialize(ClientConfigEntity configEntity, boolean https) throws Exception {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(configEntity.getHttpConnectTimeout())
                .setConnectionRequestTimeout(configEntity.getHttpRequestTimeout())
                .setSocketTimeout(configEntity.getHttpSocketTimeout())
                .build();

        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setDefaultRequestConfig(requestConfig);

        if (https) {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }

            }).build();
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

            clientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
            clientBuilder.setConnectionManager(httpClientConnectionManager);
            clientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);
        }

        httpSyncClient = clientBuilder.build();

        LOG.info("Create apache sync client with {} successfully", https ? "https mode" : "http mode");
    }

    public CloseableHttpClient getClient() {
        return httpSyncClient;
    }


    protected void handeleRequest(HttpUriRequest httpRequest, Map<String, String> headers, ResonseCallBack resonseCallBack) {
        CloseableHttpResponse response = null;
        try {
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    // 封装请求参数到容器中
                    httpRequest.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpSyncClient.execute(httpRequest);
            LOG.info(response.getStatusLine().getStatusCode() + "");
            int code = response.getStatusLine().getStatusCode();
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (resonseCallBack != null) {
                resonseCallBack.completed(code, res);
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            resonseCallBack.failed(e);
        }
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}