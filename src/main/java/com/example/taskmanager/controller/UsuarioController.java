package com.example.taskmanager.controller;

import com.example.taskmanager.dto.usuario.AlterarSenhaDTO;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<DadosListagemUsuarioDTO> listar (){
        return usuarioService.buscarUsuarioLogado();
    }

    @PutMapping("/me")
    public ResponseEntity<DadosListagemUsuarioDTO> atualizar( @RequestBody @Valid DadosAtualizaUsuario dados){
        return usuarioService.atualizarDadosUsuario(dados);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> inativar(){
        return usuarioService.inativar();
    }

    @PutMapping("/me/alterar-senha")
    @Transactional
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO dto){
        return usuarioService.alterarSenha(dto);
    }
}
