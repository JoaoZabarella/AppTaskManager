package com.example.taskmanager.config.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class ErroResponse {
    private String message;
    private Map details;

    public ErroResponse(String message, Map details) {
        this.message = message;
        this.details = details;
    }
}
