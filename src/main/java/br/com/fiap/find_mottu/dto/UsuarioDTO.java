package br.com.fiap.find_mottu.dto;

import br.com.fiap.find_mottu.model.Cargo;
import br.com.fiap.find_mottu.model.EnumCargo;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Usuario;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;
    private String primeiroNome;
    private String sobrenome;
    private String email;
    private String senha;
    private EnumCargo cargo;
    private Integer idade;
    private Filial filial;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.primeiroNome = usuario.getPrimeiroNome();
        this.sobrenome = usuario.getSobrenome();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargos().stream().findFirst().map(Cargo::getNome).orElse(null);
        this.idade = usuario.getIdade();
        this.filial = usuario.getFilial();
    }
}
