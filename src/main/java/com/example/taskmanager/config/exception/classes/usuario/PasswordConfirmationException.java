package com.example.taskmanager.config.exception.classes.usuario;

import lombok.Getter;

@Getter
public class PasswordConfirmationException extends RuntimeException {
    String senha;
    String confirmSenha;

    public PasswordConfirmationException(String message) {
        super(message);
    }

    public PasswordConfirmationException(String message, String senha, String confirmSenha) {
        super(message);
        this.senha = senha;
        this.confirmSenha = confirmSenha;
    }


}
