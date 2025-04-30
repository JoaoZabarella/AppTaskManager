package com.example.taskmanager.dto.tarefa;

import java.time.LocalDateTime;

public record DadosAtualizaTarefa (
        Long id,
        String titulo,
        String descricao,
        Long statusId,
        Long prioridadeId,
        LocalDateTime prazo,
        Long categoriaId
){}
