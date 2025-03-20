    package com.example.taskmanager.controller;

    import com.example.taskmanager.config.security.TokenService;
    import com.example.taskmanager.dto.login.DadosAutenticacao;
    import com.example.taskmanager.dto.login.DadosTokenJWT;
    import com.example.taskmanager.model.Login;
    import jakarta.validation.Valid;
    import org.apache.coyote.Response;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/login")
    public class LoginController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private TokenService tokenService;

        @PostMapping
        public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid DadosAutenticacao dados) {
            try {
                var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
                var authentication = authenticationManager.authenticate(authenticationToken);

                var tokenJWT = tokenService.gerarToken((Login) authentication.getPrincipal());
                return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));

            } catch (Exception e) {
                return ResponseEntity.status(401).body(null);
            }
        }

    }
