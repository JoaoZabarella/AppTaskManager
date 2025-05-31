package com.example.taskmanager.controller;

import com.example.taskmanager.dto.tarefa.ArquivarMultiplasTarefasDTO;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.dto.tarefa.PaginadoTarefaDTO;
import com.example.taskmanager.service.TarefasArquivasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
@Tag(name = "Tarefas Arquivadas", description = "Endpoints para gerenciamento de tarefas arquivadas")
@SecurityRequirement(name = "bearer-jwt")
public class TarefasArquivadasController {

    private final TarefasArquivasService service;

    public TarefasArquivadasController(TarefasArquivasService service) {
        this.service = service;
    }

    @GetMapping("/arquivadas")
    @Operation(summary = "Listar tarefas arquivadas", description = "Lista todas as tarefas arquivadas do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas listadas com sucesso")
    })
    public ResponseEntity<PaginadoTarefaDTO> listarTarefasArquivadas(
            @PageableDefault(size = 99, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.obterTarefasArquivadas(pageable);
    }
    @PatchMapping("/restaurar/{id}")
    @Operation(summary = "Restaurar tarefa", description = "Restaura uma tarefa arquivada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa restaurada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<DadosListagemTarefa> restaurarTarefa(@PathVariable Long id){
        return service.restaurarTarefa(id);
    }

    @PatchMapping("/restaurar")
    @Operation(summary = "Restaurar múltiplas tarefas", description = "Restaura várias tarefas arquivadas simultaneamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas restauradas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tarefas vazia")
    })
    public ResponseEntity<List<DadosListagemTarefa>> restaurarMultiplasTarefas(@RequestBody ArquivarMultiplasTarefasDTO dados) {
        return service.restaurarMultiplasTarefas(dados);
    }
}


