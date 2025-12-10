package org.turismo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turismo.dto.PontoTuristicoDTO;
import org.turismo.dto.PontoTuristicoResponseDTO;
import org.turismo.model.PontoTuristico;
import org.turismo.model.Usuario;
import org.turismo.repository.ComentarioRepository;
import org.turismo.repository.FotoRepository;
import org.turismo.repository.UsuarioRepository;
import org.turismo.repository.PontoTuristicoRepository;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PontoTuristicoService {

    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FotoRepository fotoRepository;
    private final ComentarioRepository comentarioRepository;

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public PontoTuristicoResponseDTO criar(PontoTuristicoDTO dto, String username) {
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (pontoRepository.findByNomeAndCidade(dto.getNome(), dto.getCidade()).isPresent()) {
            throw new RuntimeException("Já existe um ponto turístico com este nome nesta cidade");
        }

        PontoTuristico ponto = new PontoTuristico();
        ponto.setNome(dto.getNome());
        ponto.setDescricao(dto.getDescricao());
        ponto.setCidade(dto.getCidade());
        ponto.setEstado(dto.getEstado());
        ponto.setPais(dto.getPais());
        ponto.setLatitude(dto.getLatitude());
        ponto.setLongitude(dto.getLongitude());
        ponto.setEndereco(dto.getEndereco());
        ponto.setComoChegar(dto.getComoChegar());
        ponto.setCriadoPor(usuario);

        ponto = pontoRepository.save(ponto);
        return toResponseDTO(ponto);
    }

    @Cacheable(value = "pontos", key = "#id")
    public PontoTuristicoResponseDTO buscarPorId(Long id) {
        PontoTuristico ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));
        return toResponseDTO(ponto);
    }

    public Page<PontoTuristicoResponseDTO> listar(
            String cidade,
            String estado,
            BigDecimal notaMinima,
            String termo,
            Pageable pageable) {

        Page<PontoTuristico> pontos;

        if (termo != null && !termo.isBlank()) {
            pontos = pontoRepository.buscarPorTermo(termo, pageable);
        } else if (cidade != null && !cidade.isBlank()) {
            pontos = pontoRepository.findByCidadeContainingIgnoreCase(cidade, pageable);
        } else if (estado != null && !estado.isBlank()) {
            pontos = pontoRepository.findByEstadoContainingIgnoreCase(estado, pageable);
        } else if (notaMinima != null) {
            pontos = pontoRepository.findByNotaMediaGreaterThanEqual(notaMinima, pageable);
        } else {
            pontos = pontoRepository.findAll(pageable);
        }

        return pontos.map(this::toResponseDTO);
    }

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public PontoTuristicoResponseDTO atualizar(Long id, PontoTuristicoDTO dto, String username) {
        PontoTuristico ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!ponto.getCriadoPor().getId().equals(usuario.getId()) &&
                !usuario.getRole().equals(Usuario.Role.ADMIN)) {
            throw new RuntimeException("Sem permissão para editar este ponto");
        }

        ponto.setNome(dto.getNome());
        ponto.setDescricao(dto.getDescricao());
        ponto.setCidade(dto.getCidade());
        ponto.setEstado(dto.getEstado());
        ponto.setPais(dto.getPais());
        ponto.setLatitude(dto.getLatitude());
        ponto.setLongitude(dto.getLongitude());
        ponto.setEndereco(dto.getEndereco());
        ponto.setComoChegar(dto.getComoChegar());

        ponto = pontoRepository.save(ponto);
        return toResponseDTO(ponto);
    }

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public void deletar(Long id, String username) {
        PontoTuristico ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getRole().equals(Usuario.Role.ADMIN)) {
            throw new RuntimeException("Apenas administradores podem deletar pontos");
        }

        pontoRepository.delete(ponto);
    }

    public List<PontoTuristicoResponseDTO> listarMaisPopulares() {
        return pontoRepository.findTop10ByOrderByNotaMediaDesc()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private PontoTuristicoResponseDTO toResponseDTO(PontoTuristico ponto) {
        int totalFotos = (int) fotoRepository.countByPontoId(ponto.getId());
        int totalComentarios = (int) comentarioRepository.countByPontoId(ponto.getId());

        return new PontoTuristicoResponseDTO(
                ponto.getId(),
                ponto.getNome(),
                ponto.getDescricao(),
                ponto.getCidade(),
                ponto.getEstado(),
                ponto.getPais(),
                ponto.getLatitude(),
                ponto.getLongitude(),
                ponto.getEndereco(),
                ponto.getComoChegar(),
                ponto.getNotaMedia(),
                ponto.getTotalAvaliacoes(),
                ponto.getCriadoPor() != null ? ponto.getCriadoPor().getLogin() : null,
                ponto.getCreatedAt(),
                totalFotos,
                totalComentarios
        );
    }
}
