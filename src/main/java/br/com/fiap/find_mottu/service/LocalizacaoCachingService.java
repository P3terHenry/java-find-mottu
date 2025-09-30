package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalizacaoCachingService {

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Cacheable(value = "buscarTodasCache")
    public List<LocalizacaoDTO> findAll() {
        return localizacaoRepository.findAllLocalizacoes();
    }

    @Cacheable(value = "burcarPorID", key= "#id")
    public Optional<Localizacao> findById(Long id){
        return localizacaoRepository.findById(id);
    }

    @Cacheable(value = "buscarPaginasLocalizacao", key = "#req")
    public Page<Localizacao> findAll(PageRequest req) {
        return localizacaoRepository.findAll(req);
    }


    @CacheEvict(value = {"buscarTodosCache", "burcarPorID", "buscarPaginasLocalizacao"}, allEntries = true)
    public void limparCache() {
        System.out.println("Limpando cache!");
    }
}
