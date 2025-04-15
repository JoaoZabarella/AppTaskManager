package com.example.taskmanager.config.exception.classes;

import lombok.Getter;

@Getter
public class CategoriaNotFoundException extends RuntimeException {
    private Long id;

    public CategoriaNotFoundException(String criterio){
        super(criterio);
    }

    public CategoriaNotFoundException(Long id) {
        super("Categoria com ID [" + id + "] não encontrada");
        this.id = id;
    }
}
