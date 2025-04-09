package com.example.taskmanager.controller;

import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;




@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<DadosListagemUsuarioDTO> cadastrar(@Valid @RequestBody DadosCadastroUsuario dadosCadastro, UriComponentsBuilder uriBuilder){
        return usuarioService.cadastraUsuario(dadosCadastro, uriBuilder);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuarioDTO>> listar (@PageableDefault(page = 0, size = 10, sort ={"id"}) Pageable pageable){
        var page = usuarioService.listarDadosUsuarioAtivos(pageable);
        return ResponseEntity.ok(page);
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizaUsuario dados){
        var dadosComId = new DadosAtualizaUsuario(id, dados.nome(), dados.email());

        var usuarioAtualizado = usuarioService.atualizarDadosUsuario(dadosComId);
        return ResponseEntity.ok(usuarioAtualizado);

    }

    //Responsável pela exclusão lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar (@PathVariable Long id){
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/buscar")
    public ResponseEntity<DadosListagemUsuarioDTO> listarUsuarios(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String email
        ) {
        var usuario = usuarioService.buscarUsuario(id, nome, email);
        return ResponseEntity.ok(usuario);

    }
}
