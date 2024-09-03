package com.hasudo.hasudobatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@EnableConfigurationProperties
@SpringBootApplication
public class HasudoBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HasudoBatchApplication.class, args);
    }

}
