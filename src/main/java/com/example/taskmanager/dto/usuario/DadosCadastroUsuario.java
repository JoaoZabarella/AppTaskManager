package com.example.taskmanager.dto.usuario;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosCadastroUsuario(
        @NotBlank(message = "O campo usuario não pode estar em branco")
        String nome,

        @NotBlank(message = "O campo email não pode estar em branco") @Email (message = "Digite um email válido")
        String email,

        @NotBlank()
        @Size(min = 6, message = "A senha precisa ter mais de 6 digitos")
        String senha)
{
}