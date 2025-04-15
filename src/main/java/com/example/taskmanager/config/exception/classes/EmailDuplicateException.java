package com.example.taskmanager.config.exception.classes;

import lombok.Getter;

@Getter
public class EmailDuplicateException extends RuntimeException {
        private String email;

        public EmailDuplicateException(String message){
            super(message);
        }

        public EmailDuplicateException(String email, String message){
            super(message);
            this.email = email;
        }

}
