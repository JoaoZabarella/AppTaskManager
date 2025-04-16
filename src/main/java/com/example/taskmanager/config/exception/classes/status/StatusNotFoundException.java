package com.example.taskmanager.config.exception.classes.status;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;

public class StatusNotFoundException extends NotFoundException {
    private Long id;

    public StatusNotFoundException(String criterio) {
        super(criterio);
    }

    public StatusNotFoundException(Long id){
        super("Status com id de[" + id + "] não encontrada");
        this.id = id;
    }

}
