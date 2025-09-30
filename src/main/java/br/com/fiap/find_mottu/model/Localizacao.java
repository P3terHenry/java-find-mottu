package br.com.fiap.find_mottu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "T_MOTTU_LOCALIZACOES")
public class Localizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOCALIZACAO")
    private Long id;

    @NotNull
    @Column(name = "ID_IMAGEM", nullable = false)
    private String idImagem;

    @NotNull
    @Column(name = "POS_X", nullable = false, precision = 4, scale = 3)
    private BigDecimal posX;

    @NotNull
    @Column(name = "POS_Y", nullable = false, precision = 4, scale = 3)
    private BigDecimal posY;

    @NotNull
    @Column(name = "STATUS_LOCALIZACAO", nullable = false, length = 1)
    private Integer statusLocalizacao;

    @NotNull
    @Column(name = "DATA_LOCALIZACAO", nullable = false)
    private LocalDateTime dataLocalizacao;

    @ManyToOne
    @JoinColumn(name = "ID_MOTO")
    private Moto moto;
}
