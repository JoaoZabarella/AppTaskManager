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
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class CategoriaService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository repository;
    private final CategoriaServiceValidator validator;
    private final UsuarioAutenticadoService authenticate;

    public CategoriaService(CategoriaRepository repository, CategoriaServiceValidator validator, UsuarioAutenticadoService authenticate) {
        this.repository = repository;
        this.validator = validator;
        this.authenticate = authenticate;
    }

    public Long obterUsuario(){
        logger.info("Obtendo ID do usuário autenticado");
        Long usuarioId = authenticate.obterIdUsuarioAutenticado();
        logger.info("ID do usuário autenticado: {}", usuarioId);
        return usuarioId;
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> criarCategoria(DadosCriarCategoria dados, UriComponentsBuilder uriBuilder) {
        long startTime = System.currentTimeMillis();
        logger.info("Usuário com ID {} está criando categoria com nome: {}", obterUsuario(), dados.nome());

        Usuario usuarioAutenticado = authenticate.obterUsuarioAutenticado();

        logger.info("Validando se o nome da categoria já existe para o usuário");
        validator.validarNomeCategoria(dados.nome(), usuarioAutenticado.getId());

        var novaCategoria = Categoria.criarParaUsuario(dados.nome(), usuarioAutenticado);
        novaCategoria.setUsuario(usuarioAutenticado);
        repository.save(novaCategoria);

        URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(novaCategoria.getId()).toUri();

        long endTime = System.currentTimeMillis();
        logger.info("Categoria com ID {} criada com sucesso em {} ms", novaCategoria.getId(), (endTime - startTime));

        return ResponseEntity.created(uri).body(new DadosListagemCategoria(novaCategoria));
    }

    public ResponseEntity<PaginaCategoriaDTO> listarCategoriasAtivasDoUsuario(Pageable pageable) {
        logger.info("Buscando categorias ativas para o usuário ID {}", obterUsuario());

        Page<DadosListagemCategoria> categorias = repository.findByUsuarioIdAndAtivoTrue(obterUsuario(), pageable)
                .map(DadosListagemCategoria::new);

        PaginaCategoriaDTO resultado = PaginaCategoriaDTO.from(categorias);

        logger.info("Retornadas {} categorias ativas para o usuário ID {}", categorias.getTotalElements(), obterUsuario());
        return ResponseEntity.ok(resultado);
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> atualizarCategoria(Long categoriaId, DadosAtualizaCategoria dados) {
        logger.info("Atualizando categoria com ID {} para o usuário ID {}", categoriaId, obterUsuario());

        var categoria = validator.validarCategoria(categoriaId, obterUsuario());
        logger.info("Categoria encontrada: {}", categoria.getNome());

        logger.info("Atualizando campos da categoria");
        validator.atualizarCampos(categoria, dados);

        logger.info("Categoria com ID {} atualizada com sucesso", categoriaId);
        return ResponseEntity.ok(new DadosListagemCategoria(categoria));
    }

    @Transactional
    public ResponseEntity<Void> excluirCategoria(Long categoriaId) {
        logger.info("Excluindo (inativando) categoria com ID {} para o usuário ID {}", categoriaId, obterUsuario());

        Categoria categoria = validator.validarCategoria(categoriaId, obterUsuario());
        categoria.inativar();

        logger.info("Categoria com ID {} inativada com sucesso", categoriaId);
        return ResponseEntity.noContent().build();
    }
}