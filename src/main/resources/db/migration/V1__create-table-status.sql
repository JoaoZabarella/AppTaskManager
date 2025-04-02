CREATE TABLE status(
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        texto VARCHAR(50) NOT NULL
);


INSERT INTO status (texto) VALUES
                               ('Novo'),
                               ('Em Andamento'),
                               ('Concluído'),
                               ('Bloqueado'),
                               ('Cancelado');