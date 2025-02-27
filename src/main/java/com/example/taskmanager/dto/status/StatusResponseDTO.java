package com.example.taskmanager.dto.status;

import com.example.taskmanager.model.Status;

public record StatusResponseDTO(Long id, String nome) {
    public StatusResponseDTO(Status status) {
        this(status.getId(), status.getTexto());
    }
}
