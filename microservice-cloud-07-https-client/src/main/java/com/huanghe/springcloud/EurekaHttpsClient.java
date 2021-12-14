package com.huanghe.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * https://blog.csdn.net/qq_32238611/article/details/121568007
 * https://www.codesheep.cn/2018/12/10/eureka-server-https/
 * https://juejin.cn/post/6844903734804217864
 * https://zhuanlan.zhihu.com/p/341857389
 */
@EnableEurekaClient //将此服务注册到服务注册中心
@SpringBootApplication
public class EurekaHttpsClient {
    public static void main(String[] args) {
        SpringApplication.run(EurekaHttpsClient.class, args);
    }
}
