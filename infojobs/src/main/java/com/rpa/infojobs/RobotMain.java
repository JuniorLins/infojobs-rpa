package com.rpa.infojobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.rpa.infojobs")
public class RobotMain {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando robô com SCHEDULER...");

        SpringApplication.run(RobotMain.class, args);

        System.out.println("Aplicação iniciada. Robô será executado automaticamente via scheduler.");
        System.out.println("   - 08:00, 12:00 e 18:00");
        System.out.println("   - E também na inicialização via ApplicationReadyEvent");
        System.out.println("\nPressione Ctrl+C para encerrar.");

        Thread.currentThread().join();
    }
}