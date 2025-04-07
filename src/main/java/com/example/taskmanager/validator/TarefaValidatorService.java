package com.example.taskmanager.validator;

import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.PrioridadeRepository;
import com.example.taskmanager.repository.StatusRepository;
import com.example.taskmanager.repository.TarefaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TarefaValidatorService {

    private final TarefaRepository tarefaRepository;
    private final StatusRepository statusRepository;
    private final PrioridadeRepository prioridadeRepository;
    private final EntidadeValidator validator;

    public TarefaValidatorService(TarefaRepository tarefaRepository,
                                  StatusRepository statusRepository,
                                  PrioridadeRepository prioridadeRepository,
                                  EntidadeValidator validator) {
        this.tarefaRepository = tarefaRepository;
        this.statusRepository = statusRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.validator = validator;
    }

    public Tarefa validadorObterTarefa(Long tarefaId) {
        return tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não localizada"));
    }
    public Status validadorObterStatus(String status) {
        return statusRepository.findByTextoIgnoreCase(status)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));
    }

    public Prioridade validadorObterPrioridade(String prioridade) {
        return prioridadeRepository.findByTextoIgnoreCase(prioridade)
                .orElseThrow(() -> new RuntimeException("Prioridade não encontrada"));
    }

    public Usuario validadorObterUsuario(Long usuarioId) {
        return validator.validarUsuario(usuarioId);
    }

    public Categoria validadorObterCategoria(Long categoriaId) {
        if(categoriaId == null) {
            return null;
        }
        return validator.validarCategoria(categoriaId);
    }


    public void atualizarCampos(Tarefa tarefa, DadosAtualizaTarefa dados) {
        atualizarTitulo(tarefa, dados.titulo());
    }

    private void atualizarTitulo(Tarefa tarefa, String titulo) {
        if(titulo != null) {
            tarefa.setTitulo(titulo);
        }
    }

    private void atualizarDescricao(Tarefa tarefa, String descricao) {
        if(descricao != null){
            tarefa.setDescricao(descricao);
        }
    }

    private void atualizarStatus(Tarefa tarefa, String statusTexto) {
        if(statusTexto != null){
            tarefa.setStatus(validadorObterStatus(statusTexto));
        }
    }

    private void atualizarPrioridade(Tarefa tarefa, String prioridadeTexto){
        if(prioridadeTexto != null){
            tarefa.setPrioridade(validadorObterPrioridade(prioridadeTexto));
        }
    }

    private void atualizarPrazo(Tarefa tarefa, LocalDateTime prazo){
        if(prazo != null){
            tarefa.setPrazo(prazo);
        }
    }








}
