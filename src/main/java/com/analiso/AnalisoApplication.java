package com.analiso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AnalisoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalisoApplication.class, args);
	}

}
