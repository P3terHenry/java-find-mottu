package br.com.fiap.find_mottu.repository;

import br.com.fiap.find_mottu.model.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {

    Optional<Arquivo> findBySquareId(String squareId);

    List<Arquivo> findAllBySquareId(String squareId);

}
