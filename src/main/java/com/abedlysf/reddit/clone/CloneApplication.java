package com.abedlysf.reddit.clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication //  same as @Configuration @EnableAutoConfiguration @ComponentScan
@EnableAsync // enable acync processing to speed up the app
// (specially when the app connects to a mail server or
// such a process that takes much time )

@EntityScan(basePackages = {"com.abdelysf.reddit.clone.model"})
public class CloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloneApplication.class, args);
    }

}
