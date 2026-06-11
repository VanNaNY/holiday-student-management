package com.tyut.holiday;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 假期学生管理系统 后端启动类。
 */
@SpringBootApplication
@MapperScan("com.tyut.holiday.mapper")
public class HolidayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayApplication.class, args);
    }
}
