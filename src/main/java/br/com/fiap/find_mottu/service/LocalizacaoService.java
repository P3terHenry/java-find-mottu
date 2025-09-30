package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalizacaoService {

    @Autowired
    private LocalizacaoCachingService localizacaoCachingService;

    @Transactional(readOnly = true)
    public Page<LocalizacaoDTO> paginarTodasAsLocalizacoes(PageRequest req) {

        Page<Localizacao> paginas_localizacao = localizacaoCachingService.findAll(req);
        Page<LocalizacaoDTO> paginas_localizacao_dto =
                paginas_localizacao.map(localizacao -> new LocalizacaoDTO(localizacao));
        return paginas_localizacao_dto;
    }
}
