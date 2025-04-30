package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.mapper.TarefaMapper;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.TarefaRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import com.example.taskmanager.validator.TarefaValidatorService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    private Long obterUsuario() {
        return usuarioAutenticadoService.obterIdUsuarioAutenticado();
    }

    //Métodos de POST
    @Transactional
    public ResponseEntity<DadosListagemTarefa> criarTarefa(DadosCriarTarefa dados, Long categoriaId, UriComponentsBuilder uriBuilder) {
        long startTime = System.currentTimeMillis();
        logger.info("Usuário com ID {} está criando uma tarefa", obterUsuario());

        Tarefa tarefa = mapper.prepararTarefa(dados);
        tarefaRepository.save(tarefa);
        URI uri = mapper.construirUri(tarefa, uriBuilder);

        long endTime = System.currentTimeMillis();
        logger.info("Tarefa com ID {} criada com sucesso em {} ms", tarefa.getId(), (endTime - startTime));

        return ResponseEntity.created(uri).body(new DadosListagemTarefa(tarefa));
    }



    //Métodos de PUT
    @Transactional
    public ResponseEntity<DadosAtualizacaoTarefaResposta> atualizarTarefa(Long tarefaId, DadosAtualizaTarefa dados) {
        var tarefa = validatorService.validadosObterTarefaDoUsuario(tarefaId, obterUsuario());
        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(validatorService.atualizacaoTarefaComDados(tarefa, dados));
    }
    @Transactional
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(Long tarefaId) {
        var tarefa = validatorService.validaEObterTarefa(tarefaId, obterUsuario());
        Status statusConcluido = validatorService.validadorObterStatus(3L);

        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        tarefaRepository.save(tarefa);


        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }


    //Métodos de GET
    public ResponseEntity<PaginadoTarefaDTO> listarTarefasAtivas(Pageable pageable) {
        logger.info("Buscando tarefas para serem listadas");

        Page<DadosListagemTarefa> tarefas = tarefaRepository.findByUsuarioIdAndAtivoTrue(obterUsuario(), pageable)
                .map(DadosListagemTarefa::new);
        PaginadoTarefaDTO resultado = PaginadoTarefaDTO.from(tarefas);

        logger.info("Tarefas do usuario com ID {} retornadas com sucesso!", obterUsuario());
        return ResponseEntity.ok(resultado);

    }

    public ResponseEntity<PaginadoTarefaDTO> filtrarTarefasPorStatusPrioridadeCategoria(
            FiltrosStatusPrioridadeCategoriaDTO filtro, Pageable pageable){
        long startTime = System.currentTimeMillis();
        logger.info("Filtrando tarefas por status, prioridade e categoria para usuário ID: {}", obterUsuario());

        Page<Tarefa> tarefasFiltradas;
        Long usuarioId = obterUsuario();

        if(mapper.filtroVazio(filtro)){
            tarefasFiltradas = tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
        } else{
            tarefasFiltradas = tarefaRepository.buscarComFiltros(
                    usuarioId,
                    filtro.statusId(),
                    filtro.prioridadeId(),
                    filtro.categoriaId(),
                    pageable
            );
        }

        Page<DadosListagemTarefa> resultado = tarefasFiltradas.map(DadosListagemTarefa::new);
        long endTime = System.currentTimeMillis();
        logger.info("Filtragem concluída em {} ms. Encontradas {} tarefas.",
                (endTime - startTime), resultado.getTotalElements());
        return ResponseEntity.ok(PaginadoTarefaDTO.from(resultado));
    }


    //Métodos de Exclusão DELETE
    @Transactional
    public ResponseEntity<Void> deletarTarefa(Long tarefaId){
        Tarefa tarefa = validatorService.validaEObterTarefa(tarefaId, obterUsuario());
        tarefaRepository.delete(tarefa);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> arquivarTarefa(Long tarefaId) {
        Tarefa tarefa = validatorService.validaEObterTarefa(tarefaId, obterUsuario());
        tarefa.desativar();
        tarefaRepository.save(tarefa);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public void arquivarMultiplasTarefas(List<Long> tarefasIds){
        validatorService.verificarTarefas(tarefasIds);

        logger.info("Arquivando {} tarefas em lote para o usuario {}", tarefasIds, obterUsuario());
        Long usuarioId = obterUsuario();

        List<Tarefa> tarefas = tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId);

        if(tarefas.isEmpty()){
            logger.warn("Nenhuma tarefa encontrada para arquivar");
            return;
        }

        if(tarefas.size() != tarefasIds.size()){
            List<Long> tarefasEncotradas = tarefas.stream()
                    .map(Tarefa::getId)
                    .collect(Collectors.toList());

            List<Long> tarefasNaoEncontradas = tarefasIds.stream()
                    .filter(id -> !tarefasEncotradas.contains(id))
                    .collect(Collectors.toList());

            logger.warn("Algumas tarefas não foram encontaradas para arquivar ou não pertencem ao usuário: {}", tarefasEncotradas);
        }

        tarefas.forEach(tarefa -> tarefa.setAtivo(false));
        tarefaRepository.saveAll(tarefas);

        logger.info("{} tarefas arquivadas com sucesso", tarefas.size());

    }

    @Transactional
    public void excluirMultiplasTarefas(List<Long> tarefasIds){
        validatorService.verificarTarefas(tarefasIds);

        logger.info("Excluindo {} tarefas em lote para o usuario {}", tarefasIds.size(), obterUsuario());
        Long usuarioId = obterUsuario();

        List<Tarefa> tarefas = tarefaRepository.findAllByIdsAndUsuarioId(tarefasIds, usuarioId);

        if(tarefas.isEmpty()){
            logger.warn("Nenhuma tarefa encontrada para exclusão");
            return;
        }

        if(tarefas.size() != tarefasIds.size()){
            List<Long> tarefasEncontradas = tarefas.stream()
                    .map(Tarefa::getId)
                    .collect(Collectors.toList());

            List<Long> tarefasNaoEncontradas = tarefasIds.stream()
                    .filter(id -> !tarefasEncontradas.contains(id))
                    .collect(Collectors.toList());

            logger.warn("Algumas tarefas não foram encontradas para excluir ou não pertencem ao usuário: {}", tarefasNaoEncontradas);
        }

        tarefaRepository.deleteAll(tarefas);
        logger.info("{} tarefas excluidas com sucesso", tarefas.size());
    }



}

