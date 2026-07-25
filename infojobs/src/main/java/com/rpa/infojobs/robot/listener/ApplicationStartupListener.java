package com.rpa.infojobs.robot.listener;

import com.rpa.infojobs.robot.InfojobsRobot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {

    private final InfojobsRobot infojobsRobot;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Aplicação totalmente carregada. Executando robô pela primeira vez...");
        try {
            String userId = "9340322a-7876-4f24-8187-e87c1b631636"; // Substitua pelo seu UUID
            log.info("Executando robô para o usuário com ID: {}", userId);
            infojobsRobot.executarParaUsuario(userId);
        } catch (Exception e) {
            log.error("Erro ao executar robô na inicialização", e);
        }
    }
}
