package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/register", "/register/**", "/css/**", "/js/**").permitAll()
				.requestMatchers("/swagger-ui/**").hasRole("ADMIN")
				.requestMatchers("/api/reports").permitAll()
				.anyRequest()
				.authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/users")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/login")
				.permitAll()
			);
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
