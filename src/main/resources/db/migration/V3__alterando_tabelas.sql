-- Adicionar foreign keys (SQL Server não suporta IF EXISTS em ALTER TABLE)
IF OBJECT_ID('t_mottu_localizacoes', 'U') IS NOT NULL
    ALTER TABLE t_mottu_localizacoes ADD CONSTRAINT FKrm0ahqo3n4u1ydsfuhfibdus6 FOREIGN KEY (id_moto) REFERENCES t_mottu_motos(id_moto);

IF OBJECT_ID('t_mottu_motos', 'U') IS NOT NULL
    ALTER TABLE t_mottu_motos ADD CONSTRAINT FK8j4l0jqc2bjcmqnfq96kqbqki FOREIGN KEY (id_filial) REFERENCES t_mottu_filiais(id_filial);

IF OBJECT_ID('t_mottu_usuario', 'U') IS NOT NULL
    ALTER TABLE t_mottu_usuario ADD CONSTRAINT FK5ufk2na1t0i2w7rbc74dfd86h FOREIGN KEY (id_filial) REFERENCES t_mottu_filiais(id_filial);

IF OBJECT_ID('t_usuario_cargo', 'U') IS NOT NULL
    ALTER TABLE t_usuario_cargo ADD CONSTRAINT FKlkes5c85s3dn1ylel4sj2oga8 FOREIGN KEY (id_cargo) REFERENCES t_mottu_cargo(id_cargo);

IF OBJECT_ID('t_usuario_cargo', 'U') IS NOT NULL
    ALTER TABLE t_usuario_cargo ADD CONSTRAINT FKtijnqqgyv757onv8hdqlq0mbf FOREIGN KEY (id_usuario) REFERENCES t_mottu_usuario(id_usuario);

