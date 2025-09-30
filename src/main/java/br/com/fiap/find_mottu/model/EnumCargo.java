package br.com.fiap.find_mottu.model;

import lombok.Getter;

@Getter
public enum EnumCargo {

    ADMIN("Administrador"),
    CEO("Chief Executive Officer"),
    GERENTE("Gerente"),
    COORDENADOR("Coordenador"),
    SUPERVISOR("Supervisor"),
    AUXILIAR("Auxiliar"),
    MECANICO("Mecânico"),
    ESTAGIARIO("Estagiário"),;

    private final String descricao;

    EnumCargo(String descricao) {
        this.descricao = descricao;
    }

}
