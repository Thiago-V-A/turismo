package org.turismo.controller;

import org.turismo.dto.FotoResponseDTO;
import org.turismo.service.FotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pontos/{pontoId}/fotos")
@RequiredArgsConstructor
public class FotoController {
    private final FotoService fotoService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FotoResponseDTO> upload(
            @PathVariable Long pontoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String titulo,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return ResponseEntity.ok(fotoService.upload(pontoId, file, titulo, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<FotoResponseDTO>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(fotoService.listarPorPonto(pontoId));
    }

    @DeleteMapping("/{fotoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletar(
            @PathVariable String fotoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        fotoService.deletar(fotoId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
