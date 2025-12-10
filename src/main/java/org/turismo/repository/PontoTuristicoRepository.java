package org.turismo.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turismo.model.PontoTuristico;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PontoTuristicoRepository extends JpaRepository<PontoTuristico, Long> {
    Page<PontoTuristico> buscarPorTermo(String termo, Pageable pageable);

    Page<PontoTuristico> findByCidadeContainingIgnoreCase(String cidade, Pageable pageable);

    Optional<Object> findByNomeAndCidade(@NotBlank(message = "Nome é obrigatório") String nome, @NotBlank(message = "Cidade é obrigatória") String cidade);

    Page<PontoTuristico> findByEstadoContainingIgnoreCase(String estado, Pageable pageable);

    Page<PontoTuristico> findByNotaMediaGreaterThanEqual(BigDecimal notaMinima, Pageable pageable);
}
