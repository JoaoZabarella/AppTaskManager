package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.classes.usuario.OperacaoInvalidaException;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.dto.usuario.PaginaUsuarioDTO;
import com.example.taskmanager.mapper.UsuarioMapper;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.repository.annotation.AdminOnly;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
        validator.validarUsuarioNaoAdmin(usuario);

        usuario.adicionarRole("ROLE_ADMIN");
        repository.save(usuario);
        return ResponseEntity.ok(new DadosListagemUsuarioDTO(usuario));
    }

    @Transactional
    @AdminOnly
    public ResponseEntity<DadosListagemUsuarioDTO> removerAdmin(Long usuarioId) {
        logger.info("Solicitação para remover privilégio de admin do usuário com ID: {}", usuarioId);


        var usuario = validator.validarUsuario(usuarioId);


        if (!usuario.getRoles().contains("ROLE_ADMIN")) {
            logger.warn("Tentativa de remover privilégio admin de usuário que não é admin. ID: {}", usuarioId);
            throw new OperacaoInvalidaException("Operação inválida: este usuário não possui privilégios de administrador.");
        }

        if (isUltimoAdmin(usuario)) {
            logger.warn("Tentativa de remover o último administrador do sistema. ID: {}", usuarioId);
            throw new OperacaoInvalidaException("Operação inválida: não é possível remover o último administrador do sistema.");
        }


        usuario.removerRole("ROLE_ADMIN");
        repository.save(usuario);

        logger.info("Privilégio de admin removido com sucesso do usuário com ID: {}", usuarioId);
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
    public PaginaUsuarioDTO buscarUsuariosPorNomeOuEmail(String filtro, Pageable pageable){
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


    //Método auxiliar
    private boolean isUltimoAdmin(Usuario usuario) {
        long adminCount = repository.countByRolesContaining("ROLE_ADMIN");
        return adminCount <= 1 && usuario.getRoles().contains("ROLE_ADMIN");
    }
}
