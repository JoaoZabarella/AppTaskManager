package com.example.taskmanager.validator;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.model.Categoria;

import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceValidator {

    private final EntidadeValidator validator;

    public CategoriaServiceValidator( EntidadeValidator validator) {
        this.validator = validator;
    }

    public Categoria validarCategoria(Long categoriaId, Long usuarioId) {
        return validator.validarCategoriaDoUsuario(categoriaId, usuarioId);
    }

    public void validarNomeCategoria(String nome, Long usuarioId) {
        validator.validarNomeCategoriaDuplicado(nome, usuarioId);
    }

    public void atualizarCampos(Categoria categoria, DadosAtualizaCategoria dados) {
        if (dados.nome() != null && !dados.nome().equals(categoria.getNome())) {
            validarNomeCategoria(dados.nome(), categoria.getUsuario().getId());
            categoria.setNome(dados.nome());
        }
    }
}
