package com.ashutosh.fylex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FylexApplication {

	public static void main(String[] args) {
		SpringApplication.run(FylexApplication.class, args);
	}

}
