alter table if exists t_mottu_localizacoes add constraint FKrm0ahqo3n4u1ydsfuhfibdus6 foreign key (id_moto) references t_mottu_motos;
alter table if exists t_mottu_motos add constraint FK8j4l0jqc2bjcmqnfq96kqbqki foreign key (id_filial) references t_mottu_filiais;
alter table if exists t_mottu_usuario add constraint FK5ufk2na1t0i2w7rbc74dfd86h foreign key (id_filial) references t_mottu_filiais;
alter table if exists t_usuario_cargo add constraint FKlkes5c85s3dn1ylel4sj2oga8 foreign key (id_cargo) references t_mottu_cargo;
alter table if exists t_usuario_cargo add constraint FKtijnqqgyv757onv8hdqlq0mbf foreign key (id_usuario) references t_mottu_usuario;