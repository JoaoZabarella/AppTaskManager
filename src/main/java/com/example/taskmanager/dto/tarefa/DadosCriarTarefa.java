package com.example.taskmanager.dto.tarefa;

import com.example.taskmanager.model.Prioridade;
import java.time.LocalDateTime;

public record DadosCriarTarefa (String titulo,
                                String descricao,
                                String statusTexto,
                                String prioridadeTexto,
                                LocalDateTime prazo,
                                Prioridade prioridade) {
}
