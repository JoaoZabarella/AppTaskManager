package com.example.taskmanager.dto.tarefa;

import java.time.LocalDateTime;

public record DadosAtualizaTarefa (
        Long id,
        String titulo,
        String descricao,
        String statusTexto,
        String prioridadeTexto,
        LocalDateTime prazo
){}
