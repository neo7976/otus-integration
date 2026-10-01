package ru.dsobin.otus.spring.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
public class OtusSpringIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtusSpringIntegrationApplication.class, args);
    }
}
