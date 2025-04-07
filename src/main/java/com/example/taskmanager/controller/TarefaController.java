package com.example.taskmanager.controller;

import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
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
            @RequestParam Long usuarioId,
            @RequestParam(required = false) Long categoriaId,
            UriComponentsBuilder uriBuilder) {
        return tarefaService.criarTarefa(dados, usuarioId, categoriaId, uriBuilder);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<DadosListagemTarefa>> listarTarefasAtivasDoUsuario(
            @RequestParam Long usuarioId,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<DadosListagemTarefa> tarefas = tarefaService.listarTarefasAtivas(usuarioId, pageable);
        return ResponseEntity.ok(tarefas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DadosListagemTarefa> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizaTarefa dados){
        return tarefaService.atualizarTarefa(id, dados);
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(@PathVariable Long id) {
        return tarefaService.concluirTarefa(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        tarefaService.excluirTarefa(id);
        return ResponseEntity.noContent().build();
    }


}
