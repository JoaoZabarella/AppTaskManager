package com.example.taskmanager.service;

import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository repository;
    private final EntidadeValidator validator;

    public UsuarioAutenticadoService(UsuarioRepository repository, EntidadeValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof Usuario) {
            return (Usuario) principal;
        } else {
            String email = authentication.getName();
            return validator.validarEmailLogin(email);
        }
    }

    public Long obterIdUsuarioAutenticado() {
        return obterUsuarioAutenticado().getId();
    }
}
