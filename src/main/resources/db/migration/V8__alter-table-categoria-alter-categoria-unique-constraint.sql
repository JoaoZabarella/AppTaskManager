ALTER TABLE categoria DROP INDEX nome;

ALTER TABLE categoria ADD CONSTRAINT uk_nome_usuario UNIQUE (nome, usuario_id);