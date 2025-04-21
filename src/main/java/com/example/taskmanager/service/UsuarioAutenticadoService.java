package com.example.taskmanager.service;

import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.validator.EntidadeValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAutenticadoService {

    private final EntidadeValidator validator;

    public UsuarioAutenticadoService( EntidadeValidator validator) {
        this.validator = validator;
    }

    public Usuario obterUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = auth.getPrincipal();

        if(principal instanceof Usuario usuario) {
            return usuario;
        } else {
            String email = auth.getName();
            return validator.validarEmailLogin(email);
        }
    }

    public Long obterIdUsuarioAutenticado() {
        return obterUsuarioAutenticado().getId();
    }
}
