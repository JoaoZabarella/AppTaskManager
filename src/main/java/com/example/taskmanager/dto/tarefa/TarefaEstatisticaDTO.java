package com.example.taskmanager.dto.tarefa;

public record TarefaEstatisticaDTO(
         Long total,
         Long concluidas,
         Long emAndamento,
         Long comPrazo
) {
}
