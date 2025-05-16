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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TarefaService.class);

    private final TarefaRepository tarefaRepository;
    private final TarefaMapper mapper;
    private final TarefaValidatorService validatorService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final EntidadeValidator validator;


    public TarefaService(TarefaRepository tarefaRepository, TarefaMapper mapper, TarefaValidatorService validatorService, UsuarioAutenticadoService usuarioAutenticadoService, TarefaValidatorService tarefaValidator, EntidadeValidator validator) {
        this.tarefaRepository = tarefaRepository;
        this.mapper = mapper;
        this.validatorService = validatorService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;

        this.validator = validator;
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

        validatorService.validarTarefaPodeSerEditada(tarefa);

        DadosAtualizacaoTarefaResposta resposta = validatorService.atualizacaoTarefaComDados(tarefa, dados);
        tarefaRepository.save(tarefa);

        return ResponseEntity.ok(resposta);
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

    public ResponseEntity<PaginadoTarefaDTO> buscarTarefasPorPalavraChave(String palavraChave, Pageable pageable) {
        if(palavraChave == null || palavraChave.trim().isEmpty()){
            logger.info("Busca com palavra-chave vazia, retornando todas as tarefas ativas");
            return listarTarefasAtivas(pageable);
        }

        logger.info("Buscando tarefas com palavra chave '{}' para usuario de ID {}", palavraChave, obterUsuario());

        Page<Tarefa> tarefasEncontradas = tarefaRepository.buscarPorPalavraChave(
                obterUsuario(), palavraChave, pageable);

        Page<DadosListagemTarefa> resultado = tarefasEncontradas.map(DadosListagemTarefa::new);

        logger.info("Encontradas {} tarefas contendo a palavra-chave '{}'", resultado.getTotalElements(), palavraChave);
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
                    .toList();

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
                    .toList();

            List<Long> tarefasNaoEncontradas = tarefasIds.stream()
                    .filter(id -> !tarefasEncontradas.contains(id))
                    .collect(Collectors.toList());

            logger.warn("Algumas tarefas não foram encontradas para excluir ou não pertencem ao usuário: {}", tarefasNaoEncontradas);
        }

        tarefaRepository.deleteAll(tarefas);
        logger.info("{} tarefas excluidas com sucesso", tarefas.size());
    }

    //CONCLUIR
    @Transactional
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(Long tarefaId) {
        logger.info("Concluindo tarefa com ID {} para o usuário {}", tarefaId, obterUsuario());

        var tarefa = validatorService.validaEObterTarefa(tarefaId, obterUsuario());


        if (validatorService.isTarefaConcluida(tarefa)) {
            logger.info("Tarefa {} já está concluída", tarefaId);
            return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
        }

        validator.validarTarefa(tarefaId);

        Status statusConcluido = validatorService.validadorObterStatus(3L);

        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        tarefaRepository.save(tarefa);

        logger.info("Tarefa {} concluída com sucesso", tarefaId);
        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }


    @Transactional
    public ResponseEntity<List<DadosListagemTarefa>> concluirMultiplasTarefas(List<Long> tarefasIds){
        logger.info("Concluindo {} tarefas em lote para o usuario {}", tarefasIds.size(), obterUsuario());

        validatorService.verificarTarefas(tarefasIds);

        List<Tarefa> tarefas = tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, obterUsuario());

        if(tarefas.isEmpty()){
            logger.warn("Nenhuma tarefa encontrada para concluir");
            return ResponseEntity.noContent().build();
        }

        Status statusConcluido = validatorService.validadorObterStatus(3L);
        List<Tarefa> tarefasParaConcluir = new ArrayList<>();

        for(Tarefa tarefa : tarefas){
            if(!tarefa.isConcluida()){
                tarefa.setStatus(statusConcluido);
                tarefa.concluir();
                tarefasParaConcluir.add(tarefa);
            }
        }

        if(!tarefasParaConcluir.isEmpty()){
            tarefaRepository.saveAll(tarefasParaConcluir);
        }

        List<DadosListagemTarefa> tarefasConcluidas = tarefas.stream()
                .map(DadosListagemTarefa::new)
                .toList();

        logger.info("{} tarefas concluidas com sucesso", tarefasConcluidas.size());
        return ResponseEntity.ok(tarefasConcluidas);
    }

    //REABRIR
    @Transactional
    public ResponseEntity<DadosListagemTarefa> reabrirTarefa(Long tarefaId){
        logger.info("Reabrindo tarefa com ID {} para o usuario {}", tarefaId, obterUsuario());

        var tarefa = validatorService.validaEObterTarefa(tarefaId, obterUsuario());

        if(!validatorService.isTarefaConcluida(tarefa)){
            logger.info("Tarefa {}, ja está aberta", tarefaId);
            return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
        }

        validator.validarTarefa(tarefaId);

        Status statusEmAndamento = validatorService.validadorObterStatus(2L);
        mapper.prepararTarefaParaReabertura(tarefa, statusEmAndamento);
        tarefaRepository.save(tarefa);

        logger.info("Tarefa {} reaberta com sucesso", tarefaId);
        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }

    @Transactional
    public ResponseEntity<List<DadosListagemTarefa>> reabrirMultiplasTarefas(List<Long> tarefasIds){
        logger.info("Reabrindo {} tarefas em lote para o usuario {}", tarefasIds.size(), obterUsuario());

        validatorService.verificarTarefas(tarefasIds);

        List<Tarefa> tarefas = tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, obterUsuario());

        if(tarefas.isEmpty()){
            logger.warn("Nenhuma tarefa encontrada para reabrir");
            return ResponseEntity.noContent().build();
        }

        Status statusEmAdamento = validatorService.validadorObterStatus(2L);
        List<Tarefa> tarefasReabertas = mapper.filtrarTarefasParaReabrir(tarefas, statusEmAdamento, validatorService);

        if(!tarefasReabertas.isEmpty()){
            tarefaRepository.saveAll(tarefasReabertas);
        }
        List<DadosListagemTarefa> resultado = mapper.converterParaDTOs(tarefas);

        logger.info("{} tarefas reabertas com sucesso", tarefasReabertas.size());
        return ResponseEntity.ok(resultado);
    }

    @Transactional()
    public TarefaEstatisticaDTO obterTarefaEstatistica(){
        Long usuario = obterUsuario();
        return tarefaRepository.obterEstatisticasPorUsuario(usuario);
    }


    public ResponseEntity<DadosListagemTarefa> buscarTarefaPorId(Long id) {
        Tarefa tarefa = validatorService.validaEObterTarefa(id, obterUsuario());
        return ResponseEntity.ok(new DadosListagemTarefa(tarefa));
    }

}

