package com.example.taskmanager.config.exception.classes;

public class StatusNotFoundException extends RuntimeException{
    private Long id;

    public StatusNotFoundException(String criterio) {
        super(criterio);
    }

    public StatusNotFoundException(Long id){
        super("Status com id de[" + id + "] não encontrada");
        this.id = id;
    }

}
