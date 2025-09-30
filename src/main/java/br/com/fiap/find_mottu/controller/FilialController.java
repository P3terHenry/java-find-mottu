package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.dto.FilialDTO;
import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import br.com.fiap.find_mottu.service.FilialCachingService;
import br.com.fiap.find_mottu.service.FilialService;
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
@RequestMapping("/api/filial")
@Tag(name = "Filial", description = "Operações relacionadas às Filiais.")
public class FilialController {

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private FilialService filialService;

    @Autowired
    private FilialCachingService filialCachingService;

    @Operation(summary = "Retorna todas as filiais cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filiais retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma filial encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/todos")
    public List<FilialDTO> retornaTodasFiliais() {
        return filialRepository.findAllFiliais();
    }

    @Operation(summary = "Retorna todas as filiais cacheadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filiais em cache retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma filial encontrada em cache", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/todas-cachable")
    public List<FilialDTO> retornaTodasFiliaisCacheable() {
        List<FilialDTO> filial = filialCachingService.findAll();
        if (filial.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma filial em cache.");
        }
        return filial;
    }

    @Operation(summary = "Retorna as filiais paginadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filiais paginadas retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Nenhuma filial encontrada na página solicitada", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/todas-paginados")
    public ResponseEntity<Page<FilialDTO>> paginarFiliais(
            @RequestParam(value = "pagina", defaultValue = "0") Integer page,
            @RequestParam(value = "tamanho", defaultValue = "2") Integer size) {

        if (page < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
        }

        PageRequest pr = PageRequest.of(page, size);
        Page<FilialDTO> paginas_filiais_dto = filialService.paginarTodasAsFiliais(pr);

        if (paginas_filiais_dto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma filial encontrada na página solicitada.");
        }

        return ResponseEntity.ok(paginas_filiais_dto);
    }


    @Operation(summary = "Insere uma nova filial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filial inserida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios não informados", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/inserir")
    public Filial inserirFilial(@RequestBody Filial filial) {

        if (filial.getEndereco() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os campos da filial são obrigatórios.");
        }

        filial.setId(null);

        Filial salvo = filialRepository.save(filial);
        filialCachingService.limparCache();
        return salvo;
    }

    @Operation(summary = "Atualiza uma filial existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filial atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Filial para atualização não encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/atualizar/{id}")
    public Filial atualizarFilial(@RequestBody Filial filial, @PathVariable Long id) {
        Optional<Filial> op = filialCachingService.findById(id);

        if (op.isPresent()) {
            Filial filial_atual = op.get();

            filial_atual.setEndereco(filial.getEndereco());

            filialRepository.save(filial_atual);
            filialCachingService.limparCache();
            return filial_atual;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial para atualização não encontrada.");
        }
    }

    @Operation(summary = "Remove uma filial do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filial removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Filial possui motos e/ou usuários vinculadas e não pode ser removida", content = @Content(schema = @Schema(hidden = true)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/remover/{id}")
    public Filial removerFilial(@PathVariable Long id) {
        Optional<Filial> op = filialCachingService.findById(id);

        if (op.isPresent()){
            Filial filial = op.get();

            boolean possuiUsuarios = !usuarioRepository.findByFilial(id).isEmpty();
            boolean possuiMotos = !motoRepository.findByMotosInFilial(id).isEmpty();

            if (possuiUsuarios && possuiMotos) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a filial. Existem motos e usuários vinculado a ela.");
            } else if (possuiUsuarios) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a filial. Existem usuários vinculado a ela.");
            } else if (possuiMotos) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a filial. Existem motos vinculado a ela.");
            }

            filialRepository.delete(filial);
            filialCachingService.limparCache();
            return filial;

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial não encontrada.");
        }
    }

}
