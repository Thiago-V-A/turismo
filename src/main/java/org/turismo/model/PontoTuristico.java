package org.turismo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pontos_turisticos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "cidade"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 200)
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Cidade é obrigatória")
    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(length = 50)
    private String estado;

    @Column(length = 50)
    private String pais = "Brasil";

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 300)
    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String comoChegar;

    @Column(precision = 3, scale = 2)
    private BigDecimal notaMedia = BigDecimal.ZERO;

    @Column
    private Integer totalAvaliacoes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Usuario criadoPor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ponto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hospedagem> hospedagens = new ArrayList<>();

    @OneToMany(mappedBy = "ponto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    public void calcularNotaMedia() {
        if (avaliacoes.isEmpty()) {
            this.notaMedia = BigDecimal.ZERO;
            this.totalAvaliacoes = 0;
            return;
        }

        double soma = avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .sum();

        this.notaMedia = BigDecimal.valueOf(soma / avaliacoes.size())
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalAvaliacoes = avaliacoes.size();
    }
}
