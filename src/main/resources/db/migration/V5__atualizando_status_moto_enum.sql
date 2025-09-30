-- Migração para atualizar STATUS_MOTO de valores numéricos para enum string

-- Primeiro, vamos alterar a estrutura da coluna para suportar strings (sintaxe SQL Server)
ALTER TABLE T_MOTTU_MOTOS ALTER COLUMN STATUS_MOTO VARCHAR(20);

-- Atualizar os valores existentes de numérico para enum string
UPDATE T_MOTTU_MOTOS SET STATUS_MOTO = 'INATIVA' WHERE STATUS_MOTO = '0';
UPDATE T_MOTTU_MOTOS SET STATUS_MOTO = 'ATIVA' WHERE STATUS_MOTO = '1';
UPDATE T_MOTTU_MOTOS SET STATUS_MOTO = 'MANUTENCAO' WHERE STATUS_MOTO = '2';
UPDATE T_MOTTU_MOTOS SET STATUS_MOTO = 'RESERVADA' WHERE STATUS_MOTO = '3';

-- Adicionar constraint para garantir apenas valores válidos do enum (sintaxe SQL Server)
ALTER TABLE T_MOTTU_MOTOS ADD CONSTRAINT chk_status_moto
CHECK (STATUS_MOTO IN ('INATIVA', 'ATIVA', 'MANUTENCAO', 'RESERVADA'));
