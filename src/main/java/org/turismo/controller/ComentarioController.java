package org.turismo.controller;

import org.turismo.dto.ComentarioDTO;
import org.turismo.dto.RespostaComentarioDTO;
import org.turismo.model.Comentario;
import org.turismo.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pontos/{pontoId}/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService comentarioService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comentario> criar(
            @PathVariable Long pontoId,
            @Valid @RequestBody ComentarioDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(comentarioService.criar(pontoId, dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Comentario>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(comentarioService.listarPorPonto(pontoId));
    }

    @DeleteMapping("/{comentarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletar(
            @PathVariable String comentarioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        comentarioService.deletar(comentarioId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
