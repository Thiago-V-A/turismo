package org.turismo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaComentarioDTO {

    @NotBlank(message = "Texto é obrigatório")
    @Size(max = 300, message = "Resposta deve ter no máximo 300 caracteres")
    private String texto;
}
