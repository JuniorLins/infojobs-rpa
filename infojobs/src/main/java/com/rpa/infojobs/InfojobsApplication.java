package com.rpa.infojobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfojobsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfojobsApplication.class, args);
        System.out.println("Aplicação iniciada com SCHEDULER habilitado!");
    }
}