package com.example.taskmanager.dto.autenticacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacao(

        @Email
        @NotBlank
        String email,

        @NotBlank
        String senha
)
{}
