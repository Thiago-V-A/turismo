package org.turismo.controller;

import org.turismo.dto.PontoTuristicoDTO;
import org.turismo.dto.PontoTuristicoResponseDTO;
import org.turismo.service.ExportService;
import org.turismo.service.PontoTuristicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoTuristicoController {
    private final PontoTuristicoService pontoService;
    private final ExportService exportService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PontoTuristicoResponseDTO> criar(
            @Valid @RequestBody PontoTuristicoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(pontoService.criar(dto, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoTuristicoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pontoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<PontoTuristicoResponseDTO>> listar(
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) BigDecimal notaMinima,
            @RequestParam(required = false) String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(pontoService.listar(cidade, estado, notaMinima, termo, pageable));
    }

    @GetMapping("/populares")
    public ResponseEntity<List<PontoTuristicoResponseDTO>> listarPopulares() {
        return ResponseEntity.ok(pontoService.listarMaisPopulares());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PontoTuristicoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PontoTuristicoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(pontoService.atualizar(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        pontoService.deletar(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportar(@RequestParam String format) throws IOException {
        String content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "json":
                content = exportService.exportarJSON();
                filename = "pontos-turisticos.json";
                mediaType = MediaType.APPLICATION_JSON;
                break;
            case "csv":
                content = exportService.exportarCSV();
                filename = "pontos-turisticos.csv";
                mediaType = MediaType.parseMediaType("text/csv");
                break;
            case "xml":
                content = exportService.exportarXML();
                filename = "pontos-turisticos.xml";
                mediaType = MediaType.APPLICATION_XML;
                break;
            default:
                return ResponseEntity.badRequest().body("Formato inválido. Use: json, csv ou xml");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }
}
