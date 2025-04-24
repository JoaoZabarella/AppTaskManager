package com.example.taskmanager.config.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class ErroResponse {
    private String message;
    private Map<String, Object> details;

    public ErroResponse(String message, Map<String, Object> details) {
        this.message = message;
        this.details = details;
    }
}
