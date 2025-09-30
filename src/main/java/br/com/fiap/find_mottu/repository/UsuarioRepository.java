package br.com.fiap.find_mottu.repository;

import br.com.fiap.find_mottu.dto.UsuarioDTO;
import br.com.fiap.find_mottu.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    // Buscar por e-mail exato
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.filial.id = :idFilial")
    List<Usuario> findByFilial(@Param("idFilial") Long idFilial);

    // Contar usuários por filial
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.filial.id = :filialId")
    long countByFilialId(@Param("filialId") Long filialId);

    // Buscar usuários por parte do nome (JPQL com like)
    @Query("""
        from Usuario u 
        where lower(u.primeiroNome) like lower(concat('%', :nome, '%')) 
           or lower(u.sobrenome) like lower(concat('%', :nome, '%'))
    """)
    List<Usuario> buscarPorNome(String nome);

    @Query("SELECT new br.com.fiap.find_mottu.dto.UsuarioDTO(u) FROM Usuario u")
    List<UsuarioDTO> findAllUsuarios();

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmailforAuth(String email);
}
