package com.example.taskmanager.config.exception.classes.base;

public abstract class DuplicateException extends BusinessException{
    public DuplicateException(String message) {
        super(message);
    }
}
