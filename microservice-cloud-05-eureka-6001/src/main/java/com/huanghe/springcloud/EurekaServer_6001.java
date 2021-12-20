package com.huanghe.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: 梦学谷
 */
@SpringBootApplication
@EnableEurekaServer //标识一个Eureka Server 服务注册中心
@ComponentScan("com.huanghe.springcloud")
public class EurekaServer_6001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer_6001.class, args);
    }
}
