package org.turismo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.turismo.model.Comentario;

import java.util.List;

@Repository
public interface ComentarioRepository extends MongoRepository<Comentario, String> {

    List<Comentario> findByPontoIdOrderByCreatedAtDesc(Long pontoId);

    List<Comentario> findByUsuarioId(Long usuarioId);

    long countByPontoId(Long pontoId);
}
