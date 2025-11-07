package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.dto.UsuarioDTO;
import br.com.fiap.find_mottu.model.Cargo;
import br.com.fiap.find_mottu.model.EnumCargo;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioCachingService usuarioCachingService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void paginarTodosOsUsuarios_deveRetornarDTOsMapeados() {
        Filial filial = new Filial(1L, "Endereco A", null);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPrimeiroNome("Joao");
        usuario.setSobrenome("Silva");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("senha");
        usuario.setIdade(30);
        usuario.setFilial(filial);
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setNome(EnumCargo.ADMIN);
        Set<Cargo> cargos = new HashSet<>();
        cargos.add(cargo);
        usuario.setCargos(cargos);

        Page<Usuario> page = new PageImpl<>(List.of(usuario));
        when(usuarioCachingService.findAll(any(PageRequest.class))).thenReturn(page);

        Page<UsuarioDTO> result = usuarioService.paginarTodosOsUsuarios(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        UsuarioDTO dto = result.getContent().get(0);
        assertEquals(usuario.getId(), dto.getId());
        assertEquals(usuario.getPrimeiroNome(), dto.getPrimeiroNome());
        assertEquals(usuario.getSobrenome(), dto.getSobrenome());
        assertEquals(usuario.getEmail(), dto.getEmail());
        assertEquals(usuario.getIdade(), dto.getIdade());
        assertNotNull(dto.getFilial());
        assertEquals(filial.getEndereco(), dto.getFilial().getEndereco());
    }
}

