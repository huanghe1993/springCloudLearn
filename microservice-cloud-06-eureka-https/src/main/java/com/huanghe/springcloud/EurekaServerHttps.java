package com.huanghe.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer //标识一个Eureka Server 服务注册中心
@SpringBootApplication
public class EurekaServerHttps {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerHttps.class, args);
    }
}
