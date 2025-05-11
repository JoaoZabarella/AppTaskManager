package com.example.taskmanager.controller;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.dto.categoria.DadosCriarCategoria;
import com.example.taskmanager.dto.categoria.DadosListagemCategoria;
import com.example.taskmanager.dto.categoria.PaginaCategoriaDTO;
import com.example.taskmanager.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
@SecurityRequirement(name = "bearer-jwt")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Nome de categoria já existe")
    })
    public ResponseEntity<DadosListagemCategoria> criarCategoria(
            @RequestBody @Valid DadosCriarCategoria dados,
            UriComponentsBuilder uriBuilder) {

        return categoriaService.criarCategoria(dados, uriBuilder);
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Lista todas as categorias ativas do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    })
    public ResponseEntity<PaginaCategoriaDTO> listarCategorias(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return categoriaService.listarCategoriasAtivasDoUsuario(pageable);
    }


    @PutMapping("/{categoriaId}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza uma categoria existente do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Nome de categoria já existe")
    })
    public ResponseEntity<DadosListagemCategoria> atualizarCategoria(
            @PathVariable Long categoriaId,
            @RequestBody @Valid DadosAtualizaCategoria dados) {
        return categoriaService.atualizarCategoria(categoriaId, dados);
    }

    @DeleteMapping("/{categoriaId}")
    @Operation(summary = "Excluir categoria", description = "Inativa uma categoria existente do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<Void> excluirCategoria(
            @PathVariable Long categoriaId) {
        categoriaService.excluirCategoria(categoriaId);
        return ResponseEntity.noContent().build();
    }
}