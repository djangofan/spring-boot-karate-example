package com.standard.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class WmbTestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmbTestServiceApplication.class, args);
    }

}
