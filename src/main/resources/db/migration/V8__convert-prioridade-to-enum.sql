
ALTER TABLE tarefas ADD COLUMN prioridade_enum VARCHAR(50);

UPDATE tarefas t
    JOIN prioridade p ON t.prioridade_id = p.id
SET t.prioridade_enum =
        CASE
            WHEN p.texto = 'Baixa' THEN 'BAIXA'
            WHEN p.texto = 'Média' THEN 'MEDIA'
            WHEN p.texto = 'Alta' THEN 'ALTA'
            WHEN p.texto = 'Urgente' THEN 'URGENTE'
            ELSE 'MEDIA'
            END;


SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'tarefas'
      AND COLUMN_NAME = 'prioridade_id'
      AND REFERENCED_TABLE_NAME = 'prioridade'
);


SET @sql = CONCAT('ALTER TABLE tarefas DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


ALTER TABLE tarefas DROP COLUMN prioridade_id;


ALTER TABLE tarefas CHANGE prioridade_enum prioridade VARCHAR(50) NOT NULL;