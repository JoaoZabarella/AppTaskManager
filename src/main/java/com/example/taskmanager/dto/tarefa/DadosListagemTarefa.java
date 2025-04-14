package com.example.taskmanager.dto.tarefa;

import com.example.taskmanager.dto.comentario.ComentarioResponseDTO;
import com.example.taskmanager.model.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record DadosListagemTarefa(
        Long id,
        String titulo,
        String descricao,
        Status status,
        Prioridade prioridade,
        LocalDateTime dataCriacao,
        LocalDateTime dataConclusao,
        Usuario usuario,
        Categoria categoria,
        List<ComentarioResponseDTO> comentarios
) {

    public DadosListagemTarefa(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrioridade(),
                tarefa.getDataCriacao(),
                tarefa.getDataConclusao(),
                tarefa.getUsuario(),
                tarefa.getCategoria(),
                tarefa.getComentarios() != null
                        ? tarefa.getComentarios().stream().map(ComentarioResponseDTO::new).collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }
}
