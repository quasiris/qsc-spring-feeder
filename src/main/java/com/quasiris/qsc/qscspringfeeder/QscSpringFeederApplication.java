package com.quasiris.qsc.qscspringfeeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class QscSpringFeederApplication implements ApplicationRunner {

    @Value("${app.url}")
    String url;
    @Value("${app.tenant}")
    String tenant;
    @Value("${app.feeding.code}")
    String feedingCode;
    @Value("${app.x-qsc-token}")
    String xQscToken;

    public static void main(String[] args) {
        SpringApplication.run(QscSpringFeederApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("url = {}", url);
        log.info("tenant = {}", tenant);
        log.info("feedingCode = {}", feedingCode);


    }
}