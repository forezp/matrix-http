# matrix-http


[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/forezp/matrix-eventbus/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.forezp/matrix-http-starter.svg?label=maven%20central)](http://mvnrepository.com/artifact/io.github.forezp/matrix-http-starter)

## 这个项目干嘛的?

httpclient创建和管理的工具类。

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

   package io.github.forezp;
   
   import com.alibaba.fastjson.JSON;
   import com.fasterxml.jackson.annotation.JsonAlias;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   import java.util.HashMap;
   import java.util.Map;
   
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
       public String test() {
   
           apacheSyncClientExecutor.get("https://www.baidu.com", new ResonseCallBack() {
               @Override
               public void completed(int httpCode, String result) {
                   System.out.println(result);
               }
   
               @Override
               public void failed(Exception e) {
   
               }
           });
           return "ok";
       }
   
       @GetMapping("/test2")
       public Keyswords testJieba() {
           String url = "http://fangzhipeng.com/test";
           Map<String, Object> paras = new HashMap<>();
           paras.put("title", "詹姆斯带队湖人登第一");
           paras.put("content", "詹姆斯给力，浓眉给力");
           ResonseCallBack.DEAULT deault = new ResonseCallBack.DEAULT();
           apacheSyncClientExecutor.postForm(url, paras, deault);
           Keyswords keyswords = JSON.parseObject(deault.getData(), Keyswords.class);
           System.out.println(JSON.toJSONString(keyswords));
           return keyswords;
       }
   
   
   }


```
