package com.example.taskmanager.dto.usuario;

import com.example.taskmanager.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public record DadosListagemUsuarioDTO(
        Long id,
        String nome,
        String email

) {

    public DadosListagemUsuarioDTO(Usuario usuario){
        this(usuario.getId(),usuario.getNome(),usuario.getEmail());
    }
}
