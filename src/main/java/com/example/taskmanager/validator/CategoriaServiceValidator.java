package com.example.taskmanager.validator;

import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.CategoriaRepository;
import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceValidator {

    private final EntidadeValidator validator;


    public CategoriaServiceValidator( EntidadeValidator validator) {
        this.validator = validator;
    }


    public Usuario validarObterUsuario(Long usuarioId) {
        return validator.validarUsuario(usuarioId);
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
