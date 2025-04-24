package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.mapper.TarefaMapper;
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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TarefaService.class);

    private final TarefaRepository tarefaRepository;
    private final TarefaMapper mapper;
    private final TarefaValidatorService validatorService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public TarefaService(TarefaRepository tarefaRepository, TarefaMapper mapper, TarefaValidatorService validatorService, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.tarefaRepository = tarefaRepository;
        this.mapper = mapper;
        this.validatorService = validatorService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    private Long obterusuario() {
        return usuarioAutenticadoService.obterIdUsuarioAutenticado();
    }


    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, Long categoriaId, UriComponentsBuilder uriBuilder) {
        logger.info("Usuário com ID {} está criando uma tarefa", obterusuario());

        Tarefa tarefa = mapper.prepararTarefa(dados, categoriaId);
        tarefaRepository.save(tarefa);
        URI uri = mapper.construirUri(tarefa, uriBuilder);

        logger.info("Tarefa com ID {} criada com sucesso", tarefa.getId());
        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }


    public ResponseEntity<PaginadoTarefaDTO> listarTarefasAtivas(Pageable pageable) {
        logger.info("Buscando tarefas para serem listadas");


        Page<DadosListagemTarefa> tarefas = tarefaRepository.findByUsuarioIdAndAtivoTrue(obterusuario(), pageable)
                .map(DadosListagemTarefa::new);
        PaginadoTarefaDTO resultado = PaginadoTarefaDTO.from(tarefas);

        logger.info("Tarefas do usuario com ID {} retornadas com sucesso!", obterusuario());
        return ResponseEntity.ok(resultado);

    }

    @Transactional
    public ResponseEntity<DadosAtualizacaoTarefaResposta> atualizarTarefa(Long tarefaId, DadosAtualizaTarefa dados) {

        var tarefa = validatorService.validadosObterTarefaDoUsuario(tarefaId, obterusuario());
        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(validatorService.atualizacaoTarefaComDados(tarefa, dados));
    }

    @Transactional
    public ResponseEntity<Void> excluirTarefa(Long tarefaId) {
        Tarefa tarefa = validatorService.validaEObterTarefa(tarefaId, obterusuario());
        tarefa.desativar();
        tarefaRepository.save(tarefa);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(Long tarefaId) {


        var tarefa = validatorService.validaEObterTarefa(tarefaId, obterusuario());
        Status statusConcluido = validatorService.validadorObterStatus("Concluido");

        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));

    }

}

