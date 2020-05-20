package com.cartravel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zxc
 */
@SpringBootApplication
public class OrderMonitorApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderMonitorApp.class, args);
    }
}
