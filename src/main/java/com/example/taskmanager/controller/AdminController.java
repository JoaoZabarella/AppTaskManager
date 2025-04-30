package com.example.taskmanager.controller;

import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.dto.usuario.PaginaUsuarioDTO;
import com.example.taskmanager.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }


    @PostMapping("/usuarios/promover/{id}")
    public ResponseEntity<DadosListagemUsuarioDTO> promoverParaAdmin(@PathVariable Long id) {
        return service.promoverParaAdmin(id);
    }

    @PutMapping("/usuarios/remover/{id}")
    public ResponseEntity<DadosListagemUsuarioDTO> removerParaAdmin(@PathVariable Long id) {
        return service.removerAdmin(id);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<DadosListagemUsuarioDTO> inativarUsuario(@PathVariable Long id) {
        service.inativarUsuarioComoAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios")
    public ResponseEntity<PaginaUsuarioDTO> buscarUsuarios(@RequestParam String filtro,
                                                           @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarUsuariosPorNoemOuEmail(filtro, pageable));
    }

    @PutMapping("/usuarios/ativar/{id}")
    public ResponseEntity<Void> ativarUsuario(@PathVariable Long id) {
        service.reativarUsuario(id);
        return ResponseEntity.ok().build();
    }
}
