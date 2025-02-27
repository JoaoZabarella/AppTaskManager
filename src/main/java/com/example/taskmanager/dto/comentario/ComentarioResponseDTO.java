package com.example.taskmanager.dto.comentario;

import com.example.taskmanager.model.Comentario;
import com.example.taskmanager.model.Usuario;

import java.time.LocalDateTime;

public record ComentarioResponseDTO(Long id, String texto, String nomeUsuario, LocalDateTime dataCriacao) {

    public ComentarioResponseDTO(Comentario comentario) {
        this(comentario.getId(),
             comentario.getTexto(),
             comentario.getUsuario().getNome(),
             comentario.getDataCriacao());
}
}
