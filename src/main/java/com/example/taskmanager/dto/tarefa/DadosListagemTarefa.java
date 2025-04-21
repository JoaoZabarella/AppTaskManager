package com.example.taskmanager.dto.tarefa;

import com.example.taskmanager.model.*;
import java.time.LocalDateTime;

public record DadosListagemTarefa(
        Long id,
        String titulo,
        String descricao,
        String status,
        String prioridade,
        LocalDateTime dataCriacao,
        LocalDateTime prazo,
        LocalDateTime dataConclusao,
        String usuarioNome,
        String categoriaNome
) {

    public DadosListagemTarefa(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus() != null ? tarefa.getStatus().getTexto() : "Sem status",
                tarefa.getPrioridade() != null ? tarefa.getPrioridade().getTexto() : "Sem prioridade",
                tarefa.getDataCriacao(),
                tarefa.getPrazo(),
                tarefa.getDataConclusao(),
                tarefa.getUsuario() != null ? tarefa.getUsuario().getNome() : "Usuário desconhecido",
                tarefa.getCategoria() != null ? tarefa.getCategoria().getNome() : "Sem categoria"
        );
    }
}
