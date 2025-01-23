package com.example.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@EnableTask
@SpringBootApplication
public class ExcelTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelTaskApplication.class, args);
    }

}
