package com.example.taskmanager.config.exception;

public class PrioridadeNotFoundException extends RuntimeException {
    private Long id;
    public PrioridadeNotFoundException(String criterio) {
        super(criterio);
    }
    public PrioridadeNotFoundException(Long id) {
        super("Prioridade com id de [" + id +"] não encontrado");
        this.id = id;
    }
}
