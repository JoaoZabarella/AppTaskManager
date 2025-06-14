package com.example.taskmanager.controller;

import com.example.taskmanager.config.security.service.TokenService;
import com.example.taskmanager.dto.autenticacao.DadosAutenticacao;
import com.example.taskmanager.dto.autenticacao.DadosTokenJWT;
import com.example.taskmanager.validator.EntidadeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EntidadeValidator validator;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, EntidadeValidator validator) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.validator = validator;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Usuário inativo")
    })
    public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var email = authentication.getName();

        var tokenJWT = tokenService.gerarToken(validator.validarEmailLogin(email));
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}