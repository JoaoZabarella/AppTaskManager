package com.example.taskmanager.controller;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.dto.categoria.DadosCriarCategoria;
import com.example.taskmanager.dto.categoria.DadosListagemCategoria;
import com.example.taskmanager.dto.categoria.PaginaCategoriaDTO;
import com.example.taskmanager.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<DadosListagemCategoria> criarCategoria(
            @RequestBody @Valid DadosCriarCategoria dados,
            UriComponentsBuilder uriBuilder) {

        return categoriaService.criarCategoria(dados, uriBuilder);
    }

    @GetMapping
    public ResponseEntity<PaginaCategoriaDTO> listarCategorias(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return categoriaService.listarCategoriasAtivasDoUsuario(pageable);
    }


    @PutMapping("/{categoriaId}")
    public ResponseEntity<DadosListagemCategoria> listarCategorias(
            @PathVariable Long categoriaId,
            @RequestBody @Valid DadosAtualizaCategoria dados) {
        return categoriaService.atualizarCategoria(categoriaId, dados);
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Void> excluirCategoria(
            @PathVariable Long categoriaId) {
        categoriaService.excluirCategoria(categoriaId);
        return ResponseEntity.noContent().build();
    }
}
