package com.gearsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GearsyncBackendApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(GearsyncBackendApplication.class, args);
    }
}
