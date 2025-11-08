package br.com.fiap.find_mottu.model;

import lombok.Getter;

@Getter
public enum EnumStatusUpload {

    SUCCESS("Sucesso"),
    ERROR("Erro");

    private final String descricao;

    EnumStatusUpload(String descricao) {
        this.descricao = descricao;
    }
}
