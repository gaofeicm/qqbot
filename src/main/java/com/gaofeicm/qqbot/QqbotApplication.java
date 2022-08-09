package com.gaofeicm.qqbot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.gaofeicm.qqbot.dao"})
public class QqbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(QqbotApplication.class, args);
    }

}
