package com.example.taskmanager.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosCriarCategoria(
        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(max = 100, message = "O nome da categoria não pode ter mais que 100 caracteres")
        String nome
) {
}
