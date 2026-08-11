package com.nicico.internal.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.nicico"})
@EnableConfigurationProperties()
@EnableFeignClients
public class SalesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SalesApplication.class, args);
	}
}
