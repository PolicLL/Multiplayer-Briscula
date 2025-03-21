package com.example.briscula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.example")
public class BrisculaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrisculaApplication.class, args);
	}

}
