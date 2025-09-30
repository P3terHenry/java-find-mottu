package br.com.fiap.find_mottu.repository;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

    @Query("SELECT new br.com.fiap.find_mottu.dto.LocalizacaoDTO(" +
            "l.id, l.moto.id, l.idImagem, l.posX, l.posY, l.statusLocalizacao, l.dataLocalizacao) " +
            "FROM Localizacao l")
    List<LocalizacaoDTO> findAllLocalizacoes();

    @Query(value = "SELECT * FROM T_MOTTU_LOCALIZACOES WHERE ID_MOTO = :id", nativeQuery = true)
    List<Localizacao> findByMotoId(@Param("id") Long id);

    @Query("SELECT l FROM Localizacao l WHERE l.moto.id = :id")
    Page<Localizacao> buscarLocalizacoesPorMoto(Long id, Pageable pageable);

    @Query("SELECT l FROM Localizacao l WHERE l.moto.id = :id AND l.statusLocalizacao = 1 ORDER BY l.dataLocalizacao DESC")
    List<Localizacao> buscarUltimaLocalizacaoPorMotoComStatusAtivo(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Localizacao l WHERE l.moto.id = :motoId")
    void deleteByMotoId(@Param("motoId") Long motoId);
}
