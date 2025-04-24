package com.example.taskmanager.dto.autenticacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosAutenticacao(

        @Email(message = "O e-mail não é válido")
        @NotBlank(message = "O campo e-mail não pode estar em branco")
        String email,

        @NotBlank(message = "O campo senha não pode estar em branco ")
        String senha
)
{}
