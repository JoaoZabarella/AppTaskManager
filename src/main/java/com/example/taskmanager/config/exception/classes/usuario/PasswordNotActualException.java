package com.example.taskmanager.config.exception.classes.usuario;

public class PasswordNotActualException extends RuntimeException{
    public PasswordNotActualException(String message) {
        super(message);
    }
}
