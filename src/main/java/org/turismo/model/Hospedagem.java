package org.turismo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "hospedagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ponto_id", nullable = false)
    private PontoTuristico ponto;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 300)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoMedio;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoHospedagem tipo;

    @Column(length = 500)
    private String linkReserva;

    public enum TipoHospedagem {
        HOTEL, POUSADA, HOSTEL, RESORT, APARTAMENTO
    }
}