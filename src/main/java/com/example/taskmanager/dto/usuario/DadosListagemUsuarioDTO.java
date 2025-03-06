package com.example.taskmanager.dto.usuario;

import com.example.taskmanager.model.Usuario;

public record DadosListagemUsuarioDTO(
        Long id,
        String nome,
        String email

) {

    public DadosListagemUsuarioDTO(Usuario usuario){
        this(usuario.getId(),usuario.getNome(),usuario.getEmail());
    }
}
