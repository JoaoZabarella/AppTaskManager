package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.PrioridadeRepository;
import com.example.taskmanager.repository.StatusRepository;
import com.example.taskmanager.repository.TarefaRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final EntidadeValidator validator;
    private final StatusRepository statusRepository;
    private final PrioridadeRepository prioridadeRepository;


    public TarefaService(TarefaRepository tarefaRepository, EntidadeValidator validator, StatusRepository statusRepository, PrioridadeRepository prioridadeRepository) {
        this.tarefaRepository = tarefaRepository;
        this.validator = validator;
        this.statusRepository = statusRepository;
        this.prioridadeRepository = prioridadeRepository;
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, Long usuarioId, Long categoriaId, UriComponentsBuilder uriBuilder) {
        Usuario usuario = validator.validarUsuario(usuarioId);
        Categoria categoria = validator.validarCategoria(categoriaId);

        //Por enquanto vou deixar a lógica dessas duas validações aqui mesmo, em alterações futuras vou realizar a separação de classes para seguir os principios SOLID.
        Status statusNovo = statusRepository.findByTextoIgnoreCase(dados.statusTexto())
                .orElseThrow(() -> new RuntimeException("Status Novo não encontrado"));
        //Por enquanto vou deixar a lógica dessas duas validações aqui mesmo, em alterações futuras vou realizar a separação de classes para seguir os principios SOLID.
        Prioridade prioridadeNovo = prioridadeRepository.findByTextoIgnoreCase(dados.prioridadeTexto())
                .orElseThrow(() -> new RuntimeException("Prioridade não encontrado"));

        Tarefa tarefa = new Tarefa(dados);

        tarefa.setStatus(statusNovo);
        tarefa.setPrioridade(prioridadeNovo);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);
        tarefaRepository.save(tarefa);

        URI uri = uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(Long tarefaId){
        var tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        Status statusConcluido = statusRepository.findByTextoIgnoreCase("Concluido")
                .orElseThrow(() -> new RuntimeException("Status Concluido não encontrado"));

        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));

    }
}

