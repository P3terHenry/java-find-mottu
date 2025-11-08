package br.com.fiap.find_mottu.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_MOTTU_ARQUIVO")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ARQUIVO")
    private Long id;

    // ID retornado pela SquareCloud
    @Column(name = "ID_SQUARE", unique = true, nullable = false)
    private String squareId;

    // Nome original do arquivo enviado
    @Column(nullable = false)
    private String nome;

    // Prefixo opcional definido pelo usuário
    private String prefixo;

    // URL gerada pela SquareCloud
    @Column(nullable = false, unique = true)
    private String url;

    // Hash de segurança (se retornado)
    private String hashSeguranca;

    // Tamanho do arquivo em bytes
    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    // Status do upload ("success" | "error")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_UPLOAD")
    private EnumStatusUpload statusUpload;

    // Status interno do arquivo no sistema ("ATIVO", "EXCLUIDO")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_ARQUIVO")
    private EnumStatusArquivo statusArquivo;

    // Data de expiração (caso exista)
    private LocalDateTime dataExpiracao;

    // Flag se o arquivo é de download automático
    private Boolean autoDownload;

    // Data de criação no sistema
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Data de atualização no sistema
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.statusArquivo == null) {
            this.statusArquivo = EnumStatusArquivo.ATIVO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}