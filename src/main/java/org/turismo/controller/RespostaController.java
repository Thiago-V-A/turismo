package org.turismo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.turismo.dto.RespostaComentarioDTO;
import org.turismo.model.Comentario;
import org.turismo.service.ComentarioService;

@RestController
@RequestMapping("/api/comentarios/{comentarioId}/respostas")
@RequiredArgsConstructor
class RespostaController {
    private final ComentarioService comentarioService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comentario> adicionarResposta(
            @PathVariable String comentarioId,
            @Valid @RequestBody RespostaComentarioDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(comentarioService.adicionarResposta(comentarioId, dto, userDetails.getUsername()));
    }
}

