package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.FilialDTO;
import br.com.fiap.find_mottu.model.Filial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilialServiceTest {

    @Mock
    private FilialCachingService filialCachingService;

    @InjectMocks
    private FilialService filialService;

    @Test
    void deveRetornarDTOsMapeados_quandoPaginarTodasAsFiliais() {
        Filial filial = new Filial(1L, "Endereco Filial", null);
        Page<Filial> page = new PageImpl<>(List.of(filial));
        when(filialCachingService.findAll(any(PageRequest.class))).thenReturn(page);

        Page<FilialDTO> result = filialService.paginarTodasAsFiliais(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        FilialDTO dto = result.getContent().get(0);
        assertEquals(filial.getId(), dto.getId());
        assertEquals(filial.getEndereco(), dto.getEndereco());
    }
}

