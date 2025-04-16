package com.example.taskmanager.config.exception.classes.usuario;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;
import lombok.Getter;

@Getter
public class EmailNotFoundException extends NotFoundException {
    String email;

    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(String email, String message) {
        super("Email " + email + " não encontrado");
        this.email = email;
    }
}
