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

    @Transactional
    public ResponseEntity<DadosListagemCategoria> criarCategoria(DadosCriarCategoria dados, UriComponentsBuilder uriBuilder) {
        Usuario usuario = authenticate.obterUsuarioAutenticado();
        Long usuarioId = usuario.getId();

        validator.validarNomeCategoria(dados.nome(), usuarioId);

        var categoria = new Categoria(dados.nome());
        categoria.setUsuario(usuario);
        repository.save(categoria);

        URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemCategoria(categoria));
    }

    public ResponseEntity<PaginaCategoriaDTO> listarCategoriasAtivasDoUsuario (Pageable pageable) {
        Long usuarioId = authenticate.obterIdUsuarioAutenticado();

        Page<DadosListagemCategoria> categoria = repository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)
                .map(DadosListagemCategoria::new);

        PaginaCategoriaDTO resultado = PaginaCategoriaDTO.from(categoria);
        return ResponseEntity.ok(resultado);
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> atualizarCategoria(Long categoriaId, DadosAtualizaCategoria dados) {
        Long usuarioId = authenticate.obterIdUsuarioAutenticado();
        var categoria = validator.validarCategoria(categoriaId, usuarioId);

        validator.atualizarCampos(categoria, dados);
        repository.save(categoria);

        return ResponseEntity.ok(new DadosListagemCategoria(categoria));
    }


    @Transactional
    public ResponseEntity<Void> excluirCategoria(Long categoriaId) {
        Long usuarioId = authenticate.obterIdUsuarioAutenticado();
        Categoria categoria = validator.validarCategoria(categoriaId, usuarioId);
        categoria.inativar();
        return ResponseEntity.noContent().build();

    }
}
