package com.example.taskmanager.controller;

import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.dto.usuario.PaginaUsuarioDTO;
import com.example.taskmanager.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administração", description = "Endpoints administrativos (apenas ROLE_ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }


    @PostMapping("/usuarios/promover/{id}")
    @Operation(summary = "Promover usuário para admin", description = "Adiciona a role ROLE_ADMIN a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário promovido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário já é admin"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<DadosListagemUsuarioDTO> promoverParaAdmin(@PathVariable Long id) {
        return service.promoverParaAdmin(id);
    }

    @PutMapping("/usuarios/remover/{id}")
    @Operation(summary = "Remover privilégio admin", description = "Remove a role ROLE_ADMIN de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Privilégio admin removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não é admin ou é o último admin"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<DadosListagemUsuarioDTO> removerParaAdmin(@PathVariable Long id) {
        return service.removerAdmin(id);
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Inativar usuário", description = "Inativa um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<DadosListagemUsuarioDTO> inativarUsuario(@PathVariable Long id) {
        service.inativarUsuarioComoAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Buscar usuários", description = "Busca usuários por nome ou email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<PaginaUsuarioDTO> buscarUsuarios(
            @RequestParam String filtro,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarUsuariosPorNomeOuEmail(filtro, pageable));
    }

    @PutMapping("/usuarios/ativar/{id}")
    @Operation(summary = "Reativar usuário", description = "Reativa um usuário inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário reativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> ativarUsuario(@PathVariable Long id) {
        service.reativarUsuario(id);
        return ResponseEntity.ok().build();
    }
}