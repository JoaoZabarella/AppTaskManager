CREATE TABLE prioridade(
            id BIGINT PRIMARY KEY AUTO_INCREMENT,
            texto VARCHAR(50) NOT NULL
);


INSERT INTO prioridade (texto) VALUES
('Baixa'),
('Média'),
('Alta'),
('Urgente');