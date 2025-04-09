package com.example.taskmanager.config.exception;

public class TarefaNotFoundException extends RuntimeException {
    private Long id;

    public TarefaNotFoundException(String criterio) {
        super(criterio);
    }

    public TarefaNotFoundException(Long id) {
        super("Tarefa com ID [" +id+ "] não encontrada");
        this.id = id;
    }
}
