package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI usersMicroserviceOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("User Management API")
				.description("API for managing users and their addresses")
				.version("1.0"));
	}
}