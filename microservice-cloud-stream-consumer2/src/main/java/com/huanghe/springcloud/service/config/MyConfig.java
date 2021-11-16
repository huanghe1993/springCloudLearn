package com.huanghe.springcloud.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    public MyConfig(){
        System.out.println("myConfig");
    }

    @Bean
    public MyConfig genMyConfig(){
        System.out.println("demo mydemo");
        return new MyConfig();
    }
}
