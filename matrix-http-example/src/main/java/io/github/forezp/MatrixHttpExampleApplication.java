package io.github.forezp;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

		apacheAsyncClientExecutor.post("baidu.com", "11", new ApacheAsyncClientExecutor.AsyncCallback() {
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
