# matrix-http


[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/forezp/matrix-eventbus/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.forezp/matrix-http-starter.svg?label=maven%20central)](http://mvnrepository.com/artifact/io.github.forezp/matrix-http-starter)

## 这个项目干嘛的?

httpclient创建和管理的工具类

## 怎么用


### 添加依赖：


```$xslt

<dependency>
  <groupId>io.github.forezp</groupId>
  <artifactId>matrix-http-starter</artifactId>
  <version>${version}</version>
</dependency>
```




### 使用

在配置文件中添加：

```
http.sync.enable=true
http.async.enable=true

```

httpclient的配置如下，如果不填，即为默认值：

```
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

```
使用：

```

@SpringBootApplication
@RestController
public class MatrixHttpExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatrixHttpExampleApplication.class, args);
	}

	@Autowired
	ApacheAsyncClientExecutor apacheAsyncClientExecutor;
	@Autowired
	ApacheSyncClientExecutor apacheSyncClientExecutor;

	@GetMapping("/test")
	public String test(){

		apacheAsyncClientExecutor.post("baidu.com", "11", new ApacheAsyncClientExecutor.AResonseCallBack() {
			@Override
			public void completed(HttpResponse httpResponse) {
				System.out.println(httpResponse.getEntity().toString());
			}

			@Override
			public void failed(Exception e) {
				e.printStackTrace();
			}
		});
		return "ok";
	}



}

```
