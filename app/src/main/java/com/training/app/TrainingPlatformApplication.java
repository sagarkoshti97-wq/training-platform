package com.training.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.training")
@EntityScan(basePackages = "com.training")
@EnableJpaRepositories(basePackages = "com.training")
public class TrainingPlatformApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(TrainingPlatformApplication.class, args);
	}
}