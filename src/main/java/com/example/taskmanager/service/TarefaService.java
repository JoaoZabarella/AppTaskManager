package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;

import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.model.Usuario;
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


    public TarefaService(TarefaRepository tarefaRepository, EntidadeValidator validator, StatusRepository statusRepository) {
        this.tarefaRepository = tarefaRepository;
        this.validator = validator;
        this.statusRepository = statusRepository;
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, Long usuarioId, Long categoriaId, UriComponentsBuilder uriBuilder) {
        Usuario usuario = validator.validarUsuario(usuarioId);
        Categoria categoria = validator.validarCategoria(categoriaId);

        Status statusNovo = statusRepository.findByTexto("Novo")
                .orElseThrow(() -> new RuntimeException("Status Novo não encontrado"));

        Tarefa tarefa = new Tarefa(dados);
        tarefa.setStatus(statusNovo);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);
        tarefaRepository.save(tarefa);

        URI uri = uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }
}

