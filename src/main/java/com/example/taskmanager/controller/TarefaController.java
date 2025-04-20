package com.example.taskmanager.controller;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PutMapping("/{id}/concluir")
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(@PathVariable Long id) {
        return tarefaService.concluirTarefa(id);
    }

    @DeleteMapping("/{tarefaId}")
    public ResponseEntity<Void> arquivarTarefa(@PathVariable Long tarefaId) {
        tarefaService.excluirTarefa(tarefaId);
        return ResponseEntity.noContent().build();
    }
}
