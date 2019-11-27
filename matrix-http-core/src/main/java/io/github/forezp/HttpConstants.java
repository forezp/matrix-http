package io.github.forezp;

/**
 * Created by forezp on 2019/5/29.
 */
public class HttpConstants {

    public static final String HTTPCLIENT_CONNCT_TIMEOUT = "httpclient.connect.timeout";
    public static final String HTTPCLIENT_CONNCT_TIMEOUT_DEFAULT = "5000";
    public static final String HTTPCLIENT_CONNCT_REQUEST_TIMEOUT = "httpclient.connect.request.timeout";
    public static final String HTTPCLIENT_CONNCT_REQUEST_TIMEOUT_DEFAULT = "5000";
    public static final String HTTPCLIENT_SOCKET_TIMEOUT = "httpclient.socket.timeout";
    public static final String HTTPCLIENT_SOCKET_TIMEOUT_DEFAULT = "5000";
    public static final String HTTPCLIENT_SEDBUFSIZE = "httpclient.send.bufsize";
    public static final String HTTPCLIENT_SEDBUFSIZE_DEFAULT = "65536";
    public static final String HTTPCLIENT_RCV_BUFSIZE = "httpclient.rcv.bufsize";
    public static final String HTTPCLIENT_RCV_BUFSIZE_DEFAULT = "65536";
    public static final String HTTPCLIENT_BACK_LOG_SIZE = "httpclient.back.logszie";
    public static final String HTTPCLIENT_BACK_LOG_SIZE_DEFAULT = "128";
    public static final String HTTPCLIENT_MAX_TOTAL = "httpclient.max.total";
    public static final String HTTPCLIENT_MAX_TOTAL_DEFAULT = "64";

    public static final String HTTPCLIENT_CONNECT_TIMER_REPEAT_ = "httpclient.connect.timer.repeat";
    public static final int CPUS = Math.max( 2, Runtime.getRuntime().availableProcessors() );


    public static final String HTTP_SYNC_ENABLE="http.sync.enable";
    public static final String HTTP_ASYNC_ENABLE="http.async.enable";
    public static final String TRUE="true";

}