package com.alzion.sharingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.alzion.sharingapp.model")  // Ensure the package is scanned
public class SharingappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharingappApplication.class, args);
	}

}
