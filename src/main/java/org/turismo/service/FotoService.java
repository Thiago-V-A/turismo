package org.turismo.service;

import org.turismo.dto.FotoResponseDTO;
import org.turismo.model.Foto;
import org.turismo.model.Usuario;
import org.turismo.repository.FotoRepository;
import org.turismo.repository.PontoTuristicoRepository;
import org.turismo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FotoService {
    private final FotoRepository fotoRepository;
    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public FotoResponseDTO upload(Long pontoId, MultipartFile file, String titulo, String username) throws IOException {
        if (!pontoRepository.existsById(pontoId)) {
            throw new RuntimeException("Ponto não encontrado");
        }

        long totalFotos = fotoRepository.countByPontoId(pontoId);
        if (totalFotos >= 10) {
            throw new RuntimeException("Limite de 10 fotos atingido");
        }

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        String filepath = uploadDir + File.separator + filename;

        // Salvar arquivo
        file.transferTo(new File(filepath));

        // Salvar metadados no MongoDB
        Foto foto = new Foto();
        foto.setPontoId(pontoId);
        foto.setUsuarioId(usuario.getId());
        foto.setUsuarioLogin(usuario.getLogin());
        foto.setFilename(filename);
        foto.setTitulo(titulo);
        foto.setPath(filepath);
        foto.setContentType(file.getContentType());
        foto.setSize(file.getSize());

        foto = fotoRepository.save(foto);

        return toResponseDTO(foto);
    }

    public List<FotoResponseDTO> listarPorPonto(Long pontoId) {
        return fotoRepository.findByPontoIdOrderByCreatedAtDesc(pontoId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deletar(String fotoId, String username) {
        Foto foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new RuntimeException("Foto não encontrada"));

        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!foto.getUsuarioId().equals(usuario.getId()) &&
                !usuario.getRole().equals(Usuario.Role.ADMIN)) {
            throw new RuntimeException("Sem permissão");
        }

        // Deletar arquivo físico
        try {
            Files.deleteIfExists(Paths.get(foto.getPath()));
        } catch (IOException e) {
            // Log error
        }

        fotoRepository.delete(foto);
    }

    private FotoResponseDTO toResponseDTO(Foto foto) {
        return new FotoResponseDTO(
                foto.getId(),
                foto.getPontoId(),
                foto.getUsuarioLogin(),
                foto.getFilename(),
                foto.getTitulo(),
                "/uploads/" + foto.getFilename(),
                foto.getCreatedAt()
        );
    }
}

