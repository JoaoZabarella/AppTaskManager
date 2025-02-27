package com.example.taskmanager.service;

import com.example.taskmanager.dto.usuario.UsuarioDTO;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.StatusRespoistory;
import com.example.taskmanager.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario cadastraUsuario(UsuarioDTO usuarioDto) {
        var usuario = new Usuario();
        usuario.setNome(usuarioDto.nome());
        usuario.setEmail(usuarioDto.email());
        usuario.setSenha(usuarioDto.senha());


        if (usuarioRepository.existsByEmail(usuarioDto.email())){
            throw new RuntimeException("E-mail ja cadastrado");
        }

        return usuarioRepository.save(usuario);
    }





}
