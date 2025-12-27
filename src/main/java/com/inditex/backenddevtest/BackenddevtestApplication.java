package com.inditex.backenddevtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BackenddevtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackenddevtestApplication.class, args);
	}

}
