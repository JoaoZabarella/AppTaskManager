package com.example.taskmanager.config.exception.classes.tarefa;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;

public class TarefaNotFoundException extends NotFoundException {
    private Long id;

    public TarefaNotFoundException(String criterio) {
        super(criterio);
    }

    public TarefaNotFoundException(Long id) {
        super("Tarefa com ID [" +id+ "] não encontrada");
        this.id = id;
    }
}
