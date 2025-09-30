package br.com.fiap.find_mottu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.fiap.find_mottu.dto.FilialDTO;
import br.com.fiap.find_mottu.model.Filial;
import org.springframework.stereotype.Repository;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {

    @Query("SELECT new br.com.fiap.find_mottu.dto.FilialDTO(f.id, f.endereco) FROM Filial f")
    List<FilialDTO> findAllFiliais();


}