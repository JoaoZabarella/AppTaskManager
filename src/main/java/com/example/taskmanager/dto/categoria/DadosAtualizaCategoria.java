package com.example.taskmanager.dto.categoria;

import jakarta.validation.constraints.Size;

public record DadosAtualizaCategoria(
        @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres")
        String nome
) {
}
