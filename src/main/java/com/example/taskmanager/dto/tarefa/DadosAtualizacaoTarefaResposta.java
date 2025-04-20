package com.example.taskmanager.dto.tarefa;

import java.util.List;

public record DadosAtualizacaoTarefaResposta(
        DadosListagemTarefa tarefa,
        List<String> camposAtualizados,
        String mensagem
) { }
