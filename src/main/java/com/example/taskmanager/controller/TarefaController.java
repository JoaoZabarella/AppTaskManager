package com.example.taskmanager.controller;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
@SecurityRequirement(name = "bearer-jwt")
public class TarefaController {

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    private final TarefaService tarefaService;

    @PostMapping
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoria, status ou prioridade não encontrados")
    })
    public ResponseEntity<DadosListagemTarefa> cadastrar(
            @RequestBody @Valid DadosCriarTarefa dados,
            @Parameter(description = "ID da categoria") @RequestParam(required = false) Long categoriaId,
            UriComponentsBuilder uriBuilder) {
        return tarefaService.criarTarefa(dados, categoriaId, uriBuilder);
    }



    @GetMapping("/paginado")
    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas ativas do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas listadas com sucesso")
    })
    public ResponseEntity<PaginadoTarefaDTO> listarTarefasAtivasDoUsuario(
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return tarefaService.listarTarefasAtivas(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter tarefa por ID", description = "Retorna os detalhes de uma tarefa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<DadosListagemTarefa> obterTarefaPorId(@PathVariable Long id) {
        return tarefaService.buscarTarefaPorId(id);
    }

    @PutMapping("/{tarefaId}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou tarefa concluída"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<DadosAtualizacaoTarefaResposta> atualizar(
            @PathVariable Long tarefaId,
            @RequestBody @Valid DadosAtualizaTarefa dados){
        return tarefaService.atualizarTarefa(tarefaId, dados);
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Filtrar tarefas", description = "Filtra tarefas por status, prioridade e categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro aplicado com sucesso")
    })
    public ResponseEntity<PaginadoTarefaDTO> filtrarTarefa(
            @Parameter(description = "ID do status") @RequestParam(required = false) Long statusId,
            @Parameter(description = "ID da prioridade") @RequestParam(required = false) Long prioridadeId,
            @Parameter(description = "ID da categoria") @RequestParam(required = false) Long categoriaId,
            @PageableDefault(size=10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {
        FiltrosStatusPrioridadeCategoriaDTO filtro = new FiltrosStatusPrioridadeCategoriaDTO(
                statusId, prioridadeId, categoriaId);

        return tarefaService.filtrarTarefasPorStatusPrioridadeCategoria(filtro, pageable);
    }

    @GetMapping("/filtrar/palavra")
    @Operation(summary = "Buscar tarefas por palavra-chave", description = "Busca tarefas pelo título ou descrição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<PaginadoTarefaDTO> filtrarTarefaPalavra(
            @Parameter(description = "Palavra-chave para busca") @RequestParam(required = false) String palavraChave,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        return tarefaService.buscarTarefasPorPalavraChave(palavraChave, pageable);
    }

    @DeleteMapping("/arquivar/{tarefaId}")
    @Operation(summary = "Arquivar tarefa", description = "Arquiva uma tarefa existente do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa arquivada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<Void> arquivarTarefa(@PathVariable Long tarefaId) {
        tarefaService.arquivarTarefa(tarefaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/arquivar")
    @Operation(summary = "Arquivar múltiplas tarefas", description = "Arquiva várias tarefas simultaneamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefas arquivadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tarefas vazia")
    })
    public ResponseEntity<Void> arquivarMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados) {
        tarefaService.arquivarMultiplasTarefas(dados.tarefasId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{tarefaId}")
    @Operation(summary = "Deletar tarefa", description = "Remove permanentemente uma tarefa do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long tarefaId) {
        return tarefaService.deletarTarefa(tarefaId);
    }

    @DeleteMapping("/deletar/multiplas")
    @Operation(summary = "Deletar múltiplas tarefas", description = "Remove permanentemente várias tarefas simultaneamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefas deletadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tarefas vazia")
    })
    public ResponseEntity<Void> deletarMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados) {
        tarefaService.excluirMultiplasTarefas(dados.tarefasId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/concluir")
    @Operation(summary = "Concluir múltiplas tarefas", description = "Marca várias tarefas como concluídas simultaneamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas concluídas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tarefas vazia")
    })
    public ResponseEntity<List<DadosListagemTarefa>> concluirMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados){
        return tarefaService.concluirMultiplasTarefas(dados.tarefasId());
    }

    @PatchMapping("/concluir/{id}")
    @Operation(summary = "Concluir tarefa", description = "Marca uma tarefa como concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa concluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<DadosListagemTarefa> concluirTarefa(@PathVariable Long id) {
        return tarefaService.concluirTarefa(id);
    }

    @PatchMapping("/reabrir/{id}")
    @Operation(summary = "Reabrir tarefa", description = "Reabre uma tarefa concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa reaberta com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<DadosListagemTarefa> reabrirTarefa(@PathVariable Long id) {
        return tarefaService.reabrirTarefa(id);
    }

    @PatchMapping("/reabrir")
    @Operation(summary = "Reabrir múltiplas tarefas", description = "Reabre várias tarefas concluídas simultaneamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas reabertas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tarefas vazia")
    })
    public ResponseEntity<List<DadosListagemTarefa>> reabrirMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados){
        return tarefaService.reabrirMultiplasTarefas(dados.tarefasId());
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas de tarefas", description = "Retorna estatísticas das tarefas do usuário")
    public ResponseEntity<TarefaEstatisticaDTO> obterEstatisticas() {
        return ResponseEntity.ok(tarefaService.obterTarefaEstatistica());
    }
}