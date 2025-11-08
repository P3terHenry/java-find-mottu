IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FKrm0ahqo3n4u1ydsfuhfibdus6')
ALTER TABLE t_mottu_localizacoes DROP CONSTRAINT FKrm0ahqo3n4u1ydsfuhfibdus6;

IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK8j4l0jqc2bjcmqnfq96kqbqki')
ALTER TABLE t_mottu_motos DROP CONSTRAINT FK8j4l0jqc2bjcmqnfq96kqbqki;

IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK5ufk2na1t0i2w7rbc74dfd86h')
ALTER TABLE t_mottu_usuario DROP CONSTRAINT FK5ufk2na1t0i2w7rbc74dfd86h;

IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FKlkes5c85s3dn1ylel4sj2oga8')
ALTER TABLE t_usuario_cargo DROP CONSTRAINT FKlkes5c85s3dn1ylel4sj2oga8;

IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FKtijnqqgyv757onv8hdqlq0mbf')
ALTER TABLE t_usuario_cargo DROP CONSTRAINT FKtijnqqgyv757onv8hdqlq0mbf;

-- Dropar tabelas em ordem reversa de dependência
DROP TABLE IF EXISTS t_mottu_arquivo;
DROP TABLE IF EXISTS t_usuario_cargo;
DROP TABLE IF EXISTS t_mottu_localizacoes;
DROP TABLE IF EXISTS t_mottu_motos;
DROP TABLE IF EXISTS t_mottu_usuario;
DROP TABLE IF EXISTS t_mottu_cargo;
DROP TABLE IF EXISTS t_mottu_filiais;