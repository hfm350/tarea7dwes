package com.hfm350.tarea3dweshfm350;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Tarea3dweshfm350Application {
	
	@Bean
	public Principal applicationStarupRunner() {
		return new Principal();
	}

	public static void main(String[] args) {
		SpringApplication.run(Tarea3dweshfm350Application.class, args);
	}
	
	

}
