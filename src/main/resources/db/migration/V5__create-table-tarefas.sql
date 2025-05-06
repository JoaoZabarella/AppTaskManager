CREATE TABLE tarefas(
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        titulo VARCHAR(255) NOT NULL,
                        descricao TEXT,
                        categoria_id BIGINT,
                        usuario_id BIGINT NOT NULL,
                        status_id BIGINT NOT NULL,
                        prioridade_id BIGINT NOT NULL,
                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        data_conclusao TIMESTAMP NULL,
                        prazo TIMESTAMP NULL,
                        ativo BOOLEAN DEFAULT TRUE,
                        FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE SET NULL,
                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                        FOREIGN KEY (status_id) REFERENCES status(id) ON DELETE CASCADE,
                        FOREIGN KEY (prioridade_id) REFERENCES prioridade(id) ON DELETE CASCADE
)