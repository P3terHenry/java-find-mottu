package br.com.fiap.find_mottu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_MOTTU_FILIAIS")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FILIAL")
    @Schema(hidden = true)
    private Long id;

    @Column(name = "END_FILIAL", nullable = false)
    private String endereco;

    @OneToMany(mappedBy = "filial", fetch = FetchType.LAZY)
    @JsonIgnore
    @Schema(hidden = true)
    private List<Usuario> usuarios;
}
