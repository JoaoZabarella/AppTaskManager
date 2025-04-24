package com.example.taskmanager.dto.usuario;

import com.example.taskmanager.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


public record DadosListagemUsuarioDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime dataCriacao,
        Boolean ativo,
        Set<String> roles


) {
    public DadosListagemUsuarioDTO(Usuario usuario){
        this(usuario.getId(),usuario.getNome(),usuario.getEmail(), usuario.getDataCriacao(), usuario.isAtivo(), usuario.getRoles());
    }
}
