package com.toda.api.TODASERVERSPRINGBOOT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class TodaServerSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodaServerSpringbootApplication.class, args);
	}

}
