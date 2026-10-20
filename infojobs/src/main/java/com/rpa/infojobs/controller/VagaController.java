package com.rpa.infojobs.controller;


import com.rpa.infojobs.dto.response.PageResponse;
import com.rpa.infojobs.dto.response.VagaResponse;
import com.rpa.infojobs.model.Vaga;
import com.rpa.infojobs.security.SecurityUtils;
import com.rpa.infojobs.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaService vagaService;

    @GetMapping
    public ResponseEntity<PageResponse<VagaResponse>> listar(
            @RequestParam(required = false) Vaga.StatusVaga status,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Boolean candidatura,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataCaptura") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                vagaService.listar(userId, status, empresa, titulo, candidatura,
                        dataInicio, page, size, sortBy, direction)
        );
    }
}
