package com.rpa.infojobs.controller;


import com.rpa.infojobs.robot.InfojobsRobot;
import com.rpa.infojobs.security.SecurityUtils;
import com.rpa.infojobs.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VagaService vagaService;
    private final InfojobsRobot infojobsRobot;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(Map.of(
                "totalVagas", vagaService.countByUser(userId),
                "totalCandidaturas", vagaService.countCandidaturas(userId),
                "robotRunning", infojobsRobot.isRunning(),
                "timestamp", LocalDateTime.now()
        ));
    }
}
