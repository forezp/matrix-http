package io.github.forezp;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class MatrixHttpExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatrixHttpExampleApplication.class, args);
    }

    @Autowired
    HttpAsyncClientExecutor httpAsyncClientExecutor;
    @Autowired
    HttpClientExecutor httpClientExecutor;

    @GetMapping("/test")
    public String test() {

        httpClientExecutor.get("https://www.baidu.com", new ResonseCallBack() {
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
        httpClientExecutor.postForm(url, paras, deault);
        Keyswords keyswords = JSON.parseObject(deault.getData(), Keyswords.class);
        System.out.println(JSON.toJSONString(keyswords));
        return keyswords;
    }

    @GetMapping("/test3")
    public String deleteNacosNs() {
        String url = "http://:8848/nacos/v1/console/namespaces";
        Map<String, Object> paras = new HashMap<>();
        paras.put("namespaceId", "martrix");
        ResonseCallBack.DEAULT deault = new ResonseCallBack.DEAULT();
        httpClientExecutor.deleteForm(url, paras, deault);
        System.out.println(deault.getData());
        return "ok";
    }

    @GetMapping("/upload")
    public String upload() {
        File file = new File("/Users/dde/Desktop/WX20210425-114003@2x.png");
        if (!file.exists()) {
            return "fail";
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        httpClientExecutor.upload("http:///upload", file, paramMap, new ResonseCallBack() {
            @Override
            public void completed(int httpCode, String result) {
                System.out.println(httpCode + result);
            }

            @Override
            public void failed(Exception e) {
                System.out.println(e.getMessage());
            }
        });
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("file", file);
//        paramMap.put("key", "1927@.x");
//        String result = HttpUtil.post("http://122.51.35.239:8083/upload", paramMap);
//        result = result.replace("上传成功", "").replace("\n", "").replace("\r", "");
//        result = result.trim();
        return "ok";
    }
}
