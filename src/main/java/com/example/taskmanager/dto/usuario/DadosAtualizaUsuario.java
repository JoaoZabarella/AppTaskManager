package com.example.taskmanager.dto.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizaUsuario(
        Long id,
        String nome,
        String email
) {
}
