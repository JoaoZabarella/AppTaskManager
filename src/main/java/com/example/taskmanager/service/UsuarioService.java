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
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioService(UsuarioRepository usuarioRepository, EntidadeValidator validator, PasswordEncoder passwordEncoder, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
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

    public ResponseEntity<DadosListagemUsuarioDTO> listarDadosUsuarioAtivos() {
        logger.info("BUSCANDO DADOS DO USUÁRIO");

        Long usuarioId = usuarioAutenticadoService.obterIdUsuarioAutenticado();
        logger.info("Retornando os dados do usuaário de ID {}", usuarioId);

        DadosListagemUsuarioDTO dadosUsuario = new DadosListagemUsuarioDTO( validator.validarUsuario(usuarioId));

        return ResponseEntity.ok(dadosUsuario);
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        Long usuarioId = usuarioAutenticadoService.obterIdUsuarioAutenticado();
        logger.info("Atualizando dados do usuário com ID: {}", dados.id());

        var usuario = validator.validarUsuario(usuarioId);
        validator.atualizarUsuario(usuario, dados);

        logger.info("Usuário com ID: {} atualizado com sucesso", dados.id());
        return ResponseEntity.ok(new DadosListagemUsuarioDTO(usuario));

    }

    @Transactional
    public ResponseEntity<Void> inativar() {
        Long usuarioId = usuarioAutenticadoService.obterIdUsuarioAutenticado();
        logger.info("Inativando usuário com ID: {}", usuarioId);
        var usuario = validator.validarUsuario(usuarioId);

        usuario.desativar();
        usuarioRepository.save(usuario);

        logger.info("Usuário com ID: {} inativado com sucesso", usuarioId);
        return ResponseEntity.noContent().build();
    }
}
