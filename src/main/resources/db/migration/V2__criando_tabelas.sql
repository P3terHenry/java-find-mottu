create table t_mottu_cargo (
    id_cargo bigint identity(1,1) primary key,
    nome_cargo varchar(20) not null,
    constraint chk_nome_cargo check (nome_cargo in ('ADMIN','AUXILIAR','CEO','COORDENADOR','ESTAGIARIO','GERENTE','MECANICO','SUPERVISOR'))
);

create table t_mottu_filiais (
    id_filial bigint identity(1,1) primary key,
    end_filial varchar(255) not null
);

create table t_mottu_localizacoes (
    id_localizacao bigint identity(1,1) primary key,
    pos_x decimal(4,3) not null,
    pos_y decimal(4,3) not null,
    status_localizacao integer not null,
    data_localizacao datetime2(6) not null,
    id_moto bigint,
    id_imagem varchar(255) not null
);

create table t_mottu_motos (
    id_moto bigint identity(1,1) primary key,
    status_moto integer not null,
    id_filial bigint not null,
    id_imei bigint,
    num_motor bigint,
    id_qr_code varchar(255),
    modelo_moto varchar(255),
    num_chassi varchar(255),
    placa_moto varchar(255)
);

create table t_mottu_usuario (
    id_usuario bigint identity(1,1) primary key,
    idade integer not null,
    id_filial bigint not null,
    senha varchar(60) not null,
    primeiro_nome varchar(50) not null,
    sobrenome varchar(50) not null,
    email varchar(100) not null
);

create table t_usuario_cargo (
    id_cargo bigint not null,
    id_usuario bigint not null,
    primary key (id_cargo, id_usuario)
);

