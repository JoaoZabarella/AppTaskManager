package com.example.taskmanager.config.exception.classes;

import lombok.Getter;

@Getter
public class UsuarioNotFoundException extends RuntimeException {
    private Long id;


    public UsuarioNotFoundException(String criterio) {
        super("Usuário com critério [" + criterio + "] não encontrado");
    }


    public UsuarioNotFoundException(Long id) {
        super("Usuário com ID [" + id + "] não encontrado");
        this.id = id;
    }
}