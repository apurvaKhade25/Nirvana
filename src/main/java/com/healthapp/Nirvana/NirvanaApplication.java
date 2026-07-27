package com.healthapp.Nirvana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class NirvanaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NirvanaApplication.class, args);
	}

}
