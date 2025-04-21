package com.example.taskmanager.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senha")
public class GeradorSenhaController {

    private final PasswordEncoder passwordEncoder;

    public GeradorSenhaController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
