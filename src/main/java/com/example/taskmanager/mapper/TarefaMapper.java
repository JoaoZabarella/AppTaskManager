package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.dto.tarefa.FiltrosStatusPrioridadeCategoriaDTO;
import com.example.taskmanager.model.*;
import com.example.taskmanager.service.UsuarioAutenticadoService;
import com.example.taskmanager.validator.EntidadeValidator;
import com.example.taskmanager.validator.TarefaValidatorService;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Component
public class TarefaMapper {
    
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final EntidadeValidator validator;

    public TarefaMapper(UsuarioAutenticadoService usuarioAutenticadoService, EntidadeValidator validator) {
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.validator = validator;
    }


    public Tarefa prepararTarefa(DadosCriarTarefa dados){
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();


        Categoria categoria = dados.categoriaId() !=null ? validator.validarCategoria(dados.categoriaId()) : null;
        Status status = validator.validarStatus(dados.statusId());
        Prioridade prioridade = validator.validarPrioridade(dados.prioridadeId());

        Tarefa tarefa = new Tarefa(dados);
        tarefa.setStatus(status);
        tarefa.setPrioridade(prioridade);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);

        return tarefa;
    }

    public URI construirUri(Tarefa tarefa, UriComponentsBuilder uriBuilder){
        return uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.getId()).toUri();
    }

    public boolean filtroVazio(FiltrosStatusPrioridadeCategoriaDTO filtro){
        return (filtro.statusId() == null ) &&
                (filtro.prioridadeId() == null) &&
                filtro.categoriaId() == null;
    }

    public Tarefa prepararTarefaParaReabertura(Tarefa tarefa, Status statusEmAndamento){
        tarefa.setStatus(statusEmAndamento);
        tarefa.reabrir();
        return tarefa;
    }

    public List<Tarefa> filtrarTarefasParaReabrir(List<Tarefa> tarefas, Status statusEmAndamento, TarefaValidatorService service){
        List<Tarefa> tarefasParaReabrir = new ArrayList<>();

        for(Tarefa tarefa : tarefas){
            if(service.isTarefaConcluida(tarefa)){
                prepararTarefaParaReabertura(tarefa, statusEmAndamento);
                tarefasParaReabrir.add(tarefa);
            }
        }

        return tarefasParaReabrir;
    }


    public List<DadosListagemTarefa> converterParaDTOs(List<Tarefa> tarefas){
        return tarefas.stream()
                .map(DadosListagemTarefa::new)
                .toList();
    }
}
