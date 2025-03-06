package com.example.taskmanager.config.exception;


public class UsuarioNaoEncontradoException extends RuntimeException{
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário do ID  " + id + " não encontrado" );

    }
}
