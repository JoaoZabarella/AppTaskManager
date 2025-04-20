package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.model.*;
import com.example.taskmanager.service.UsuarioAutenticadoService;
import com.example.taskmanager.validator.EntidadeValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;


@Component
public class TarefaMapper {
    
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final EntidadeValidator validator;

    public TarefaMapper(UsuarioAutenticadoService usuarioAutenticadoService, EntidadeValidator validator) {
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.validator = validator;
    }


    public Tarefa prepararTarefa(DadosCriarTarefa dados, Long categoriaId){
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Long usuarioId = usuario.getId();

        Categoria categoria = validator.validarCategoria(categoriaId);
        Status statusNovo = validator.validarStatus(dados.statusTexto());
        Prioridade prioridadeNovo = validator.validarPrioridade(dados.prioridadeTexto());

        Tarefa tarefa = new Tarefa(dados);
        tarefa.setStatus(statusNovo);
        tarefa.setPrioridade(prioridadeNovo);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);

        return tarefa;
    }

    public URI construirUri(Tarefa tarefa, UriComponentsBuilder uriBuilder){
        return uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.getId()).toUri();
    }

}
