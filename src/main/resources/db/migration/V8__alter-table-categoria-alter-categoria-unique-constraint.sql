DROP INDEX IF EXISTS nome ON categoria;

ALTER TABLE categoria ADD CONSTRAINT uk_nome_usuario UNIQUE (nome, usuario_id);
