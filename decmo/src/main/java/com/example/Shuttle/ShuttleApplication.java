package com.example.Shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {

		"com.example.restcontroller",
		"com.example.token",
		"com.example.service",
		"com.example.security"

})
// Entity 위치 설정
@EntityScan(basePackages = { "com.example.entity" })

// 저장소 위치 설정
@EnableJpaRepositories(basePackages = { "com.example.repository" })

public class ShuttleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleApplication.class, args);
	}
}