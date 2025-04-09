package com.example.taskmanager.service;

import com.example.taskmanager.dto.categoria.DadosCriarCategoria;
import com.example.taskmanager.dto.categoria.DadosListagemCategoria;
import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.repository.CategoriaRepository;
import com.example.taskmanager.validator.CategoriaServiceValidator;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaServiceValidator validator;

    public CategoriaService(CategoriaRepository repository, CategoriaServiceValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    public ResponseEntity<DadosListagemCategoria> criarCategoria(DadosCriarCategoria dados, Long usuarioId, UriComponentsBuilder uriBuilder) {

        var usuario = validator.validarObterUsuario(usuarioId);

        validator.validarNomeCategoria(dados.nome(), usuarioId);

        var categoria = new Categoria(dados.nome());
        categoria.setUsuario(usuario);
        repository.save(categoria);

        URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemCategoria(categoria));

    }

}
