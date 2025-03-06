package com.example.taskmanager.service;

import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioValidator {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Verifica se já tem o email cadastrado
    public void validarEmail(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("Email já cadastrado");
        }

    }
}
