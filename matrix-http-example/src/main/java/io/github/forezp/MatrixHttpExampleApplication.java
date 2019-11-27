package io.github.forezp;

import org.apache.http.HttpResponse;
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

        apacheSyncClientExecutor.get("https://www.baidu.com",new ApacheSyncClientExecutor.SyncCallback() {

            @Override
            public void completed(int code, String result) {
                System.out.println(result);
            }

            @Override
            public void failed(Exception e) {

            }
        });
        return "ok";
    }

    @GetMapping("/test2")
    public String testJieba() {
        String url = "http://jieba.fangzhipeng.com/test/jieba";
        Map<String, Object> paras = new HashMap<>();
        paras.put("title", "詹姆斯带队湖人登第一");
        paras.put("content", "詹姆斯给力，浓眉给力");
        SyncCallbackImpl syncCallback = new SyncCallbackImpl();
        apacheSyncClientExecutor.postForm(url, paras, syncCallback);
        System.out.println(syncCallback.code);
        return syncCallback.result;


    }

    class SyncCallbackImpl implements ApacheSyncClientExecutor.SyncCallback {

        private String result;
        private int code;

        @Override
        public void completed(int code, String result) {
            this.result = result;
            this.code = code;
        }

        @Override
        public void failed(Exception e) {

        }

    }


}
