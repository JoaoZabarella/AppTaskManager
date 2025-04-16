package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.TarefaRepository;
import com.example.taskmanager.validator.TarefaValidatorService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class TarefaService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final TarefaRepository tarefaRepository;
    private final TarefaValidatorService validatorService;

    public TarefaService(TarefaRepository tarefaRepository, TarefaValidatorService validatorService) {
        this.tarefaRepository = tarefaRepository;
        this.validatorService = validatorService;
    }


    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, Long usuarioId, Long categoriaId, UriComponentsBuilder uriBuilder) {
        logger.info("Criando Tarefa");

        Usuario usuario = validatorService.validadorObterUsuario(usuarioId);
        Categoria categoria = validatorService.validadorObterCategoria(categoriaId);
        Status statusNovo = validatorService.validadorObterStatus(dados.statusTexto());
        Prioridade prioridadeNovo = validatorService.validadorObterPrioridade(dados.prioridadeTexto());

        Tarefa tarefa = new Tarefa(dados);

        tarefa.setStatus(statusNovo);
        tarefa.setPrioridade(prioridadeNovo);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);
        tarefaRepository.save(tarefa);

        URI uri = uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.getId()).toUri();

        logger.info("Tarefa com ID {} criada com sucesso", tarefa.getId());
        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }

    public ResponseEntity<Page<DadosListagemTarefa>> listarTarefasAtivas(Long usuarioId, Pageable pageable) {

        Page<DadosListagemTarefa> tarefas = tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)
                .map(DadosListagemTarefa::new);

        return ResponseEntity.ok(tarefas);

    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> atualizarTarefa(Long tarefaId, DadosAtualizaTarefa dados) {
        var tarefa = validatorService.validadorObterTarefa(tarefaId);
        validatorService.atualizarCampos(tarefa, dados);
        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }

    @Transactional
    public ResponseEntity<Void> excluirTarefa(Long id) {
        Tarefa tarefa = validatorService.validadorObterTarefa(id);
        tarefa.desativar();

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(Long tarefaId) {
        var tarefa = validatorService.validadorObterTarefa(tarefaId);
        Status statusConcluido = validatorService.validadorObterStatus("Concluido");

        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));

    }

}

