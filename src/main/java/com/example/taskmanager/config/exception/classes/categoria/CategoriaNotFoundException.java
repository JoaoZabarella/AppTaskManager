package com.example.taskmanager.config.exception.classes.categoria;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;
import lombok.Getter;

@Getter
public class CategoriaNotFoundException extends NotFoundException {
    private Long id;

    public CategoriaNotFoundException(String criterio){
        super(criterio);
    }

    public CategoriaNotFoundException(Long id) {
        super("Categoria com ID [" + id + "] não encontrada");
        this.id = id;
    }
}
