package br.com.fiap.find_mottu.repository;

import br.com.fiap.find_mottu.model.Cargo;
import br.com.fiap.find_mottu.model.EnumCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    Optional<Cargo> findByNome(EnumCargo nome);
}
