package com.rpa.infojobs.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vagas", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "url"}))
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String empresa;

    private String localizacao;
    private String salario;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private LocalDate dataPublicacao;

    @Column(nullable = false, length = 500)
    private String url;

    @CreationTimestamp
    private LocalDateTime dataCaptura;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVaga status = StatusVaga.PENDENTE;

    private Boolean candidaturaRealizada = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum StatusVaga {
        PENDENTE, PROCESSADA, CANDIDATADA, ERRO
    }
}