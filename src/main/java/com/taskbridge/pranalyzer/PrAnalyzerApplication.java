package com.taskbridge.pranalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PrAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrAnalyzerApplication.class, args);
    }
}
