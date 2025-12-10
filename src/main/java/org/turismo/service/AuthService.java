package org.turismo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turismo.dto.AuthResponseDTO;
import org.turismo.dto.LoginDTO;
import org.turismo.dto.RegisterDTO;
import org.turismo.model.Usuario;
import org.turismo.repository.UsuarioRepository;
import org.turismo.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponseDTO register(RegisterDTO dto) {
        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new RuntimeException("Login já está em uso");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(dto.getLogin());
        usuario.setEmail(dto.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        usuario.setRole(dto.getRole());

        usuario = usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getLogin());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                usuario.getId(),
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public AuthResponseDTO login(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getSenha())
        );

        Usuario usuario = usuarioRepository.findByLogin(dto.getLogin())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getLogin());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                usuario.getId(),
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}