package com.example.taskmanager.config.exception;

import lombok.Getter;

@Getter
public class UsuarioNaoEncontradoException extends RuntimeException{
    private Long id;
    public UsuarioNaoEncontradoException(String criterio) {
        super("Usuário com critéroo [" + criterio + "] não encontrado" );

    }
}
