package com.example.taskmanager.config.exception;

import lombok.Getter;

@Getter
public class UsuarioNaoEncontradoException extends RuntimeException {
    private Long id;

    // Construtor que recebe o critério
    public UsuarioNaoEncontradoException(String criterio) {
        super("Usuário com critério [" + criterio + "] não encontrado");
    }


    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário com ID [" + id + "] não encontrado");
        this.id = id;
    }
}