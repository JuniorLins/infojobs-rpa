package com.rpa.infojobs.repository;

import com.rpa.infojobs.model.Vaga;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VagaSpecification {

    private VagaSpecification() {}

    public static Specification<Vaga> byUser(String userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<Vaga> byStatus(Vaga.StatusVaga status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Vaga> byEmpresa(String empresa) {
        if (empresa == null || empresa.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("empresa")), "%" + empresa.toLowerCase() + "%");
    }

    public static Specification<Vaga> byTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
    }

    public static Specification<Vaga> byCandidatura(Boolean candidatura) {
        if (candidatura == null) return null;
        return (root, query, cb) -> cb.equal(root.get("candidaturaRealizada"), candidatura);
    }

    public static Specification<Vaga> capturedAfter(LocalDateTime data) {
        if (data == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataCaptura"), data);
    }
}
