package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.UsuarioDTO;
import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioCachingService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Cacheable(value = "buscarTodosCache")
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAllUsuarios();
    }

    @Cacheable(value = "buscarPorID", key = "#id")
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Cacheable(value = "buscarPaginasUsuarios", key = "#req")
    public Page<Usuario> findAll(PageRequest req) {
        return usuarioRepository.findAll(req);
    }

    @CacheEvict(value = {"buscarTodosCache", "buscarPorID","buscarPaginasUsuarios"}, allEntries = true)
    public void limparCache() {
        System.out.println("Limpando cache!");
    }
}
