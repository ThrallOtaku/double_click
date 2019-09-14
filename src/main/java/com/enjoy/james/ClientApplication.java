package com.enjoy.james;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
* springboot程序启动入口，启动时包括了内置的tomcat容器
* @author 【享学课堂】 James老师QQ：1076258117 架构技术QQ群：684504192   
* @throws Exception
*/
@SpringBootApplication
@MapperScan(basePackages = "com.enjoy.james.dao")
public class ClientApplication  {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
