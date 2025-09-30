package br.com.fiap.find_mottu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class JWTLoginResponseDTO {

    private String token;
    private String subject;
    private Date IssuedAt;
    private Date expiration;
}
