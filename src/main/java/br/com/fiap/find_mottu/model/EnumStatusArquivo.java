package br.com.fiap.find_mottu.model;

import lombok.Getter;

@Getter
public enum EnumStatusArquivo {

    EXCLUIDO("Excluído"),
    ATIVO("Ativo");

    private final String descricao;

    EnumStatusArquivo(String descricao) {
        this.descricao = descricao;
    }
}
