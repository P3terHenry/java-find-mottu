package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.dto.MotoDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.service.MotoCachingService;
import br.com.fiap.find_mottu.service.MotoService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/moto")
@Tag(name = "Motos", description = "Operações relacionadas as Motos.")
public class MotoController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private MotoCachingService motoCaching;

    @Operation(summary = "Retorna todas as motos cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motos retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma moto encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<MotoDTO> retornaTodasAsMotos() {
        List<MotoDTO> motos = motoRepository.findAllMotos();
        if (motos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma moto encontrada.");
        }
        return motos;
    }

    @Operation(summary = "Retorna todas as motos cacheadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motos em cache retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma moto encontrada em cache", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos-cacheable")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<MotoDTO> retornaTodasMotosCacheable() {
        List<MotoDTO> motos = motoCaching.findAll();
        if (motos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma moto em cache.");
        }
        return motos;
    }

    @Operation(summary = "Retorna as motos paginadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motos paginadas retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Nenhuma moto encontrada na página solicitada", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todos-paginados")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<MotoDTO>> paginarMotos(
            @RequestParam(value = "pagina", defaultValue = "0") Integer page,
            @RequestParam(value = "tamanho", defaultValue = "2") Integer size) {

        if (page < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
        }

        PageRequest pr = PageRequest.of(page, size);
        Page<MotoDTO> paginas_motos_dto = motoService.paginarTodasAsMotos(pr);

        if (paginas_motos_dto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma moto encontrada na página solicitada.");
        }

        return ResponseEntity.ok(paginas_motos_dto);
    }

    @Operation(summary = "Retorna localizações paginadas de uma moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localizações paginadas retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campo de ordenação inválido", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Nenhuma localização encontrada para a moto", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}/localizacoes-paginadas")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<LocalizacaoDTO>> paginarLocalizacoesDaMoto(
            @PathVariable Long id,
            @RequestParam(value = "pagina", defaultValue = "0") Integer page,
            @RequestParam(value = "tamanho", defaultValue = "2") Integer size,
            @RequestParam(value = "ordenacao", defaultValue = "dataLocalizacao,desc") String sortParam) {

        List<String> camposPermitidos = List.of("dataLocalizacao", "statusLocalizacao");
        String[] partes = sortParam.split(",");
        String campo = partes[0];
        Sort.Direction direcao = partes.length > 1 && partes[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (!camposPermitidos.contains(campo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido.");
        }

        PageRequest pr = PageRequest.of(page, size, Sort.by(direcao, campo));
        Page<LocalizacaoDTO> paginas_localizacoes_dto = motoService.paginarLocalizacoesPorIdDaMoto(id, pr);

        if (paginas_localizacoes_dto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma localização encontrada.");
        }

        return ResponseEntity.ok(paginas_localizacoes_dto);
    }

    @Operation(summary = "Retorna a última localização de uma moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Última localização encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma localização encontrada para a moto", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}/ultima-localizacao")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<LocalizacaoDTO> buscarUltimaLocalizacao(@PathVariable Long id) {
        List<Localizacao> entidades = localizacaoRepository.buscarUltimaLocalizacaoPorMotoComStatusAtivo(id);
        if (entidades.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma localização encontrada para a moto.");
        }

        return entidades.stream().map(LocalizacaoDTO::new).toList();
    }

    @Operation(summary = "Insere uma nova moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto inserida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios não informados", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/inserir")
    @SecurityRequirement(name = "Bearer Authentication")
    public Moto inserirMoto(@RequestBody Moto moto) {
        if (moto.getFilial() == null ||
                moto.getIdImei() == null ||
                moto.getIdQrCode() == null ||
                moto.getModeloMoto() == null ||
                moto.getNumChassi() == null ||
                moto.getNumMotor() == null ||
                moto.getPlacaMoto() == null ||
                moto.getStatusMoto() == null) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os campos da moto são obrigatórios.");
        }

        Moto salvo = motoRepository.save(moto);
        motoCaching.limparCache();
        return salvo;
    }

    @Operation(summary = "Atualiza uma moto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Moto para atualização não encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping(value = "/atualizar/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Moto atualizarMoto(@RequestBody Moto moto, @PathVariable Long id) {
        Optional<Moto> op = motoCaching.findById(id);

        if (op.isPresent()) {
            Moto moto_atual = op.get();

            moto_atual.setFilial(moto.getFilial());
            moto_atual.setIdImei(moto.getIdImei());
            moto_atual.setIdQrCode(moto.getIdQrCode());
            moto_atual.setModeloMoto(moto.getModeloMoto());
            moto_atual.setNumChassi(moto.getNumChassi());
            moto_atual.setNumMotor(moto.getNumMotor());
            moto_atual.setPlacaMoto(moto.getPlacaMoto());
            moto_atual.setStatusMoto(moto.getStatusMoto());

            motoRepository.save(moto_atual);
            motoCaching.limparCache();

            return moto_atual;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto para atualização não encontrada.");
        }
    }

    @Operation(summary = "Remove uma moto do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Moto não encontrada", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Moto possui localizações vinculadas e não pode ser removida", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping(value = "/remover/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Moto removerMoto(@PathVariable Long id) {
        Optional<Moto> op = motoCaching.findById(id);

        if (op.isPresent()) {
            Moto moto = op.get();

            boolean possuiLocalizacoes = !localizacaoRepository.findByMotoId(id).isEmpty();
            if (possuiLocalizacoes) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a moto. Existem localizações vinculadas a ela.");
            }

            motoRepository.delete(moto);
            motoCaching.limparCache();
            return moto;

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto não encontrada.");
        }
    }
}
