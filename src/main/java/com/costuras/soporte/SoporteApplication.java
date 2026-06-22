package com.costuras.soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class SoporteApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoporteApplication.class, args);
    }
}
