package ru.ifmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class NavigatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NavigatorApplication.class, args);
    }
}
