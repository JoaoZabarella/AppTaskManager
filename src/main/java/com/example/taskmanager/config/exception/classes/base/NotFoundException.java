package com.example.taskmanager.config.exception.classes.base;

public abstract class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }
}
