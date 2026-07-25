package com.rpa.infojobs.repository;

import com.rpa.infojobs.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, UUID> {
    Optional<Vaga> findByUrl(String url);
    boolean existsByUrl(String url);
    List<Vaga> findByEmpresaContainingIgnoreCase(String empresa);
    List<Vaga> findByStatus(Vaga.StatusVaga status);

    @Query("SELECT v FROM Vaga v WHERE v.dataCaptura BETWEEN :inicio AND :fim")
    List<Vaga> buscarPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT v.empresa, COUNT(v) FROM Vaga v GROUP BY v.empresa ORDER BY COUNT(v) DESC")
    List<Object[]> contarVagasPorEmpresa();

    long countByCandidaturaRealizada(boolean realizada);
}
