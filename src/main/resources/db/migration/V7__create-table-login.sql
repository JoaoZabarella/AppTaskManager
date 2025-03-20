CREATE TABLE login(
    id bigint not null AUTO_INCREMENT,
    login varchar(255) not null,
    password varchar(255) not null,

    primary key (id)
);