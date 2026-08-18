package com.jian.hobbyadventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HobbyAdventureApplication {

	public static void main(String[] args) {
		SpringApplication.run(HobbyAdventureApplication.class, args);
	}

}
