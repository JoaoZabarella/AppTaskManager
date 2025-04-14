package com.example.taskmanager.validator;

import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.PrioridadeRepository;
import com.example.taskmanager.repository.StatusRepository;
import com.example.taskmanager.repository.TarefaRepository;
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
        return validator.validarTarefa(tarefaId);
    }
    public Status validadorObterStatus(String status) {
        return validator.validarStatus(status);
    }

    public Prioridade validadorObterPrioridade(String prioridade) {
        return validator.validarPrioridade(prioridade);
    }

    public Usuario validadorObterUsuario(Long usuarioId) {
        return validator.validarUsuario(usuarioId);
    }

    public Categoria validadorObterCategoria(Long categoriaId) {
        return validator.validarCategoria(categoriaId);
    }


    public void atualizarCampos(Tarefa tarefa, DadosAtualizaTarefa dados){

    }

    public void atualizarTitulo(Tarefa tarefa, String titulo){
        if(titulo != null) {
            tarefa.setTitulo(titulo);
        }
    }

    public void atualizarDescricao(Tarefa tarefa, String descricao){
        if(descricao != null) {
            tarefa.setDescricao(descricao);
        }
    }

    public void atualizarStatus(Tarefa tarefa, String status){
        if(status != null) {
            tarefa.setStatus(validator.validarStatus(status));
        }
    }

    public void atualizarPrioridade(Tarefa tarefa, String prioridade){
        if(prioridade != null){
            tarefa.setPrioridade(validator.validarPrioridade(prioridade));
        }
    }

    public void atualizarPrazo(Tarefa tarefa, LocalDateTime prazo){
        if(prazo != null) {
            tarefa.setPrazo(prazo);
        }
    }

    public void validarTarefaNaoConcluida(Tarefa tarefa){
        if(tarefa.isConcluida()){
            throw new RuntimeException("Esta tarefa já esta concluida");
        }
    }








}
