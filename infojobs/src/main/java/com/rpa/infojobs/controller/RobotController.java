package com.rpa.infojobs.controller;

import com.rpa.infojobs.robot.InfojobsRobot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/robot")
@RequiredArgsConstructor
@Slf4j
public class RobotController {

    private final InfojobsRobot infoJobsRobot;
    private final ThreadPoolTaskExecutor taskExecutor;

    @PostMapping("/executar")
    public ResponseEntity<String> executarRobo(@RequestParam String userId) {
        log.info("Requisição para executar robô para o usuário: {}", userId);

        taskExecutor.execute(() -> {
            try {
                infoJobsRobot.executarParaUsuario(userId);
                log.info("Robô executado com sucesso para o usuário: {}", userId);
            } catch (Exception e) {
                log.error("Erro ao executar robô para o usuário {}: {}", userId, e.getMessage());
            }
        });

        return ResponseEntity.accepted()
                .body("Robô iniciado em background para o usuário: " + userId);
    }
}