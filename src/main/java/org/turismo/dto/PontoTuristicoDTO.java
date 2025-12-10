package org.turismo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    private String estado;
    private String pais = "Brasil";
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String endereco;
    private String comoChegar;
}
