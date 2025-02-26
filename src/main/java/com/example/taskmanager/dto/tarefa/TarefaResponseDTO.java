package com.example.taskmanager.dto.tarefa;

import java.time.LocalDateTime;

public record TarefaResponseDTO(Long id, String titulo, String descricao, LocalDateTime prazo, Boolean concluida, ) {
}
