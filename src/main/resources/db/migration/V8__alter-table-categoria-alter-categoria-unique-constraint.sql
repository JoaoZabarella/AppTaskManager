DROP INDEX nome ON categoria;

ALTER TABLE categoria ADD CONSTRAINT UK_categoria_nome UNIQUE (nome);