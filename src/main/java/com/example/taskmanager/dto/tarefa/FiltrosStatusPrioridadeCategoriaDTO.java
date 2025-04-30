package com.example.taskmanager.dto.tarefa;

import java.util.List;

public record FiltrosStatusPrioridadeCategoriaDTO(
        Long statusId,
        Long prioridadeId,
        Long categoriaId
) { }
