package com.example.taskmanager.config.exception.classes.usuario;

import com.example.taskmanager.config.exception.classes.base.DuplicateException;
import lombok.Getter;

@Getter
public class EmailDuplicateException extends DuplicateException {
        private String email;

        public EmailDuplicateException(String message){
            super(message);
        }

        public EmailDuplicateException(String email, String message){
            super(message);
            this.email = email;
        }

}
