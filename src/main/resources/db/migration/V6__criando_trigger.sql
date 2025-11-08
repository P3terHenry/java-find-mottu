CREATE TRIGGER trg_t_mottu_arquivo_update
ON t_mottu_arquivo
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Atualiza apenas os registros que foram modificados
    UPDATE t
    SET t.ATUALIZADO_EM = SYSDATETIME()
    FROM t_mottu_arquivo t
    INNER JOIN inserted i ON t.ID_ARQUIVO = i.ID_ARQUIVO;
END;
