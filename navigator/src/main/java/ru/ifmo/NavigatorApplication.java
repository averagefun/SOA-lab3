package ru.ifmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;

@SpringBootApplication(exclude = {
        ConsulAutoServiceRegistrationAutoConfiguration.class,
        ConsulServiceRegistryAutoConfiguration.class
})@EnableDiscoveryClient
public class NavigatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NavigatorApplication.class, args);
    }
}
