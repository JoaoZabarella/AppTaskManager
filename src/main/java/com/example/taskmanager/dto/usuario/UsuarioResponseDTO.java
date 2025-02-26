package com.example.taskmanager.dto.usuario;

import com.example.taskmanager.model.Usuario;

public record UsuarioResponseDTO(Long id, String nome, String email) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
