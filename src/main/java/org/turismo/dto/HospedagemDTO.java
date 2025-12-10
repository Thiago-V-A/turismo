package org.turismo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.turismo.model.Hospedagem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospedagemDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String endereco;
    private String telefone;
    private BigDecimal precoMedio;
    private Hospedagem.TipoHospedagem tipo;
    private String linkReserva;
}