package com.linyi.takeout;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
@MapperScan("com.linyi.takeout.mapper")
public class TakeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakeoutApplication.class, args);
        log.info("外卖程序已启动～～");
    }

}
