package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.dto.MotoDTO;
import br.com.fiap.find_mottu.model.EnumStatusMoto;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MotoServiceTest {

    @Mock
    private MotoRepository motoRepository;

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private MotoCachingService motoCachingService;

    @InjectMocks
    private MotoService motoService;

    @Test
    void deveRetornarDTOsMapeados_quandoPaginarTodasAsMotos() {
        Filial filial = new Filial(1L, "Rua A", null);
        Moto moto = new Moto(1L, "QR1", 123456789012345L, "CHASSI12345678901", 1L, "Modelo X", "ABC-1234", EnumStatusMoto.ATIVA, filial, null);
        Page<Moto> page = new PageImpl<>(List.of(moto));
        when(motoCachingService.findAll(any(PageRequest.class))).thenReturn(page);

        Page<MotoDTO> result = motoService.paginarTodasAsMotos(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        MotoDTO dto = result.getContent().get(0);
        assertEquals(moto.getId(), dto.getId());
        assertEquals(moto.getPlacaMoto(), dto.getPlacaMoto());
        assertEquals(moto.getModeloMoto(), dto.getModeloMoto());
        assertNotNull(dto.getFilial());
        assertEquals(filial.getEndereco(), dto.getFilial().getEndereco());
    }

    @Test
    void deveLancarExcecao_quandoMotoNaoForEncontrada() {
        when(motoRepository.existsById(10L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                motoService.paginarLocalizacoesPorIdDaMoto(10L, PageRequest.of(0, 10)));

        assertTrue(ex.getMessage().contains("Moto com ID 10"));
    }

    @Test
    void deveRetornarLocalizacoes_quandoMotoExistir() {
        Filial filial = new Filial(2L, "Rua B", null);
        Moto moto = new Moto(2L, "QR2", 987654321098765L, "CHASSI98765432109", 2L, "Modelo Y", "DEF-5678", EnumStatusMoto.ATIVA, filial, null);
        LocalDateTime now = LocalDateTime.now();
        Localizacao loc = new Localizacao(1L, "IMG1", new BigDecimal("1.234"), new BigDecimal("4.567"), 1, now, moto);
        Page<Localizacao> page = new PageImpl<>(List.of(loc));

        when(motoRepository.existsById(2L)).thenReturn(true);
        when(localizacaoRepository.buscarLocalizacoesPorMoto(2L, PageRequest.of(0, 10))).thenReturn(page);

        Page<LocalizacaoDTO> result = motoService.paginarLocalizacoesPorIdDaMoto(2L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        LocalizacaoDTO dto = result.getContent().get(0);
        assertEquals(loc.getId(), dto.getId());
        assertEquals(moto.getId(), dto.getIdMoto());
        assertEquals(loc.getIdImagem(), dto.getIdImagem());
    }
}

