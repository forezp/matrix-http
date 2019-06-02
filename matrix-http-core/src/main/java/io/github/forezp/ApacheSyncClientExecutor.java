package io.github.forezp;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by forezp on 2019/5/29.
 */
public class ApacheSyncClientExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(ApacheSyncClientExecutor.class);

    private CloseableHttpClient httpSyncClient;
    private ClientConfigEntity configEntity;

    public ApacheSyncClientExecutor(ClientConfigEntity configEntity) {
        this.configEntity = configEntity;
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
        }

        httpSyncClient = clientBuilder.build();

        LOG.info("Create apache sync client with {} successfully", https ? "https mode" : "http mode");
    }

    public CloseableHttpClient getClient() {
        return httpSyncClient;
    }


    public CloseableHttpResponse post(String url, Object object, ApacheAsyncClientExecutor.AsyncCallback callback) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        String value = SerializerExecutor.toJson(object);

        HttpEntity entity = new StringEntity(value, "utf-8");

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/json;charset=utf-8");
        httpPost.setEntity(entity);

        try {
            CloseableHttpResponse closeableHttpResponse= httpSyncClient.execute(httpPost);
            LOG.info(closeableHttpResponse.getStatusLine().getStatusCode()+"");
            return closeableHttpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}