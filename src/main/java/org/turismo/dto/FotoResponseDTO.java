package org.turismo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoResponseDTO {
    private String id;
    private Long pontoId;
    private String usuarioLogin;
    private String filename;
    private String titulo;
    private String url;
    private LocalDateTime createdAt;
}