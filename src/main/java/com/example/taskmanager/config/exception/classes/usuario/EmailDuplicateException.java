package com.example.taskmanager.config.exception.classes.usuario;

import com.example.taskmanager.config.exception.classes.base.DuplicateException;
import lombok.Getter;

@Getter
public class EmailDuplicateException extends DuplicateException {
        public EmailDuplicateException(String message){
            super(message);
        }

}
