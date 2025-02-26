package com.example.taskmanager.service;

import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.StatusRespoistory;
import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastraUsuario(Usuario usuario) {
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }





}
