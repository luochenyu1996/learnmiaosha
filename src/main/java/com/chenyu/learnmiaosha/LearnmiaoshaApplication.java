package com.chenyu.learnmiaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chenyu.learnmiaosha.mapper")
public class LearnmiaoshaApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearnmiaoshaApplication.class, args);
    }

}
