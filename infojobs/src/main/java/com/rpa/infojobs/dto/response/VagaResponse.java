package com.rpa.infojobs.dto.response;

import com.rpa.infojobs.model.Vaga;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VagaResponse(
        UUID id,
        String titulo,
        String empresa,
        String localizacao,
        String salario,
        LocalDate dataPublicacao,
        String url,
        Vaga.StatusVaga status,
        Boolean candidaturaRealizada,
        LocalDateTime dataCaptura
) {

    public static VagaResponse from(Vaga v) {
        return new VagaResponse(
                v.getId(), v.getTitulo(), v.getEmpresa(), v.getLocalizacao(),
                v.getSalario(), v.getDataPublicacao(), v.getUrl(),
                v.getStatus(), v.getCandidaturaRealizada(), v.getDataCaptura()
        );
    }
}
