package br.com.fiap.find_mottu.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_MOTTU_MOTOS")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MOTO")
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "O ID do QR Code é um campo obrigatório")
    @Column(name = "ID_QR_CODE")
    private String idQrCode;

    @NotNull(message = "O ID do IMEI é um campo obrigatório")
    @Min(value = 100000000000000L, message = "O IMEI deve ter 15 dígitos")
    @Max(value = 999999999999999L, message = "O IMEI deve ter 15 dígitos")
    @Column(name = "ID_IMEI")
    private Long idImei;

    @NotBlank(message = "O número do chassi é um campo obrigatório")
    @Size(min = 17, max = 17, message = "O número do chassi deve ter exatamente 17 caracteres")
    @Column(name = "NUM_CHASSI")
    private String numChassi;

    @NotNull(message = "O número do motor é um campo obrigatório")
    @Min(value = 1, message = "O número do motor deve ser um valor positivo")
    @Column(name = "NUM_MOTOR")
    private Long numMotor;

    @NotBlank(message = "O modelo da moto é um campo obrigatório")
    @Column(name = "MODELO_MOTO")
    private String modeloMoto;

    @NotBlank(message = "A placa da moto é um campo obrigatório")
    @Column(name = "PLACA_MOTO")
    private String placaMoto;

    @NotNull(message = "O status da moto é um campo obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_MOTO", nullable = false)
    private EnumStatusMoto statusMoto;

    @NotNull(message = "A filial é um campo obrigatório")
    @ManyToOne
    @JoinColumn(name = "ID_FILIAL", nullable = false)
    private Filial filial;

    @OneToMany(mappedBy = "moto", fetch = FetchType.LAZY)
    @OrderBy("dataLocalizacao DESC")
    private List<Localizacao> localizacoes;

}
