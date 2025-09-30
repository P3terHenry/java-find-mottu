package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.dto.UsuarioDTO;
import br.com.fiap.find_mottu.dto.UsuarioRequestDTO;
import br.com.fiap.find_mottu.model.Cargo;
import br.com.fiap.find_mottu.model.EnumCargo;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.CargoRepository;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import br.com.fiap.find_mottu.service.UsuarioCachingService;
import br.com.fiap.find_mottu.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
@Tag(name = "Usuários", description = "Operações relacionadas aos Usuários.")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private UsuarioCachingService usuarioCachingService;

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Retorna todas os usuários cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<UsuarioDTO> retornaTodosOsUsuarios() {
        List<UsuarioDTO> usuarios = usuarioRepository.findAllUsuarios();
        if(usuarios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum usuário encontrado.");
        }
        return usuarios;
    }

    @Operation(summary = "Retorna todos os usuários cacheadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários em cache retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado em cache", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos-cachable")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<UsuarioDTO> retornaTodosOsUsuariosCacheable() {
        List<UsuarioDTO> usuarios = usuarioCachingService.findAll();
        if (usuarios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum usuário em cache.");
        }
        return usuarios;
    }

    @Operation(summary = "Retorna os usuários paginados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários paginados retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado na página solicitada", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos-paginados")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<UsuarioDTO>> paginarUsuarios(
            @RequestParam(value = "pagina", defaultValue = "0") Integer page,
            @RequestParam(value = "tamanho", defaultValue = "2") Integer size) {

        if (page < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
        }

        PageRequest pr = PageRequest.of(page, size);
        Page<UsuarioDTO> paginas_usuarios_dto = usuarioService.paginarTodosOsUsuarios(pr);

        if (paginas_usuarios_dto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum usuário encontrado na página solicitada.");
        }

        return ResponseEntity.ok(paginas_usuarios_dto);
    }

    @Operation(summary = "Insere um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário inserido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios não informados", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/inserir")
    @SecurityRequirement(name = "Bearer Authentication")
    public Usuario inserirUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRequestDTO.getIdFilial() == null ||
                usuarioRequestDTO.getPrimeiroNome() == null ||
                usuarioRequestDTO.getSobrenome() == null ||
                usuarioRequestDTO.getEmail() == null ||
                usuarioRequestDTO.getCargo() == null ||
                usuarioRequestDTO.getIdade() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os campos do usuário são obrigatórios.");
        }

        // Validar filial
        Optional<Filial> FilialOpt = filialRepository.findById(usuarioRequestDTO.getIdFilial());
        if (FilialOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial com o ID fornecido não foi encontrada.");
        }

        // Validar e buscar cargo pelo enum
        EnumCargo enumCargo;
        try {
            enumCargo = EnumCargo.valueOf(usuarioRequestDTO.getCargo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cargo inválido. Valores válidos: " +
                String.join(", ", EnumCargo.values().toString()));
        }

        Optional<Cargo> cargoOpt = cargoRepository.findByNome(enumCargo);
        if (cargoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo não encontrado no sistema.");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setFilial(FilialOpt.get());
        usuario.setPrimeiroNome(usuarioRequestDTO.getPrimeiroNome());
        usuario.setSobrenome(usuarioRequestDTO.getSobrenome());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setIdade(usuarioRequestDTO.getIdade());
        usuario.getCargos().add(cargoOpt.get());

        Usuario salvo = usuarioRepository.save(usuario);
        usuarioCachingService.limparCache();
        return salvo;
    }

    @Operation(summary = "Atualiza um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário para atualização não encontrado", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/atualizar/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Usuario atualizarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO, @PathVariable Long id) {
        Optional<Usuario> op = usuarioCachingService.findById(id);

        if (op.isPresent()) {
            Usuario usuarioAtual = op.get();

            // Validar campos obrigatórios
            if (usuarioRequestDTO.getIdFilial() == null ||
                    usuarioRequestDTO.getPrimeiroNome() == null ||
                    usuarioRequestDTO.getSobrenome() == null ||
                    usuarioRequestDTO.getEmail() == null ||
                    usuarioRequestDTO.getCargo() == null ||
                    usuarioRequestDTO.getIdade() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os campos do usuário são obrigatórios.");
            }

            // Validar filial
            Optional<Filial> FilialOpt = filialRepository.findById(usuarioRequestDTO.getIdFilial());
            if (FilialOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial com o ID fornecido não foi encontrada.");
            }

            // Validar e buscar cargo pelo enum
            EnumCargo enumCargo;
            try {
                enumCargo = EnumCargo.valueOf(usuarioRequestDTO.getCargo().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cargo inválido. Valores válidos: " +
                    String.join(", ", EnumCargo.values().toString()));
            }

            Optional<Cargo> cargoOpt = cargoRepository.findByNome(enumCargo);
            if (cargoOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo não encontrado no sistema.");
            }

            // Atualizar dados básicos
            usuarioAtual.setFilial(FilialOpt.get());
            usuarioAtual.setPrimeiroNome(usuarioRequestDTO.getPrimeiroNome());
            usuarioAtual.setSobrenome(usuarioRequestDTO.getSobrenome());
            usuarioAtual.setEmail(usuarioRequestDTO.getEmail());
            usuarioAtual.setIdade(usuarioRequestDTO.getIdade());
            // Atualizar cargos (limpar e adicionar novo)
            // Isso evita problemas de IDs duplicados na tabela associativa
            usuarioAtual.getCargos().clear(); // Remove todos os cargos existentes
            usuarioAtual.getCargos().add(cargoOpt.get()); // Adiciona o novo cargo

            usuarioRepository.save(usuarioAtual);
            usuarioCachingService.limparCache();

            return usuarioAtual;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário para atualização não encontrado.");
        }
    }

    @Operation(summary = "Remove um usuário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/remover/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Usuario removerUsuario(@PathVariable Long id) {
        Optional<Usuario> op = usuarioCachingService.findById(id);

        if (op.isPresent()) {
            Usuario usuario = op.get();

            // Limpar todos os relacionamentos na tabela associativa antes de deletar
            // Isso evita problemas de constraint de chave estrangeira
            usuario.getCargos().clear();

            // Salvar para persistir a remoção dos relacionamentos
            usuarioRepository.save(usuario);

            // Agora deletar o usuário com segurança
            usuarioRepository.delete(usuario);
            usuarioCachingService.limparCache();

            return usuario;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }
    }

}
