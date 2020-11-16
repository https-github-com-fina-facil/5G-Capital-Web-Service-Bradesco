package com.bradesco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DecryptApplication {

	public static void main(String[] args) {
		SpringApplication.run(DecryptApplication.class, args);
	}

}
