package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.classes.tarefa.ListTaskEmptyException;
import com.example.taskmanager.dto.tarefa.ArquivarMultiplasTarefasDTO;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.dto.tarefa.PaginadoTarefaDTO;
import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.repository.TarefaRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefasArquivasService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TarefasArquivasService.class);

    private final TarefaRepository repository;
    private final UsuarioAutenticadoService service;
    private final EntidadeValidator validator;


    public TarefasArquivasService(TarefaRepository repository, UsuarioAutenticadoService service, EntidadeValidator validator) {
        this.repository = repository;
        this.service = service;
        this.validator = validator;
    }

    private Long obterUsuario() {
        return service.obterIdUsuarioAutenticado();
    }

    public ResponseEntity<PaginadoTarefaDTO> obterTarefasArquivadas(Pageable pageable) {
        logger.info("Buscando tarefas arquivadas para o usuário {}", obterUsuario());

        Page<DadosListagemTarefa> tarefas = repository.findByUsuarioIdAndAtivoFalse(obterUsuario(), pageable)
                .map(DadosListagemTarefa::new);

        PaginadoTarefaDTO resultado = PaginadoTarefaDTO.from(tarefas);

        logger.info("Retornadas {} tarefas arquivas para o usuário {}", tarefas.getTotalElements(), obterUsuario());
        return ResponseEntity.ok(resultado);
    }

    @Transactional
    public ResponseEntity<DadosListagemTarefa> restaurarTarefa(Long taskId) {
        logger.info("Restaurando tarefa arquivada ID {} para o usuário {}", taskId, obterUsuario());


        Tarefa tarefa = validator.validarTarefaDoUsuario(taskId, obterUsuario());


        if (tarefa.isAtivo()) {
            logger.info("Tarefa ID {} já está ativa", taskId);
            return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
        }


        tarefa.setAtivo(true);
        repository.save(tarefa);

        logger.info("Tarefa ID {} restaurada com sucesso", taskId);
        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }

    @Transactional
    public ResponseEntity<List<DadosListagemTarefa>> restaurarMultiplasTarefas(ArquivarMultiplasTarefasDTO dados){
        List<Long> tarefasIds = dados.tarefasId();

        if(tarefasIds.isEmpty()) {
            throw new ListTaskEmptyException("A lista de tarefas não pode estar vazia");
        }

        logger.info("Restaurando {} tarefas arquivadas para o usuário {}", tarefasIds.size(), obterUsuario());
        List<Tarefa> tarefas = repository.findAllByIdsAndUsuarioId(tarefasIds, obterUsuario());

        if(tarefas.isEmpty()) {
            logger.warn("Nenhuma tarefa encontrada para restaurar");
            return ResponseEntity.noContent().build();
        }

        tarefas.forEach(tarefa -> tarefa.setAtivo(true));
        repository.saveAll(tarefas);

        List<DadosListagemTarefa> tarefasRestauradas = tarefas.stream()
                .map(DadosListagemTarefa::new)
                .toList();

        logger.info("{} tarefas restauradas com sucesso", tarefasRestauradas.size());
        return ResponseEntity.ok(tarefasRestauradas);
    }
}
