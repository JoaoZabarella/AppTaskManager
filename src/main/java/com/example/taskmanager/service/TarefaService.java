package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;

import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.repository.TarefaRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, UriComponentsBuilder uriBuilder){
        var tarefa = new Tarefa(dados);

        tarefaRepository.save(tarefa);

        URI uri = uriBuilder.path("/tarefa/{id}").build(tarefa.getId());
        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }


}
