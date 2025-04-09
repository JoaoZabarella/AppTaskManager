package com.example.taskmanager.dto.categoria;

import com.example.taskmanager.model.Categoria;

public record DadosListagemCategoria(
        Long id,
        String nome

) {
    public DadosListagemCategoria (Categoria categoria){
        this(
                categoria.getId(),
                categoria.getNome()
        );
    }
}
