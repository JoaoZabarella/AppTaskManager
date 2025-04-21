package com.example.taskmanager.service;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.dto.categoria.DadosCriarCategoria;
import com.example.taskmanager.dto.categoria.DadosListagemCategoria;
import com.example.taskmanager.dto.categoria.PaginaCategoriaDTO;
import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.CategoriaRepository;
import com.example.taskmanager.validator.CategoriaServiceValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaServiceValidator validator;
    private final UsuarioAutenticadoService authenticate;

    public CategoriaService(CategoriaRepository repository, CategoriaServiceValidator validator, UsuarioAutenticadoService authenticate) {
        this.repository = repository;
        this.validator = validator;
        this.authenticate = authenticate;
    }

    public Long obterUsuario(){
        return authenticate.obterIdUsuarioAutenticado();
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> criarCategoria(DadosCriarCategoria dados, UriComponentsBuilder uriBuilder) {
        Usuario usuarioAutenticado =  authenticate.obterUsuarioAutenticado();

        validator.validarNomeCategoria(dados.nome(), usuarioAutenticado.getId());

       var novaCategoria = Categoria.criarParaUsuario(dados.nome(), usuarioAutenticado);
        novaCategoria.setUsuario(usuarioAutenticado);
        repository.save(novaCategoria);

        URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(novaCategoria.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemCategoria(novaCategoria));
    }

    public ResponseEntity<PaginaCategoriaDTO> listarCategoriasAtivasDoUsuario (Pageable pageable) {

        Page<DadosListagemCategoria> categorias = repository.findByUsuarioIdAndAtivoTrue(obterUsuario(), pageable)
                .map(DadosListagemCategoria::new);

        PaginaCategoriaDTO resultado = PaginaCategoriaDTO.from(categorias);
        return ResponseEntity.ok(resultado);
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> atualizarCategoria(Long categoriaId, DadosAtualizaCategoria dados) {

        var categoria = validator.validarCategoria(categoriaId, obterUsuario());

        validator.atualizarCampos(categoria, dados);

        return ResponseEntity.ok(new DadosListagemCategoria(categoria));
    }

    @Transactional
    public ResponseEntity<Void> excluirCategoria(Long categoriaId) {
        Categoria categoria = validator.validarCategoria(categoriaId, obterUsuario());
        categoria.inativar();
        return ResponseEntity.noContent().build();

    }
}
