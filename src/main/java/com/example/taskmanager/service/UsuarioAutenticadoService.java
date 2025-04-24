package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.classes.auth.UsuarioNaoAutenticadoException;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.validator.EntidadeValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAutenticadoService {

    private final EntidadeValidator validator;

    public UsuarioAutenticadoService( EntidadeValidator validator) {
        this.validator = validator;
    }

    public Usuario obterUsuarioAutenticado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UsuarioNaoAutenticadoException("Usuário não está autenticado.");
        }

        var principal = auth.getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario;
        }

        var email = auth.getName();
        return validator.validarEmailLogin(email);
    }

    public Long obterIdUsuarioAutenticado() {
        return obterUsuarioAutenticado().getId();
    }
}