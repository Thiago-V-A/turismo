package org.turismo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.turismo.model.Foto;

import java.util.List;

@Repository
public interface FotoRepository extends MongoRepository<Foto, String> {

    List<Foto> findByPontoIdOrderByCreatedAtDesc(Long pontoId);

    List<Foto> findByUsuarioId(Long usuarioId);

    long countByPontoId(Long pontoId);
}