package com.rpa.infojobs.repository;

import com.rpa.infojobs.model.Vaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, UUID>, JpaSpecificationExecutor<Vaga> {

    Optional<Vaga> findByUserIdAndUrl(String userId, String url);

    boolean existsByUserIdAndUrl(String userId, String url);

    long countByUserId(String userId);

    long countByUserIdAndCandidaturaRealizada(String userId, boolean realizada);

    Page<Vaga> findByUserId(String userId, Pageable pageable);
}
