package com.example.taskmanager.config.exception.classes.usuario;

import lombok.Getter;

@Getter
public class UsernameExistException extends RuntimeException{
    private String nome;

    public UsernameExistException(String message) {
        super(message);
    }

    public UsernameExistException(String nome, String message) {
        super(message);
        this.nome = nome;
    }
}
