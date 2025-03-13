package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.UsuarioNaoEncontradoException;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.mapper.UsuarioMapper;
import com.example.taskmanager.repository.UsuarioRepository;
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
    private final UsuarioValidator usuarioValidator;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioValidator usuarioValidator, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioValidator = usuarioValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> cadastraUsuario(DadosCadastroUsuario usuarioCadastro, UriComponentsBuilder uriBuilder) {
        logger.info("Iniciando cadastro de usuário com e-mail: {}", usuarioCadastro.email());

        usuarioValidator.validarEmail(usuarioCadastro.email());
        var usuario = UsuarioMapper.toEntity(usuarioCadastro, passwordEncoder);

        usuarioRepository.save(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        logger.info("Usuário cadastrado com sucesso, ID: {}", usuario.getId());
        return ResponseEntity.created(uri).body(new DadosListagemUsuarioDTO(usuario));
    }

    public Page<DadosListagemUsuarioDTO> listarDadosUsuarioAtivos(Pageable pageable) {
        logger.info("Listando usuários ativos");
        return usuarioRepository.findByAtivoTrue(pageable).map(DadosListagemUsuarioDTO::new);
    }

    @Transactional
    public DadosListagemUsuarioDTO atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        logger.info("Atualizando dados do usuário com ID: {}", dados.id());

        var usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado, ID: {}", dados.id());
                    return new UsuarioNaoEncontradoException("ID:  " + dados.id());
                });

        if (dados.nome() != null && !dados.nome().isBlank()) {
            usuario.setNome(dados.nome());
        }

        if (dados.email() != null && !dados.email().isBlank()) {
            usuario.setEmail(dados.email());
        }

        logger.info("Usuário com ID: {} atualizado com sucesso", dados.id());
        return new DadosListagemUsuarioDTO(usuario);
    }

    @Transactional
    public void inativar(Long id) {
        logger.info("Inativando usuário com ID: {}", id);

        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado para inativação, ID: {}", id);
                    return new UsuarioNaoEncontradoException("ID: " + id);
                });

        usuario.desativar();
        usuarioRepository.save(usuario);

        logger.info("Usuário com ID: {} inativado com sucesso", id);
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
                    return new UsuarioNaoEncontradoException(criterio);
                });
    }
}
