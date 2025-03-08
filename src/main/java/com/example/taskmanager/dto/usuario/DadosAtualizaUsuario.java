package com.example.taskmanager.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public record DadosAtualizaUsuario(
        Long id,
        String nome,
        String email
) {
}
