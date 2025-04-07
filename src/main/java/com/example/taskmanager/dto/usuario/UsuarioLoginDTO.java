package com.example.taskmanager.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record UsuarioLoginDTO(String email, String senha) {
}
