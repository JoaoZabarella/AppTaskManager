package com.example.taskmanager.dto.tarefa;


import java.time.OffsetDateTime;

public record DadosCriarTarefa (String titulo,
                                String descricao,
                                Long statusId,
                                Long prioridadeId,
                                OffsetDateTime prazo,
                                Long categoriaId
                                ) {
}
