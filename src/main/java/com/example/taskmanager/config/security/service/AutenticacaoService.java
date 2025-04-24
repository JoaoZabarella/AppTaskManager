package com.example.taskmanager.config.security.service;
import com.example.taskmanager.config.exception.classes.auth.CredenciaisInvalidasException;
import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .map(usuario -> new User(
                        usuario.getEmail(),
                        usuario.getSenha(),
                        Collections.emptyList()))
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais Invalidas"));
    }
}

