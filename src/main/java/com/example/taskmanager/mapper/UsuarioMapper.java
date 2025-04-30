package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioMapper {
    private final UsuarioRepository repository;

    public UsuarioMapper(UsuarioRepository repository) {
        this.repository = repository;
    }

    public static Usuario toEntity(DadosCadastroUsuario dto, PasswordEncoder passwordEncoder) {
        if (dto.email() == null || dto.email().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        return new Usuario(dto, passwordEncoder);
    }

}

