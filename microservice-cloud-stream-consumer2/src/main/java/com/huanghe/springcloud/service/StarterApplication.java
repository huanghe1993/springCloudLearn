package com.huanghe.springcloud.service;

import com.huanghe.springcloud.service.config.MyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.huanghe.springcloud.service"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MyConfig.class})
        })
public class StarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);
    }
}

