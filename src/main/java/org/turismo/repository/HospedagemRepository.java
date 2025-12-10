package org.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.turismo.model.Hospedagem;

import java.util.List;

public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {
    List<Hospedagem> findByPontoId(Long pontoId);
}
