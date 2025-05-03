package com.example.taskmanager.controller;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<DadosListagemTarefa> cadastrar(
            @RequestBody @Valid DadosCriarTarefa dados,
            @RequestParam(required = false) Long categoriaId,
            UriComponentsBuilder uriBuilder) {
        return tarefaService.criarTarefa(dados, categoriaId, uriBuilder);
    }

    @GetMapping("/paginado")
    public ResponseEntity<PaginadoTarefaDTO> listarTarefasAtivasDoUsuario(
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return tarefaService.listarTarefasAtivas(pageable);
    }

    @PutMapping("/{tarefaId}")
    public ResponseEntity<DadosAtualizacaoTarefaResposta> atualizar(@PathVariable Long tarefaId, @RequestBody @Valid DadosAtualizaTarefa dados){
        return tarefaService.atualizarTarefa(tarefaId, dados);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<PaginadoTarefaDTO> filtrarTarefa(@RequestParam(required = false) Long statusId,
                                                           @RequestParam(required = false) Long prioridadeId,
                                                           @RequestParam (required = false) Long categoriaId,
                                                           @PageableDefault(size=10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {
        FiltrosStatusPrioridadeCategoriaDTO filtro = new FiltrosStatusPrioridadeCategoriaDTO(
                statusId, prioridadeId, categoriaId);

        return tarefaService.filtrarTarefasPorStatusPrioridadeCategoria(filtro, pageable);
    }

    @GetMapping("/filtrar/palavra")
    public ResponseEntity<PaginadoTarefaDTO> filtrarTarefaPalavra(
            @RequestParam(required = false) String palavraChave,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        return tarefaService.buscarTarefasPorPalavraChave(palavraChave, pageable);
    }

    @DeleteMapping("/arquivar/{tarefaId}")
    public ResponseEntity<Void> arquivarTarefa(@PathVariable Long tarefaId) {
        tarefaService.arquivarTarefa(tarefaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/arquivar")
    public ResponseEntity<Void> arquivarMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados) {
        tarefaService.arquivarMultiplasTarefas(dados.tarefasId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{tarefaId}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long tarefaId) {
        return tarefaService.deletarTarefa(tarefaId);
    }

    @DeleteMapping("/deletar/multiplas")
    public ResponseEntity<Void> deletarMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados) {
        tarefaService.excluirMultiplasTarefas(dados.tarefasId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/concluir")
    public ResponseEntity<List<DadosListagemTarefa>> concluirMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados){
            return tarefaService.concluirMultiplasTarefas(dados.tarefasId());
    }

    @PatchMapping("/concluir/{id}")
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(@PathVariable Long id) {
        return tarefaService.concluirTarefa(id);
    }

    @PatchMapping("/reabrir/{id}")
    public ResponseEntity<DadosListagemTarefa> reabrirTarefa(@PathVariable Long id) {
        return tarefaService.reabrirTarefa(id);
    }

    @PatchMapping("/reabrir")
    public ResponseEntity<List<DadosListagemTarefa>> reabrirMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados){
        return tarefaService.reabrirMultiplasTarefas(dados.tarefasId());
    }

}
