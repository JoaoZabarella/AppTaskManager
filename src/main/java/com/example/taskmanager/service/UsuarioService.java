package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.classes.usuario.UsuarioNotFoundException;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.mapper.UsuarioMapper;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;


@Service
public class UsuarioService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final EntidadeValidator validator;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, EntidadeValidator validator, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> cadastraUsuario(DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        logger.info("Iniciando cadastro de usuário com e-mail: {}", dados.email());

        validator.validarEmailDuplicado(dados.email());
        validator.validarNomeUsuarioExistente(dados.nome());
        validator.validarConfirmacaoDeSenha(dados.senha(), dados.confirmaSenha());
        var usuario = UsuarioMapper.toEntity(dados, passwordEncoder);

        usuarioRepository.save(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        logger.info("Usuário cadastrado com sucesso, ID: {}", usuario.getId());
        return ResponseEntity.created(uri).body(new DadosListagemUsuarioDTO(usuario));
    }

    public ResponseEntity<Page<DadosListagemUsuarioDTO>> listarDadosUsuarioAtivos(Pageable pageable) {
        logger.info("Listando usuários ativos");

        Page<DadosListagemUsuarioDTO> usuarios  = usuarioRepository.findByAtivoTrue(pageable)
                .map(DadosListagemUsuarioDTO::new);

        return ResponseEntity.ok(usuarios);
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        logger.info("Atualizando dados do usuário com ID: {}", dados.id());

        var usuario = validator.validarUsuario(dados.id());
        validator.atualizarUsuario(usuario, dados);

        logger.info("Usuário com ID: {} atualizado com sucesso", dados.id());
        return ResponseEntity.ok(new DadosListagemUsuarioDTO(usuario));

    }

    @Transactional
    public ResponseEntity<Void> inativar(Long id) {
        logger.info("Inativando usuário com ID: {}", id);
        var usuario = validator.validarUsuario(id);

        usuario.desativar();
        usuarioRepository.save(usuario);

        logger.info("Usuário com ID: {} inativado com sucesso", id);
        return ResponseEntity.noContent().build();
    }

    public DadosListagemUsuarioDTO buscarUsuario(Long id, String nome, String email) {
        logger.info("Buscando usuário com critérios: ID = {}, Nome = {}, Email = {}", id, nome, email);
        return usuarioRepository.buscarUsuario(id, nome, email)
                .map(DadosListagemUsuarioDTO::new)
                .orElseThrow(() -> {
                    String criterio = (id != null) ? "Id: " + id :
                            (nome != null) ? "Nome: " + nome :
                                    (email != null) ? "Email: " + email : "Desconhecido";
                    logger.error("Usuário não encontrado com critério: {}", criterio);
                    return new UsuarioNotFoundException(criterio);
                });
    }
}
