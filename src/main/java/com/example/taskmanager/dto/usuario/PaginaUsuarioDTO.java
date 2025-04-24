package com.example.taskmanager.dto.usuario;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaUsuarioDTO (
        List<DadosListagemUsuarioDTO> usuarios,
        int paginaAtual,
        int totalItens,
        int totalPaginas
){
    public static PaginaUsuarioDTO from(Page<DadosListagemUsuarioDTO> page){
        return new PaginaUsuarioDTO(
                page.getContent(),
                page.getNumber(),
                (int) page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
