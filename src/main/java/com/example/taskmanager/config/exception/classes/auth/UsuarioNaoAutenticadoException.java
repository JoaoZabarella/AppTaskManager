package com.example.taskmanager.config.exception.classes.auth;

import com.example.taskmanager.config.exception.classes.base.BusinessException;

public class UsuarioNaoAutenticadoException extends BusinessException {
    public UsuarioNaoAutenticadoException(String message) {
        super(message);
    }
}
