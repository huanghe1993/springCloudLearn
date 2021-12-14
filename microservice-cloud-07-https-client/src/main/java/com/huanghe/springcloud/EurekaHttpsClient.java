package com.huanghe.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient //将此服务注册到服务注册中心
@SpringBootApplication
public class EurekaHttpsClient {
    public static void main(String[] args) {
        SpringApplication.run(EurekaHttpsClient.class, args);
    }
}
