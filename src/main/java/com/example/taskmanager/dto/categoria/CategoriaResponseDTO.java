package com.example.taskmanager.dto.categoria;

import com.example.taskmanager.model.Categoria;

public record CategoriaResponseDTO(Long id, String nome) {
    public CategoriaResponseDTO(Categoria categoria) {
        this(categoria.getId(), categoria.getNome());
    }
}
