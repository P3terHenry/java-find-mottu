package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.model.Moto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizacaoServiceTest {

    @Mock
    private LocalizacaoCachingService localizacaoCachingService;

    @InjectMocks
    private LocalizacaoService localizacaoService;

    @Test
    void deveRetornarDTOsMapeados_quandoPaginarTodasAsLocalizacoes() {
        Moto moto = new Moto();
        moto.setId(5L);
        LocalDateTime now = LocalDateTime.now();
        Localizacao loc = new Localizacao(1L, "IMG2", new BigDecimal("2.000"), new BigDecimal("3.000"), 1, now, moto);
        Page<Localizacao> page = new PageImpl<>(List.of(loc));
        when(localizacaoCachingService.findAll(any(PageRequest.class))).thenReturn(page);

        Page<LocalizacaoDTO> result = localizacaoService.paginarTodasAsLocalizacoes(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        LocalizacaoDTO dto = result.getContent().get(0);
        assertEquals(loc.getId(), dto.getId());
        assertEquals(loc.getIdImagem(), dto.getIdImagem());
        assertEquals(loc.getStatusLocalizacao(), dto.getStatusLocalizacao());
        assertEquals(moto.getId(), dto.getIdMoto());
    }
}

