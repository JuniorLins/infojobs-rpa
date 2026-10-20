package com.rpa.infojobs.controller;

import com.rpa.infojobs.robot.InfojobsRobot;
import com.rpa.infojobs.robot.exception.RobotBusyException;
import com.rpa.infojobs.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/robot")
@RequiredArgsConstructor
@Slf4j
public class RobotController {

    private final InfojobsRobot infoJobsRobot;
    private final ThreadPoolTaskExecutor taskExecutor;

    @PostMapping("/executar")
    public ResponseEntity<Map<String, Object>> executarRobo() {
        String userId = SecurityUtils.getCurrentUserId();

        if (infoJobsRobot.isRunning()) {
            throw new RobotBusyException("Robô já está em execução. Aguarde o término.");
        }

        log.info("Requisição para executar robô para o usuário: {}", userId);

        taskExecutor.execute(() -> {
            try {
                infoJobsRobot.executarParaUsuario(userId);
            } catch (Exception e) {
                log.error("Erro ao executar robô para o usuário {}: {}", userId, e.getMessage(), e);
            }
        });

        return ResponseEntity.accepted().body(Map.of(
                "message", "Robô iniciado",
                "userId", userId,
                "startedAt", LocalDateTime.now()
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "running", infoJobsRobot.isRunning(),
                "timestamp", LocalDateTime.now()
        ));
    }
}