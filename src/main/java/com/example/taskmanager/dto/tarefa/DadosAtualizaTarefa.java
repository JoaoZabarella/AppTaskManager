package com.example.taskmanager.dto.tarefa;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record DadosAtualizaTarefa (
        Long id,
        String titulo,
        String descricao,
        Long statusId,
        Long prioridadeId,
        OffsetDateTime prazo,
        Long categoriaId
){}
