package org.turismo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.turismo.model.Usuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String login;
    private String email;
    private Usuario.Role role;

    public AuthResponseDTO(String token, Long id, String login, String email, Usuario.Role role) {
        this.token = token;
        this.id = id;
        this.login = login;
        this.email = email;
        this.role = role;
    }
}