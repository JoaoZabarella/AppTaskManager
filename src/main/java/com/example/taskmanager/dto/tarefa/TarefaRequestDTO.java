package com.example.taskmanager.dto.tarefa;

import java.time.LocalDateTime;

public record TarefaRequestDTO(String titulo, String descricao, LocalDateTime prazo, Boolean concluida) {
}
