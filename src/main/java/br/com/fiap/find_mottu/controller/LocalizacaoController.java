package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.dto.LocalizacaoDTO;
import br.com.fiap.find_mottu.dto.LocalizacaoRequestDTO;
import br.com.fiap.find_mottu.model.Localizacao;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.service.LocalizacaoCachingService;
import br.com.fiap.find_mottu.service.LocalizacaoService;
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
@RequestMapping("/api/localizacao/")
@Tag(name = "Localizações", description = "Operações relacionadas as Localizações das Motos.")
public class LocalizacaoController {

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private LocalizacaoCachingService localizacaoCachingService;

    @Autowired
    private LocalizacaoService localizacaoService;

    @Operation(summary = "Retorna todas as localizações cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localizações retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma localizações encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todas")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<LocalizacaoDTO> retornaTodasAsLocalizacoes(){
        List<LocalizacaoDTO> localizacoes = localizacaoRepository.findAllLocalizacoes();
        if (localizacoes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nennhuma localização encontrada.");
        }
        return localizacoes;
    }

    @Operation(summary = "Retorna todas as localizações cacheadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localizações em cache retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma localizações encontrada em cache", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todas-cacheable")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<LocalizacaoDTO> retornaTodasLocalizacoesCacheable() {
        List<LocalizacaoDTO> localizazacao = localizacaoCachingService.findAll();
        if (localizazacao.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma localização em cache.");
        }
        return localizazacao;
    }

    @Operation(summary = "Retorna as localizações paginadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localizações paginadas retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Nenhuma localização encontrada na página solicitada", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(value = "/todas-paginadas")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<LocalizacaoDTO>> paginarLocalizacoes(
            @RequestParam(value = "pagina", defaultValue = "0") Integer page,
            @RequestParam(value = "tamanho", defaultValue = "2") Integer size) {

        if (page < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
        }

        PageRequest pr = PageRequest.of(page, size);
        Page<LocalizacaoDTO> paginas_localizacoes_dto = localizacaoService.paginarTodasAsLocalizacoes(pr);

        if (paginas_localizacoes_dto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma moto encontrada na página solicitada.");
        }

        return ResponseEntity.ok(paginas_localizacoes_dto);
    }

    @Operation(summary = "Insere uma nova localização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localização inserida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios não informados", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/inserir")
    @SecurityRequirement(name = "Bearer Authentication")
    public Localizacao inserirLocalizacao(@RequestBody LocalizacaoRequestDTO dto) {
        if (dto.getIdMoto() == null || dto.getIdImagem() == null ||
                dto.getPosX() == null || dto.getPosY() == null || dto.getStatusLocalizacao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os campos obrigatórios devem ser informados.");
        }

        Optional<Moto> motoOpt = motoRepository.findById(dto.getIdMoto());
        if (motoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto com o ID fornecido não foi encontrada.");
        }

        Localizacao localizacao = new Localizacao();
        localizacao.setMoto(motoOpt.get());
        localizacao.setIdImagem(dto.getIdImagem());
        localizacao.setPosX(dto.getPosX());
        localizacao.setPosY(dto.getPosY());
        localizacao.setStatusLocalizacao(dto.getStatusLocalizacao());
        localizacao.setDataLocalizacao(dto.getDataLocalizacao());

        Localizacao salvo = localizacaoRepository.save(localizacao);
        localizacaoCachingService.limparCache();

        return salvo;
    }

    @Operation(summary = "Atualiza uma localização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localização atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Localização ou moto não encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping(value = "/atualizar/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Localizacao atualizarLocalizacao(@RequestBody LocalizacaoRequestDTO dto, @PathVariable Long id) {
        Optional<Localizacao> op = localizacaoCachingService.findById(id);

        if (op.isPresent()) {
            Localizacao loc_Atual = op.get();

            Optional<Moto> motoOpt = motoRepository.findById(dto.getIdMoto());
            if (motoOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto com o ID fornecido não foi encontrada.");
            }

            loc_Atual.setMoto(motoOpt.get());
            loc_Atual.setIdImagem(dto.getIdImagem());
            loc_Atual.setPosX(dto.getPosX());
            loc_Atual.setPosY(dto.getPosY());
            loc_Atual.setStatusLocalizacao(dto.getStatusLocalizacao());
            loc_Atual.setDataLocalizacao(dto.getDataLocalizacao());

            localizacaoRepository.save(loc_Atual);
            localizacaoCachingService.limparCache();

            return loc_Atual;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Localização para atualização não encontrada.");
        }
    }

    @Operation(summary = "Remove uma localização do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localização removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping(value = "/remover/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Localizacao removerLocalizacao(@PathVariable Long id) {
        Optional<Localizacao> op = localizacaoCachingService.findById(id);

        if (op.isPresent()) {
            Localizacao localizacao = op.get();
            localizacaoRepository.delete(localizacao);
            localizacaoCachingService.limparCache();
            return localizacao;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Localização não encontrada.");
        }
    }
}
