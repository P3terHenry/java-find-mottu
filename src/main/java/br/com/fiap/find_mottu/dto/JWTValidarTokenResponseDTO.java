package br.com.fiap.find_mottu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWTValidarTokenResponseDTO {

    private String token;
    private boolean statusToken;
    private String message;
}
