package io.github.forezp;

/**
 * Created by forezp on 2019/5/29.
 */
public class ClientConfigEntity {

    private Integer httpConnectTimeout;

    private  Integer httpRequestTimeout;

    private   Integer httpSocketTimeout;

    private  Integer httpSendBufSize;

    private  Integer httpRcvBufSize;

    private  Integer httpBackLogSize;

    private  Integer httpMaxTotal;

    private Integer connectionTimerRepeat;

    public Integer getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(Integer httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public Integer getHttpRequestTimeout() {
        return httpRequestTimeout;
    }

    public void setHttpRequestTimeout(Integer httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
    }

    public Integer getHttpSocketTimeout() {
        return httpSocketTimeout;
    }

    public void setHttpSocketTimeout(Integer httpSocketTimeout) {
        this.httpSocketTimeout = httpSocketTimeout;
    }

    public Integer getHttpSendBufSize() {
        return httpSendBufSize;
    }

    public void setHttpSendBufSize(Integer httpSendBufSize) {
        this.httpSendBufSize = httpSendBufSize;
    }

    public Integer getHttpRcvBufSize() {
        return httpRcvBufSize;
    }

    public void setHttpRcvBufSize(Integer httpRcvBufSize) {
        this.httpRcvBufSize = httpRcvBufSize;
    }

    public Integer getHttpBackLogSize() {
        return httpBackLogSize;
    }

    public void setHttpBackLogSize(Integer httpBackLogSize) {
        this.httpBackLogSize = httpBackLogSize;
    }

    public Integer getHttpMaxTotal() {
        return httpMaxTotal;
    }

    public void setHttpMaxTotal(Integer httpMaxTotal) {
        this.httpMaxTotal = httpMaxTotal;
    }

    public Integer getConnectionTimerRepeat() {
        return connectionTimerRepeat;
    }

    public void setConnectionTimerRepeat(Integer connectionTimerRepeat) {
        this.connectionTimerRepeat = connectionTimerRepeat;
    }
}