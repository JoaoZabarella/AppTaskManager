package com.example.taskmanager.config.exception.classes.tarefa;

import com.example.taskmanager.config.exception.classes.base.NotFoundException;

public class ListTaskEmptyException extends NotFoundException {
    public ListTaskEmptyException(String message) {
        super(message);
    }
}
