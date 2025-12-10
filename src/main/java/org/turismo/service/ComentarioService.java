package org.turismo.service;

import org.turismo.dto.ComentarioDTO;
import org.turismo.dto.RespostaComentarioDTO;
import org.turismo.model.Comentario;
import org.turismo.model.Usuario;
import org.turismo.repository.ComentarioRepository;
import org.turismo.repository.PontoTuristicoRepository;
import org.turismo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;

    public Comentario criar(Long pontoId, ComentarioDTO dto, String username) {
        if (!pontoRepository.existsById(pontoId)) {
            throw new RuntimeException("Ponto não encontrado");
        }

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comentario comentario = new Comentario();
        comentario.setPontoId(pontoId);
        comentario.setUsuarioId(usuario.getId());
        comentario.setUsuarioLogin(usuario.getLogin());
        comentario.setTexto(dto.getTexto());

        Comentario.Metadata metadata = new Comentario.Metadata();
        metadata.setDevice(dto.getDevice());
        metadata.setUserAgent(dto.getUserAgent());
        comentario.setMetadata(metadata);

        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listarPorPonto(Long pontoId) {
        return comentarioRepository.findByPontoIdOrderByCreatedAtDesc(pontoId);
    }

    public Comentario adicionarResposta(String comentarioId, RespostaComentarioDTO dto, String username) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comentario.Resposta resposta = new Comentario.Resposta();
        resposta.setUsuarioId(usuario.getId());
        resposta.setUsuarioLogin(usuario.getLogin());
        resposta.setTexto(dto.getTexto());

        comentario.getRespostas().add(resposta);
        return comentarioRepository.save(comentario);
    }

    public void deletar(String comentarioId, String username) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!comentario.getUsuarioId().equals(usuario.getId()) &&
                !usuario.getRole().equals(Usuario.Role.ADMIN)) {
            throw new RuntimeException("Sem permissão");
        }

        comentarioRepository.delete(comentario);
    }
}