package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.dto.MotoDTO;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MotoService {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private MotoCachingService motoCachingService;

    @Transactional(readOnly = true)
    public Page<MotoDTO> paginarTodasAsMotos(PageRequest req) {

        Page<Moto> paginas_motos = motoCachingService.findAll(req);
        Page<MotoDTO> paginas_motos_dto =
                paginas_motos.map(moto -> new MotoDTO(moto));
        return paginas_motos_dto;
    }

    @Transactional(readOnly = true)
    public Page<LocalizacaoDTO> paginarLocalizacoesPorIdDaMoto(Long id, PageRequest req) {
        // verifica se a moto existe
        if (!motoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Moto com ID " + id + " não encontrada.");
        }

        // busca as localizações paginadas
        return localizacaoRepository
                .buscarLocalizacoesPorMoto(id, req)
                .map(LocalizacaoDTO::new); // converte cada Localizacao em LocalizacaoDTO
    }
}
