package com.example.taskmanager.config.exception.classes.prioridade;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;

public class PrioridadeNotFoundException extends NotFoundException {
    private Long id;

    public PrioridadeNotFoundException(String criterio) {
        super(criterio);
    }

    public PrioridadeNotFoundException(Long id) {
        super("Prioridade com id de [" + id +"] não encontrado");
        this.id = id;
    }
}
