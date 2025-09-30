package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.FilialDTO;
import br.com.fiap.find_mottu.model.Filial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilialService {

    @Autowired
    private FilialCachingService filialCachingService;

    @Transactional(readOnly = true)
    public Page<FilialDTO> paginarTodasAsFiliais(PageRequest req) {

        Page<Filial> paginas_filiais = filialCachingService.findAll(req);
        Page<FilialDTO> paginas_filiais_dto =
                paginas_filiais.map(filial -> new FilialDTO(filial));
        return paginas_filiais_dto;

    }
}
