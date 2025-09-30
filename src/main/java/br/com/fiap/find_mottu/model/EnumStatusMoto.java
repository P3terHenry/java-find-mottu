package br.com.fiap.find_mottu.model;

import lombok.Getter;

@Getter
public enum EnumStatusMoto {

    INATIVA("Inativa"),
    ATIVA("Ativa"),
    MANUTENCAO("Manutenção"),
    RESERVADA("Reservada");

    private final String descricao;

    EnumStatusMoto(String descricao) {
        this.descricao = descricao;
    }

}
