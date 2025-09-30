package br.com.fiap.find_mottu.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_MOTTU_CARGO")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CARGO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOME_CARGO")
    private EnumCargo nome;
}
