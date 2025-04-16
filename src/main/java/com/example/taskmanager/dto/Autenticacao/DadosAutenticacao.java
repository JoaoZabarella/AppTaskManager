package com.example.taskmanager.dto.Autenticacao;

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
