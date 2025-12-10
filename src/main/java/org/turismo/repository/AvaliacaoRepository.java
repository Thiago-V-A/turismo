package org.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turismo.model.Avaliacao;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByPontoId(Long pontoId);

    Optional<Avaliacao> findByPontoIdAndUsuarioId(Long pontoId, Long usuarioId);

    boolean existsByPontoIdAndUsuarioId(Long pontoId, Long usuarioId);
}
