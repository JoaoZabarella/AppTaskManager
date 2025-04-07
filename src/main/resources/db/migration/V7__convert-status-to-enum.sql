-- Adicionar coluna temporária para o valor enum do status
ALTER TABLE tarefas ADD COLUMN status_enum VARCHAR(50);

-- Mapear os valores atuais para os valores de enum
UPDATE tarefas t
    JOIN status s ON t.status_id = s.id
SET t.status_enum =
        CASE
            WHEN s.texto = 'Novo' THEN 'NOVO'
            WHEN s.texto = 'Em Andamento' THEN 'EM_ANDAMENTO'
            WHEN s.texto = 'Concluído' THEN 'CONCLUIDO'
            WHEN s.texto = 'Bloqueado' THEN 'BLOQUEADO'
            WHEN s.texto = 'Cancelado' THEN 'CANCELADO'
            ELSE 'NOVO'
            END;


SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'tarefas'
      AND COLUMN_NAME = 'status_id'
      AND REFERENCED_TABLE_NAME = 'status'
);


SET @sql = CONCAT('ALTER TABLE tarefas DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


ALTER TABLE tarefas DROP COLUMN status_id;

ALTER TABLE tarefas CHANGE status_enum status VARCHAR(50) NOT NULL;