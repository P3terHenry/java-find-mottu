package br.com.fiap.find_mottu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_MOTTU_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank(message = "O primeiro nome é um campo obrigatório")
    @Column(name = "PRIMEIRO_NOME", nullable = false, length = 50)
    private String primeiroNome;

    @NotBlank(message = "O sobrenome é um campo obrigatório")
    @Column(name = "SOBRENOME", nullable = false, length = 50)
    private String sobrenome;

    @NotBlank(message = "O email é um campo obrigatório")
    @Email(message = "O email informado é inválido")
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @NotBlank(message = "A senha é um campo obrigatório")
    @Column(name = "SENHA", nullable = false, length = 60)
    private String senha;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_USUARIO_CARGO",
            joinColumns = @JoinColumn(name = "ID_USUARIO"),
            inverseJoinColumns = @JoinColumn(name = "ID_CARGO"))
    private Set<Cargo> cargos = new HashSet<Cargo>();

    @NotNull(message = "A idade é um campo obrigatório")
    @Min(value = 18, message = "A idade tem que ser no mínimo 18 anos")
    @Column(name = "IDADE", nullable = false)
    private Integer idade;

    @ManyToOne
    @JoinColumn(name = "ID_FILIAL", nullable = false)
    private Filial filial;
}
