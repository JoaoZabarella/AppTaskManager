package com.example.taskmanager.config.exception.classes.tarefa;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;

public class TarefaNotFoundException extends NotFoundException {

    public TarefaNotFoundException(String criterio) {
        super(criterio);
    }

}
