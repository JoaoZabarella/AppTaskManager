package com.example.taskmanager.dto.prioridade;

import com.example.taskmanager.model.Prioridade;

public record PrioridadeResponseDTO(Long id, String texto) {
    public PrioridadeResponseDTO(Prioridade prioridade){
        this(prioridade.getId(), prioridade.getTexto());
    }
}
