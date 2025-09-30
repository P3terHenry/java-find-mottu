package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.FilialDTO;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilialCachingService {

    @Autowired
    private FilialRepository filialRepository;

    @Cacheable(value = "filiais")
    public List<FilialDTO> findAll() {
        return filialRepository.findAllFiliais();
    }

    @Cacheable(value = "buscarPaginasFiliais", key = "#req")
    public Page<Filial> findAll(PageRequest req) {
        return filialRepository.findAll(req);
    }

    @Cacheable(value = "buscarPorID", key = "#id")
    public Optional<Filial> findById(Long id) {
        return filialRepository.findById(id);
    }


    @CacheEvict(value = "filiais", allEntries = true)
    public void limparCache() {
        System.out.println("Limpando cache!");
    }
}
