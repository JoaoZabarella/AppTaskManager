package com.example.taskmanager.service;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.dto.categoria.DadosCriarCategoria;
import com.example.taskmanager.dto.categoria.DadosListagemCategoria;
import com.example.taskmanager.dto.categoria.PaginaCategoriaDTO;
import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.CategoriaRepository;
import com.example.taskmanager.validator.CategoriaServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private CategoriaServiceValidator validator;

    @Mock
    private UsuarioAutenticadoService authenticate;

    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario usuario;
    private Categoria categoria;
    private DadosCriarCategoria dadosCriarCategoria;
    private DadosAtualizaCategoria dadosAtualizaCategoria;
    private DadosListagemCategoria dadosListagemCategoria;
    private UriComponentsBuilder uriBuilder;
    private Long usuarioId = 1L;
    private Long categoriaId = 1L;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");

        categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setNome("Categoria Teste");
        categoria.setAtivo(true);
        categoria.setUsuario(usuario);

        dadosCriarCategoria = new DadosCriarCategoria("Categoria Teste");
        dadosAtualizaCategoria = new DadosAtualizaCategoria("Categoria Atualizada");
        dadosListagemCategoria = new DadosListagemCategoria(categoria);

        uriBuilder = UriComponentsBuilder.fromPath("/");
    }

    @Test
    @DisplayName("Deve criar uma categoria com sucesso")
    void criarCategoria() {
        when(authenticate.obterUsuarioAutenticado()).thenReturn(usuario);
        doNothing().when(validator).validarNomeCategoria(anyString(), anyLong());

        when(repository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria cat = invocation.getArgument(0);
            cat.setId(categoriaId);
            return cat;
        });

        ResponseEntity<DadosListagemCategoria> response = categoriaService.criarCategoria(dadosCriarCategoria, uriBuilder);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(categoriaId, response.getBody().id());
        assertEquals(dadosCriarCategoria.nome(), response.getBody().nome());

        verify(authenticate).obterUsuarioAutenticado();
        verify(validator).validarNomeCategoria(dadosCriarCategoria.nome(), usuarioId);
        verify(repository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve listar todas as categorias ativas do usuário")
    void listarCategoriasAtivasDoUsuario() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        Pageable pageable = PageRequest.of(0, 10);
        List<Categoria> categorias = Collections.singletonList(categoria);
        Page<Categoria> categoriasPage = new PageImpl<>(categorias, pageable, categorias.size());

        when(repository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)).thenReturn(categoriasPage);

        ResponseEntity<PaginaCategoriaDTO> response = categoriaService.listarCategoriasAtivasDoUsuario(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().categorias().size());
        assertEquals(categoriaId, response.getBody().categorias().get(0).id());
        assertEquals(categoria.getNome(), response.getBody().categorias().get(0).nome());
        assertEquals(0, response.getBody().paginaAtual());
        assertEquals(1, response.getBody().totalItens());
        assertEquals(1, response.getBody().totalPaginas());

        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(repository).findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
    }

    @Test
    @DisplayName("Deve atualizar uma categoria com sucesso")
    void atualizarCategoria() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        when(validator.validarCategoria(categoriaId, usuarioId)).thenReturn(categoria);
        doNothing().when(validator).atualizarCampos(eq(categoria), any(DadosAtualizaCategoria.class));

        ResponseEntity<DadosListagemCategoria> response = categoriaService.atualizarCategoria(categoriaId, dadosAtualizaCategoria);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(categoriaId, response.getBody().id());
        assertEquals(categoria.getNome(), response.getBody().nome());


        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(validator).validarCategoria(categoriaId, usuarioId);
        verify(validator).atualizarCampos(categoria, dadosAtualizaCategoria);
    }

    @Test
    @DisplayName("Deve excluir (inativar) uma categoria com sucesso")
    void excluirCategoria() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        when(validator.validarCategoria(categoriaId, usuarioId)).thenReturn(categoria);

        ResponseEntity<Void> response = categoriaService.excluirCategoria(categoriaId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        assertFalse(categoria.getAtivo());


        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(validator).validarCategoria(categoriaId, usuarioId);
    }

    @Test
    @DisplayName("Deve verificar se obterUsuario retorna o id do usuário autenticado")
    void obterUsuario() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        Long result = categoriaService.obterUsuario();

        assertEquals(usuarioId, result);
        verify(authenticate).obterIdUsuarioAutenticado();
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void criarCategoriaComNomeDuplicado() {
        when(authenticate.obterUsuarioAutenticado()).thenReturn(usuario);

        doThrow(new RuntimeException("Categoria com este nome já existe")).when(validator).validarNomeCategoria(anyString(), anyLong());

        assertThrows(RuntimeException.class, () -> categoriaService.criarCategoria(dadosCriarCategoria, uriBuilder));

        verify(authenticate).obterUsuarioAutenticado();
        verify(validator).validarNomeCategoria(dadosCriarCategoria.nome(), usuarioId);
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void atualizarCategoriaInexistente() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        when(validator.validarCategoria(categoriaId, usuarioId)).thenThrow(new RuntimeException("Categoria não encontrada"));

        assertThrows(RuntimeException.class, () -> categoriaService.atualizarCategoria(categoriaId, dadosAtualizaCategoria));

        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(validator).validarCategoria(categoriaId, usuarioId);
        verify(validator, never()).atualizarCampos(any(Categoria.class), any(DadosAtualizaCategoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir categoria inexistente")
    void excluirCategoriaInexistente() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        when(validator.validarCategoria(categoriaId, usuarioId)).thenThrow(new RuntimeException("Categoria não encontrada"));

        assertThrows(RuntimeException.class, () -> categoriaService.excluirCategoria(categoriaId));

        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(validator).validarCategoria(categoriaId, usuarioId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void listarCategoriasVazias() {
        when(authenticate.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> categoriasPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)).thenReturn(categoriasPage);

        ResponseEntity<PaginaCategoriaDTO> response = categoriaService.listarCategoriasAtivasDoUsuario(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().categorias().isEmpty());
        assertEquals(0, response.getBody().paginaAtual());
        assertEquals(0, response.getBody().totalItens());
        assertEquals(0, response.getBody().totalPaginas());

        verify(authenticate, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(repository).findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
    }
}