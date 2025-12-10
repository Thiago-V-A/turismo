package org.turismo.controller;

import org.turismo.dto.HospedagemDTO;
import org.turismo.model.Hospedagem;
import org.turismo.service.HospedagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pontos/{pontoId}/hospedagens")
@RequiredArgsConstructor
public class HospedagemController {
    private final HospedagemService hospedagemService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Hospedagem> criar(
            @PathVariable Long pontoId,
            @Valid @RequestBody HospedagemDTO dto) {
        return ResponseEntity.ok(hospedagemService.criar(pontoId, dto));
    }

    @GetMapping
    public ResponseEntity<List<Hospedagem>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(hospedagemService.listarPorPonto(pontoId));
    }
}
