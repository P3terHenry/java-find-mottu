package br.com.fiap.find_mottu.dto;

import br.com.fiap.find_mottu.model.Filial;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilialDTO {

    private Long id;
    private String endereco;


    public FilialDTO(Filial filial) {
        this.id = filial.getId();
        this.endereco = filial.getEndereco();
    }

    public FilialDTO(Long id, String endereco) {
        this.id = id;
        this.endereco = endereco;
    }

}