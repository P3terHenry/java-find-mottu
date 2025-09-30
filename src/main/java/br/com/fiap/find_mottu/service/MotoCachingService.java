package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.MotoDTO;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotoCachingService {

    @Autowired
    private MotoRepository motoRepository;

    @Cacheable(value = "buscarTodosCache")
    public List<MotoDTO> findAll(){
        return motoRepository.findAllMotos();
    }

    @Cacheable(value = "burcarPorID", key = "#id")
    public Optional<Moto> findById(Long id) {
        return motoRepository.findById(id);
    }

    @Cacheable(value = "buscarPaginasMotos", key = "#req")
    public Page<Moto> findAll(PageRequest req) {
        return motoRepository.findAll(req);
    }

    @CacheEvict(value = {"buscarTodosCache", "buscarPorID", "buscarPaginasMotos"}, allEntries = true)
    public void limparCache() {
        System.out.println("Limpando cache!");
    }
}
