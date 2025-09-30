package br.com.fiap.find_mottu.dto;

import br.com.fiap.find_mottu.model.Localizacao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LocalizacaoDTO {

    private Long id;
    private Long idMoto;
    private String idImagem;
    private BigDecimal posX;
    private BigDecimal posY;
    private Integer statusLocalizacao;
    private LocalDateTime dataLocalizacao;

    public LocalizacaoDTO(Localizacao localizacao) {
        this.id = localizacao.getId();
        this.idMoto = localizacao.getMoto().getId();
        this.idImagem = localizacao.getIdImagem();
        this.posX = localizacao.getPosX();
        this.posY = localizacao.getPosY();
        this.statusLocalizacao = localizacao.getStatusLocalizacao();
        this.dataLocalizacao = localizacao.getDataLocalizacao();
    }

    public LocalizacaoDTO(Long id, Long idMoto, String idImagem, BigDecimal posX, BigDecimal posY, Integer statusLocalizacao, LocalDateTime dataLocalizacao) {
        super();
        this.id = id;
        this.idMoto = idMoto;
        this.idImagem = idImagem;
        this.posX = posX;
        this.posY = posY;
        this.statusLocalizacao = statusLocalizacao;
        this.dataLocalizacao = dataLocalizacao;
    }
}