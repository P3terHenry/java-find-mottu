package br.com.fiap.find_mottu.dto;

import br.com.fiap.find_mottu.model.EnumStatusMoto;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.model.Moto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MotoDTO {

    private Long id;
    private String idQrCode;
    private Long idImei;
    private String numChassi;
    private Long numMotor;
    private String modeloMoto;
    private String placaMoto;
    private EnumStatusMoto statusMoto;
    private FilialDTO filial;

    public MotoDTO(Moto moto) {
        this(moto, List.of());
    }

    public MotoDTO(Moto moto, List<Localizacao> localizacoes) {
        this.id = moto.getId();
        this.idQrCode = moto.getIdQrCode();
        this.idImei = moto.getIdImei();
        this.numChassi = moto.getNumChassi();
        this.numMotor = moto.getNumMotor();
        this.modeloMoto = moto.getModeloMoto();
        this.placaMoto = moto.getPlacaMoto();
        this.statusMoto = moto.getStatusMoto();
        this.filial = new FilialDTO(moto.getFilial());
    }

    public MotoDTO(Long id, String idQrCode, Long idImei, String numChassi, Long numMotor,
                   String modeloMoto, String placaMoto, EnumStatusMoto statusMoto, Filial filial) {
        this.id = id;
        this.idQrCode = idQrCode;
        this.idImei = idImei;
        this.numChassi = numChassi;
        this.numMotor = numMotor;
        this.modeloMoto = modeloMoto;
        this.placaMoto = placaMoto;
        this.statusMoto = statusMoto;
        this.filial = new FilialDTO(filial);
    }

}
