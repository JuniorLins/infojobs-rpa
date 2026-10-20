package com.rpa.infojobs.service;

import com.rpa.infojobs.dto.response.PageResponse;
import com.rpa.infojobs.dto.response.VagaResponse;
import com.rpa.infojobs.model.Vaga;
import com.rpa.infojobs.repository.VagaRepository;
import com.rpa.infojobs.repository.VagaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;

    public PageResponse<VagaResponse> listar(String userId,
                                             Vaga.StatusVaga status,
                                             String empresa,
                                             String titulo,
                                             Boolean candidatura,
                                             LocalDateTime dataInicio,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String direction) {

        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy == null || sortBy.isBlank() ? "dataCaptura" : sortBy
        );
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);

        Specification<Vaga> spec = Specification
                .where(VagaSpecification.byUser(userId))
                .and(VagaSpecification.byStatus(status))
                .and(VagaSpecification.byEmpresa(empresa))
                .and(VagaSpecification.byTitulo(titulo))
                .and(VagaSpecification.byCandidatura(candidatura))
                .and(VagaSpecification.capturedAfter(dataInicio));

        Page<Vaga> result = vagaRepository.findAll(spec, pageable);
        return PageResponse.from(result, VagaResponse::from);
    }

    public long countByUser(String userId) {
        return vagaRepository.countByUserId(userId);
    }

    public long countCandidaturas(String userId) {
        return vagaRepository.countByUserIdAndCandidaturaRealizada(userId, true);
    }
}
