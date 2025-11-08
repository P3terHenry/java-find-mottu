package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.model.Arquivo;
import br.com.fiap.find_mottu.model.EnumStatusArquivo;
import br.com.fiap.find_mottu.model.EnumStatusUpload;
import br.com.fiap.find_mottu.repository.ArquivoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ArquivoService {

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Transactional
    public Arquivo salvarOuAtualizarSquareCloud(String nome, String prefixo, Boolean autoDownload, Map<String, Object> data, Integer expireDays) {
        String squareId = data != null ? (String) data.get("id") : null;

        // 🔹 Verifica se já existe um arquivo com esse squareId
        Arquivo arquivo = null;
        if (squareId != null) {
            arquivo = arquivoRepository.findBySquareId(squareId).orElse(null);
        }

        // 🔹 Se não existir, cria um novo
        if (arquivo == null) {
            arquivo = new Arquivo();
            arquivo.setSquareId(squareId);
            arquivo.setStatusUpload(EnumStatusUpload.SUCCESS);
            arquivo.setStatusArquivo(EnumStatusArquivo.ATIVO);
        }

        // 🔹 Define data de expiração somente se expireDays for informado
        if (expireDays != null) {
            arquivo.setDataExpiracao(LocalDateTime.now().plusDays(expireDays));
        } else {
            arquivo.setDataExpiracao(null);
        }

        // 🔹 Atualiza os campos
        arquivo.setNome(nome != null ? nome : (data != null ? (String) data.get("name") : null));
        arquivo.setPrefixo(prefixo);
        arquivo.setAutoDownload(autoDownload);
        if (data != null) {
            arquivo.setUrl((String) data.get("url"));
            arquivo.setTamanhoBytes(
                    data.get("size") instanceof Number ? ((Number) data.get("size")).longValue() : arquivo.getTamanhoBytes()
            );
        }

        return arquivoRepository.save(arquivo);
    }

    @Transactional
    public void marcarArquivoComoExcluido(String objectName) {
        arquivoRepository.findBySquareId(objectName)
                .ifPresent(arquivo -> {
                    arquivo.setStatusArquivo(EnumStatusArquivo.EXCLUIDO);
                    arquivoRepository.save(arquivo);
                });
    }

}