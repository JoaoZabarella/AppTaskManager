package com.example.taskmanager.dto.tarefa;

import com.example.taskmanager.model.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record DadosListagemTarefa(
        Long id,
        String titulo,
        String descricao,
        Long statusId,
        String statusTexto,
        Long prioridadeId,
        String prioridadeTexto,
        OffsetDateTime dataCriacao,
        OffsetDateTime prazo,
        OffsetDateTime dataConclusao,
        String usuarioNome,
        Long categoriaId,
        String categoriaNome,
        String concluida

) {
    public DadosListagemTarefa(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus() != null ? tarefa.getStatus().getId() : null,
                tarefa.getStatus() != null ? tarefa.getStatus().getTexto() : "Sem status",
                tarefa.getPrioridade() != null ? tarefa.getPrioridade().getId() : null,
                tarefa.getPrioridade() != null ? tarefa.getPrioridade().getTexto() : "Sem prioridade",
                tarefa.getDataCriacao(),
                tarefa.getPrazo(),
                tarefa.getDataConclusao() ,
                tarefa.getUsuario() != null ? tarefa.getUsuario().getNome() : "Usuário desconhecido",
                tarefa.getCategoria() != null ? tarefa.getCategoria().getId() : null,
                tarefa.getCategoria() != null ? tarefa.getCategoria().getNome() : "Sem categoria",
                tarefa.isConcluida() ? "Tarefa concluida" : "Tarefa em aberto"
        );
    }
}