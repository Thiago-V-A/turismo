package org.turismo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turismo.dto.AvaliacaoDTO;
import org.turismo.model.Avaliacao;
import org.turismo.model.PontoTuristico;
import org.turismo.model.Usuario;
import org.turismo.repository.AvaliacaoRepository;
import org.turismo.repository.PontoTuristicoRepository;
import org.turismo.repository.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @CacheEvict(value = "pontos", key = "#pontoId")
    public Avaliacao criar(Long pontoId, AvaliacaoDTO dto, String username) {
        PontoTuristico ponto = pontoRepository.findById(pontoId)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (avaliacaoRepository.existsByPontoIdAndUsuarioId(pontoId, usuario.getId())) {
            throw new RuntimeException("Você já avaliou este ponto. Use PUT para atualizar.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPonto(ponto);
        avaliacao.setUsuario(usuario);
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        avaliacao = avaliacaoRepository.save(avaliacao);

        // Atualizar nota média do ponto
        ponto.calcularNotaMedia();
        pontoRepository.save(ponto);

        return avaliacao;
    }

    @Transactional
    @CacheEvict(value = "pontos", key = "#pontoId")
    public Avaliacao atualizar(Long pontoId, AvaliacaoDTO dto, String username) {
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Avaliacao avaliacao = avaliacaoRepository.findByPontoIdAndUsuarioId(pontoId, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        avaliacao = avaliacaoRepository.save(avaliacao);

        // Atualizar nota média do ponto
        PontoTuristico ponto = avaliacao.getPonto();
        ponto.calcularNotaMedia();
        pontoRepository.save(ponto);

        return avaliacao;
    }

    public List<Avaliacao> listarPorPonto(Long pontoId) {
        return avaliacaoRepository.findByPontoId(pontoId);
    }

    @Transactional
    @CacheEvict(value = "pontos", key = "#pontoId")
    public void deletar(Long pontoId, Long avaliacaoId, String username) {
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        if (!avaliacao.getUsuario().getId().equals(usuario.getId()) &&
                !usuario.getRole().equals(Usuario.Role.ADMIN)) {
            throw new RuntimeException("Sem permissão para deletar esta avaliação");
        }

        avaliacaoRepository.delete(avaliacao);

        // Atualizar nota média do ponto
        PontoTuristico ponto = avaliacao.getPonto();
        ponto.calcularNotaMedia();
        pontoRepository.save(ponto);
    }
}