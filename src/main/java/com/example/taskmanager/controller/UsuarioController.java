package com.example.taskmanager.controller;

import com.example.taskmanager.dto.usuario.AlterarSenhaDTO;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já existente")
    })
    public ResponseEntity<DadosListagemUsuarioDTO> cadastrar(@Valid @RequestBody DadosCadastroUsuario dadosCadastro, UriComponentsBuilder uriBuilder){
        return usuarioService.cadastraUsuario(dadosCadastro, uriBuilder);
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário logado", description = "Retorna os dados do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<DadosListagemUsuarioDTO> listar (){
        return usuarioService.buscarUsuarioLogado();
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do usuário", description = "Atualiza os dados do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<DadosListagemUsuarioDTO> atualizar(@RequestBody @Valid DadosAtualizaUsuario dados){
        return usuarioService.atualizarDadosUsuario(dados);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Inativar conta", description = "Inativa a conta do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta inativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Void> inativar(){
        return usuarioService.inativar();
    }

    @PutMapping("/me/alterar-senha")
    @Transactional
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO dto){
        return usuarioService.alterarSenha(dto);
    }
}