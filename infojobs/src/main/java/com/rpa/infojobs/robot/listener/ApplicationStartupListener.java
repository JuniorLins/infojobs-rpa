package com.rpa.infojobs.robot.listener;

import com.rpa.infojobs.robot.InfojobsRobot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {

    private final InfojobsRobot infojobsRobot;

    @Value("${robot.auto-start-on-boot:false}")
    private boolean autoStartOnBoot;

    @Value("${robot.auto-start-user-id:}")
    private String autoStartUserId;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!autoStartOnBoot) {
            log.info("Auto-start do robô desabilitado. Ignorando execução no boot.");
            return;
        }
        if (autoStartUserId == null || autoStartUserId.isBlank()) {
            log.warn("Auto-start habilitado, mas 'robot.auto-start-user-id' não configurado. Ignorando.");
            return;
        }
        log.info("Executando robô no boot para o usuário: {}", autoStartUserId);
        try {
            infojobsRobot.executarParaUsuario(autoStartUserId);
        } catch (Exception e) {
            log.error("Erro ao executar robô na inicialização", e);
        }
    }
}
