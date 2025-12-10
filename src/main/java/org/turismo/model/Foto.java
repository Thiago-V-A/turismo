package org.turismo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

    @Id
    private String id;

    private Long pontoId;

    private Long usuarioId;

    private String usuarioLogin;

    private String filename;

    private String titulo;

    private String path;

    private String contentType;

    private Long size;

    private LocalDateTime createdAt = LocalDateTime.now();
}
