package org.turismo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String cidade;
    private String estado;
    private String pais;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String endereco;
    private String comoChegar;
    private BigDecimal notaMedia;
    private Integer totalAvaliacoes;
    private String criadoPor;
    private LocalDateTime createdAt;
    private Integer totalFotos;
    private Integer totalComentarios;
}