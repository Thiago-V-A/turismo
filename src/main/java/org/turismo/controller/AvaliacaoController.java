package org.turismo.controller;

import org.turismo.dto.AvaliacaoDTO;
import org.turismo.model.Avaliacao;
import org.turismo.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pontos/{pontoId}/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Avaliacao> criar(
            @PathVariable Long pontoId,
            @Valid @RequestBody AvaliacaoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(avaliacaoService.criar(pontoId, dto, userDetails.getUsername()));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Avaliacao> atualizar(
            @PathVariable Long pontoId,
            @Valid @RequestBody AvaliacaoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(avaliacaoService.atualizar(pontoId, dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Avaliacao>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(avaliacaoService.listarPorPonto(pontoId));
    }

    @DeleteMapping("/{avaliacaoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletar(
            @PathVariable Long pontoId,
            @PathVariable Long avaliacaoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        avaliacaoService.deletar(pontoId, avaliacaoId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
