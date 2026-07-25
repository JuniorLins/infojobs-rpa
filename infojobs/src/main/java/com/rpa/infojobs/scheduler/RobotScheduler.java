package com.rpa.infojobs.scheduler;

import com.rpa.infojobs.robot.InfojobsRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class RobotScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RobotScheduler.class);

    @Autowired
    private InfojobsRobot infojobsRobot;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 0 8,12,18 * * *")
    public void executarRoboAgendado() {
        logger.info("========================================");
        logger.info("INICIANDO EXECUÇÃO AGENDADA DO ROBÔ");
        logger.info("Data/Hora: {}", LocalDateTime.now().format(formatter));
        logger.info("========================================");

        try {
            infojobsRobot.executar();
            logger.info("EXECUÇÃO AGENDADA CONCLUÍDA COM SUCESSO!");
        } catch (Exception e) {
            logger.error("ERRO NA EXECUÇÃO AGENDADA: {}", e.getMessage(), e);
        }
    }
}
