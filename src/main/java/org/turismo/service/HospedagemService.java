package org.turismo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turismo.dto.HospedagemDTO;
import org.turismo.model.Hospedagem;
import org.turismo.model.PontoTuristico;
import org.turismo.repository.HospedagemRepository;
import org.turismo.repository.PontoTuristicoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospedagemService {
    private final HospedagemRepository hospedagemRepository;
    private final PontoTuristicoRepository pontoRepository;

    @Transactional
    public Hospedagem criar(Long pontoId, @Valid HospedagemDTO dto) {
        PontoTuristico ponto = pontoRepository.findById(pontoId)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado"));

        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setPonto(ponto);
        hospedagem.setNome(dto.getNome());
        hospedagem.setEndereco(dto.getEndereco());
        hospedagem.setTelefone(dto.getTelefone());
        hospedagem.setPrecoMedio(dto.getPrecoMedio());
        hospedagem.setTipo(dto.getTipo());
        hospedagem.setLinkReserva(dto.getLinkReserva());

        return hospedagemRepository.save(hospedagem);
    }

    public List<Hospedagem> listarPorPonto(Long pontoId) {
        return hospedagemRepository.findByPontoId(pontoId);
    }
}