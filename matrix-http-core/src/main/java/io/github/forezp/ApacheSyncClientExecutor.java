package io.github.forezp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by forezp on 2019/5/29.
 */
public class ApacheSyncClientExecutor {
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


    public void postJson(String url, Object params, SyncCallback syncCallback) {
        url = CommonUtils.decorateUrl(url);
        String value = SerializerExecutor.toJson(params);

        HttpEntity entity = new StringEntity(value, "utf-8");

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/json;charset=utf-8");
        httpPost.setEntity(entity);
        handeleRequest(httpPost, syncCallback);
    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public void postForm(String url, Map<String, Object> map) throws Exception {
        // 1. 声明httppost
        url = CommonUtils.decorateUrl(url);
        HttpPost httpPost = new HttpPost(url);

        // 2.封装请求参数，请求数据是表单
        // 声明封装表单数据的容器
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        if (map != null) {

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                // 封装请求参数到容器中
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        // 创建表单的Entity类
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");

        // 3. 把封装好的表单实体对象设置到HttpPost中
        httpPost.setEntity(entity);

        // 4. 使用Httpclient发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);

        // 5. 解析返回数据，封装HttpResult
        // 5.1状态码
        int code = response.getStatusLine().getStatusCode();
        // 5.2 响应体内容
        String body = null;
        if (response.getEntity() != null) {
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        HttpResult result = new HttpResult();
        result.setCode(code);
        result.setBody(body);
        return result;
    }

    private void handeleRequest(HttpUriRequest httpPost, SyncCallback syncCallback) {
        CloseableHttpResponse response = null;
        try {
            response = httpSyncClient.execute(httpPost);
            LOG.info(response.getStatusLine().getStatusCode() + "");
            int code = response.getStatusLine().getStatusCode();
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            syncCallback.completed(code, res);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            syncCallback.failed(e);
        }
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface SyncCallback {
        void completed(int code, String result);

        void failed(Exception e);

    }
}