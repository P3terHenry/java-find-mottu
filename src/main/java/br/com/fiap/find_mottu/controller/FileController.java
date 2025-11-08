package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.model.Arquivo;
import br.com.fiap.find_mottu.repository.ArquivoRepository;
import br.com.fiap.find_mottu.service.ArquivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/arquivos")
@Tag(name = "Arquivos", description = "Operações relacionadas à manipulação de arquivos na SquareCloud Blob API.")
public class FileController {

    @Value("${squarecloud.api.key}")
    private String squareCloudApiKey;

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Operation(summary = "Faz upload de um arquivo para a SquareCloud e salva o retorno no banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo enviado e salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ao enviar o arquivo", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Chave de API inválida ou ausente", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "413", description = "Arquivo excede o tamanho máximo permitido (100MB)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "502", description = "Erro ao se comunicar com a SquareCloud", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Arquivo> uploadArquivo(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String prefixo,
            @RequestParam(defaultValue = "false") Boolean autoDownload,
            @RequestParam(required = false, defaultValue = "false") Boolean securityHash,
            @RequestParam(required = false) Integer expireDays
    ) throws IOException {

        // 🧩 Validações básicas
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhum arquivo foi enviado.");
        }

        if (arquivo.getSize() > 100 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo excede o tamanho máximo permitido de 100MB.");
        }

        // Só valida expireDays se não for nulo
        if (expireDays != null && (expireDays < 1 || expireDays > 365)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período de expiração deve estar entre 1 e 365 dias.");
        }

        // Nome sem extensão
        String nomeSemExtensao = arquivo.getOriginalFilename().replaceFirst("[.][^.]+$", "");

        // 🌐 Configuração do WebClient
        WebClient webClient = WebClient.builder()
                .baseUrl("https://blob.squarecloud.app")
                .defaultHeader(HttpHeaders.AUTHORIZATION, squareCloudApiKey)
                .build();

        // 🧱 Corpo multipart
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", arquivo.getResource())
                .filename(arquivo.getOriginalFilename())
                .contentType(MediaType.valueOf(arquivo.getContentType()));

        Map<String, Object> responseBody;

        try {
            responseBody = webClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/v1/objects")
                                .queryParam("name", nomeSemExtensao)
                                .queryParamIfPresent("prefix", (prefixo == null || prefixo.isBlank())
                                        ? java.util.Optional.empty()
                                        : java.util.Optional.of(prefixo))
                                .queryParam("auto_download", autoDownload)
                                .queryParam("security_hash", securityHash);

                        // Só envia expire se não for nulo
                        if (expireDays != null) {
                            builder.queryParam("expire", expireDays);
                        }

                        return builder.build();
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                                    "Erro ao enviar arquivo: " + clientResponse.statusCode())))
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(); // Bloqueia até a resposta chegar

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao se comunicar com a SquareCloud: " + e.getMessage());
        }

        if (responseBody == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida da SquareCloud.");
        }

        // ⚠️ Tratamento de erros retornados pela SquareCloud
        String status = (String) responseBody.get("status");
        String code = (String) responseBody.get("code");

        if ("error".equalsIgnoreCase(status)) {
            switch (code) {
                case "ACCESS_DENIED" -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chave de API inválida ou ausente.");
                case "INVALID_OBJECT_NAME" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do arquivo é inválido. Use apenas letras, números e underscore (_).");
                case "INVALID_OBJECT_EXPIRE" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor de expiração é inválido. Deve estar entre 1 e 365 dias.");
                case "INVALID_OBJECT_SECURITY_HASH" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor de security_hash é inválido. Deve ser true ou false.");
                case "INVALID_OBJECT_AUTO_DOWNLOAD" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor de auto_download é inválido. Deve ser true ou false.");
                case "INVALID_FILE" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo enviado é inválido.");
                case "INVALID_FILETYPE" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tipo de arquivo enviado não é permitido.");
                case "FILE_TOO_SMALL" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo é muito pequeno. Deve ter pelo menos 1KB.");
                case "FILE_TOO_LARGE" -> throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo é grande demais. O limite é 100MB.");
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro desconhecido da SquareCloud: " + code);
            }
        }

        // ✅ Upload bem-sucedido
        Object responseObj = responseBody.get("response");
        if (!(responseObj instanceof Map)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Formato de resposta inesperado da SquareCloud.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseObj;

        Arquivo saved = arquivoService.salvarOuAtualizarSquareCloud(
                arquivo.getOriginalFilename(),
                prefixo,
                autoDownload,
                data,
                expireDays
        );

        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Exclui um arquivo da SquareCloud e atualiza status no banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Chave de API inválida ou ausente", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "502", description = "Erro ao se comunicar com a SquareCloud", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> excluirArquivo(@RequestParam("squareId") String squareId) {
        if (squareId == null || squareId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do arquivo é obrigatório.");
        }

        // Busca o arquivo no banco pelo squareId
        Arquivo arquivo = arquivoRepository.findBySquareId(squareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo não encontrado no banco."));

        String objectName = arquivo.getPrefixo() != null && !arquivo.getPrefixo().isBlank()
                ? arquivo.getPrefixo() + "/" + arquivo.getNome()
                : arquivo.getNome();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://blob.squarecloud.app")
                .defaultHeader(HttpHeaders.AUTHORIZATION, squareCloudApiKey)
                .build();

        Map<String, Object> responseBody;
        try {
            responseBody = webClient.method(HttpMethod.DELETE)
                    .uri("/v1/objects")
                    .bodyValue(Map.of("object", objectName))
                    .exchangeToMono(response -> {
                        if (response.statusCode().isError()) {
                            return response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new ResponseStatusException(
                                            HttpStatus.BAD_GATEWAY,
                                            "Erro ao excluir arquivo: " + response.statusCode() + " - " + body
                                    )));
                        }
                        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
                    })
                    .block();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao se comunicar com a SquareCloud: " + e.getMessage());
        }

        if (responseBody == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida da SquareCloud.");
        }

        String status = (String) responseBody.get("status");
        String code = (String) responseBody.get("code");

        if ("error".equalsIgnoreCase(status)) {
            switch (code) {
                case "ACCESS_DENIED" -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chave de API inválida ou ausente.");
                case "INVALID_OBJECT" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do objeto é inválido.");
                case "FAILED_DELETE" -> throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao excluir o objeto. Tente novamente.");
                case "OBJECT_NOT_FOUND" -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O objeto não existe na SquareCloud.");
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro desconhecido da SquareCloud: " + code);
            }
        }

        // Atualiza status do arquivo no banco
        arquivoService.marcarArquivoComoExcluido(squareId);

        return ResponseEntity.ok().build();
    }
}