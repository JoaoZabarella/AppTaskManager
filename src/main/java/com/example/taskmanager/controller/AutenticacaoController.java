package com.example.taskmanager.controller;

import com.example.taskmanager.config.security.service.TokenService;
import com.example.taskmanager.dto.autenticacao.DadosAutenticacao;
import com.example.taskmanager.dto.autenticacao.DadosTokenJWT;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EntidadeValidator validator;
    private final UsuarioRepository repository;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, EntidadeValidator validator, UsuarioRepository repository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.validator = validator;
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var email = authentication.getName();

        var tokenJWT = tokenService.gerarToken(validator.validarEmailLogin(email));
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}
