package io.github.forezp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import static io.github.forezp.HttpConstants.CPUS;

/**
 * Created by forezp on 2019/5/29.
 */
public class HttpAsyncClientExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(HttpAsyncClientExecutor.class);

    private CloseableHttpAsyncClient httpAsyncClient;

    private ClientConfigEntity clientConfigEntity;

    public HttpAsyncClientExecutor(ClientConfigEntity clientConfigEntity) {
        this.clientConfigEntity = clientConfigEntity;
    }

    protected void initialize() throws Exception {
        initialize(clientConfigEntity);
    }

    protected void initialize(final ClientConfigEntity configEntity) throws Exception {

        final CyclicBarrier barrier = new CyclicBarrier(2);
        Executors.newCachedThreadPool().submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                            .setIoThreadCount(CPUS * 2)
                            .setConnectTimeout(configEntity.getHttpConnectTimeout())
                            .setSoTimeout(configEntity.getHttpSocketTimeout())
                            .setSndBufSize(configEntity.getHttpSendBufSize())
                            .setRcvBufSize(configEntity.getHttpRcvBufSize())
                            .setBacklogSize(configEntity.getHttpBackLogSize())
                            .setTcpNoDelay(true)
                            .setSoReuseAddress(true)
                            .setSoKeepAlive(true)
                            .build();
                    ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
                    PoolingNHttpClientConnectionManager httpManager = new PoolingNHttpClientConnectionManager(ioReactor);
                    httpManager.setMaxTotal(configEntity.getHttpMaxTotal());

                    httpAsyncClient = HttpAsyncClients.custom().setConnectionManager(httpManager).build();
                    httpAsyncClient.start();

                    LOG.info("Create apache async client successfully");

                    barrier.await();
                } catch (IOReactorException e) {
                    LOG.error("Create apache async client failed", e);
                }

                return null;
            }
        });

        barrier.await();
    }

    public CloseableHttpAsyncClient getClient() {
        return httpAsyncClient;
    }


    public void post(String url, Object object, AResonseCallBack callback) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        String value = SerializerExecutor.toJson(object);

        HttpEntity entity = new StringEntity(value, "utf-8");

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/json;charset=utf-8");
        httpPost.setEntity(entity);

        DefaultHttpAResonseCallBack httpAResonseCallBack = new DefaultHttpAResonseCallBack();
        httpAResonseCallBack.setHttpPost(httpPost);
        httpAResonseCallBack.setAResonseCallBack(callback);
        httpAsyncClient.execute(httpPost, httpAResonseCallBack);
    }

    public void post(String url, Object object) {
        post(url, object, null);
    }


    public class DefaultHttpAResonseCallBack implements FutureCallback<HttpResponse> {
        private HttpPost httpPost;

        private AResonseCallBack aResonseCallBack;

        public void setHttpPost(HttpPost httpPost) {
            this.httpPost = httpPost;
        }

        public void setAResonseCallBack(AResonseCallBack aResonseCallBack) {
            this.aResonseCallBack = aResonseCallBack;
        }

        @Override
        public void completed(HttpResponse httpResponse) {
            if (aResonseCallBack != null) {
                aResonseCallBack.completed(httpResponse);
            }
            httpPost.reset();
        }

        @Override
        public void failed(Exception e) {

            if (aResonseCallBack != null) {
                aResonseCallBack.failed(e);
            }
            httpPost.reset();
            LOG.error("Monitor web service invoke failed, url={}", httpPost.getURI(), e);
        }

        @Override
        public void cancelled() {
            httpPost.reset();
        }


    }


    public interface AResonseCallBack {
        void completed(HttpResponse httpResponse);

        void failed(Exception e);

    }

}