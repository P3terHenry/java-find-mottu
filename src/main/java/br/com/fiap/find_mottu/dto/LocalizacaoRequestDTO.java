package br.com.fiap.find_mottu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LocalizacaoRequestDTO {

    @NotNull
    private Long idMoto;
    @NotBlank
    private String idImagem;
    @NotNull
    private BigDecimal posX;
    @NotNull
    private BigDecimal posY;
    @NotNull
    private Integer statusLocalizacao;
    @NotNull
    private LocalDateTime dataLocalizacao;
}
