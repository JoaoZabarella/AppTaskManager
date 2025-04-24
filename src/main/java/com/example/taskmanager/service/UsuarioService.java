package com.example.taskmanager.service;

import com.example.taskmanager.config.security.RoleConst;
import com.example.taskmanager.dto.usuario.AlterarSenhaDTO;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.mapper.UsuarioMapper;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
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


    private Usuario getUsuarioLogado(){
        return validator.validarUsuario(obterUsuario());
    }

    public UsuarioService(UsuarioRepository usuarioRepository, EntidadeValidator validator, PasswordEncoder passwordEncoder, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public Long obterUsuario(){
        return usuarioAutenticadoService.obterIdUsuarioAutenticado();
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> cadastraUsuario(DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        logger.info("Iniciando cadastro de usuário com e-mail: {}", dados.email());

        validator.validarEmailDuplicado(dados.email());
        validator.validarNomeUsuarioExistente(dados.nome());
        validator.validarConfirmacaoDeSenha(dados.senha(), dados.confirmaSenha());
        var usuario = UsuarioMapper.toEntity(dados, passwordEncoder);

        usuario.adicionarRole(RoleConst.ROLE_USER);
        usuarioRepository.save(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        logger.info("Usuário cadastrado com sucesso, ID: {}", usuario.getId());
        return ResponseEntity.created(uri).body(new DadosListagemUsuarioDTO(usuario));
    }

    public ResponseEntity<DadosListagemUsuarioDTO> buscarUsuarioLogado() {
        logger.info("Retornando os dados do usuaário de ID {}", obterUsuario());

        DadosListagemUsuarioDTO dadosUsuario = new DadosListagemUsuarioDTO(getUsuarioLogado());
        return ResponseEntity.ok(dadosUsuario);
    }

    @Transactional
    public ResponseEntity<DadosListagemUsuarioDTO> atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        logger.info("Atualizando dados do usuário com ID: {}", obterUsuario());

        validator.atualizarUsuario(getUsuarioLogado(), dados);

        logger.info("Usuário com ID: {} atualizado com sucesso", dados.id());
        return ResponseEntity.ok(new DadosListagemUsuarioDTO(getUsuarioLogado()));

    }

    @Transactional
    public ResponseEntity<Void> inativar() {
        logger.info("Inativando usuário com ID: {}", obterUsuario());


        getUsuarioLogado().desativar();
        usuarioRepository.save(getUsuarioLogado());

        logger.info("Usuário com ID: {} inativado com sucesso", obterUsuario());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> alterarSenha(AlterarSenhaDTO dados){

        validator.validarSenhaAtual(dados.senhaAtual(), getUsuarioLogado().getSenha());
        validator.validarConfirmacaoSenha(dados.novaSenha(), dados.confirmaSenha());

        String senhaCodificada = passwordEncoder.encode(dados.novaSenha());
        getUsuarioLogado().setSenha(senhaCodificada);
        usuarioRepository.save(getUsuarioLogado());
        return ResponseEntity.noContent().build();

    }




}
