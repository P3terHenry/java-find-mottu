package br.com.fiap.find_mottu.repository;

import br.com.fiap.find_mottu.dto.MotoDTO;
import br.com.fiap.find_mottu.model.EnumStatusMoto;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.projection.MotoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotoRepository extends JpaRepository<Moto, Long> {

    @Query("SELECT new br.com.fiap.find_mottu.dto.MotoDTO(m.id, m.idQrCode, m.idImei, m.numChassi, m.numMotor, m.modeloMoto, m.placaMoto, m.statusMoto, m.filial) FROM Moto m")
    List<MotoDTO> findAllMotos();

    // Busca todas as motos por status usando enum
    List<Moto> findByStatusMoto(EnumStatusMoto statusMoto);

    @Query(value = "SELECT * FROM T_MOTTU_MOTOS WHERE ID_FILIAL = :id", nativeQuery = true)
    List<Moto> findByMotosInFilial(@Param("id") Long id);

    // Consulta com projection para retornar apenas modelo + status
    @Query(value = "SELECT MODELO_MOTO as modeloMoto, STATUS_MOTO as statusMoto FROM T_MOTTU_MOTOS WHERE STATUS_MOTO = :status", nativeQuery = true)
    List<MotoProjection> buscarModelosPorStatus(@Param("status") String status);

    // Busca por filial
    List<Moto> findByFilialId(Long filialId);

    // Busca por placa específica (para validação de duplicidade)
    Optional<Moto> findByPlacaMoto(String placaMoto);

    // Busca por QR Code específico (para validação de duplicidade)
    Optional<Moto> findByIdQrCode(String idQrCode);

    //Contar motos por filial
    @Query("SELECT COUNT(m) FROM Moto m WHERE m.filial.id = :filialId")
    long countByFilialId(@Param("filialId") Long filialId);

    // Busca por filial E status combinados usando enum
    @Query("SELECT m FROM Moto m WHERE m.filial.id = :filialId AND m.statusMoto = :statusMoto")
    List<Moto> findByFilialIdAndStatusMoto(@Param("filialId") Long filialId, @Param("statusMoto") EnumStatusMoto statusMoto);

    // Busca por termo (placa, modelo ou chassi) - busca textual
    @Query("SELECT m FROM Moto m WHERE " +
           "LOWER(m.placaMoto) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.modeloMoto) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.numChassi) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Moto> findBySearchTerm(@Param("search") String search);

    // Contar motos por status usando enum (para estatísticas)
    long countByStatusMoto(EnumStatusMoto statusMoto);
}
