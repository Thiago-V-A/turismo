package org.turismo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    @Id
    private String id;

    private Long pontoId;

    private Long usuarioId;

    private String usuarioLogin;

    private String texto;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Metadata metadata;

    private List<Resposta> respostas = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private String language = "pt";
        private String device;
        private String userAgent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resposta {
        private Long usuarioId;
        private String usuarioLogin;
        private String texto;
        private LocalDateTime data = LocalDateTime.now();
    }
}
