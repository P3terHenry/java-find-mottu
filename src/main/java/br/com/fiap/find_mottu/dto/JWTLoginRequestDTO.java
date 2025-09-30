package br.com.fiap.find_mottu.dto;

import lombok.Getter;

@Getter
public class JWTLoginRequestDTO {

    private String email;
    private String senha;
}
