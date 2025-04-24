package com.example.taskmanager.service;

import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.dto.usuario.PaginaUsuarioDTO;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.repository.annotation.AdminOnly;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final EntidadeValidator validator;
    private final UsuarioRepository repository;

    public AdminService(EntidadeValidator validator, UsuarioRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }


    @Transactional
    @AdminOnly
    public ResponseEntity<DadosListagemUsuarioDTO> promoverParaAdmin(Long usuarioId){
        logger.info("Promovendo usuário com ID: {}", usuarioId);
        var usuario = validator.validarUsuario(usuarioId);
        validator.validarUsuarioAdmin(usuario);

        usuario.adicionarRole("ROLE_ADMIN");
        repository.save(usuario);
        return ResponseEntity.ok(new DadosListagemUsuarioDTO(usuario));
    }

    @Transactional
    @AdminOnly
    public void inativarUsuarioComoAdmin(Long usuarioId){
        logger.info("Administrador inativando usuário com ID: {}", usuarioId);
        var usuario = validator.validarUsuario(usuarioId);

        usuario.desativar();
        repository.save(usuario);

        logger.info("Usuário com ID: {} inativado com sucesso", usuarioId);
    }

    @AdminOnly
    public PaginaUsuarioDTO buscarUsuariosPorNoemOuEmail(String filtro, Pageable pageable){
        var usuarios = validator.validarBusca(filtro, pageable);

        Page<DadosListagemUsuarioDTO> pageConvertida = usuarios.map(DadosListagemUsuarioDTO::new);
        return PaginaUsuarioDTO.from(pageConvertida);
    }

    @Transactional
    @AdminOnly
    public void reativarUsuario(Long usuarioId){
        logger.info("Reativando um usuário com ID {}", usuarioId);
        var usuario = validator.validarUsuarioInativado(usuarioId);
        usuario.ativar();
        repository.save(usuario);
    }

    @Transactional
    @AdminOnly
    public void removerAdminUsuario(Long usuarioId){
        logger.info("Tirando o privilégio de admin do usuario com ID {}", usuarioId);
        var usuario = validator.validarUsuarioInativado(usuarioId);
        usuario.removerRole("");
    }
}
