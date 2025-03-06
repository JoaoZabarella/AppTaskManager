package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.UsuarioNaoEncontradoException;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.mapper.UsuarioMapper;
import com.example.taskmanager.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioValidator usuarioValidator;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioValidator usuarioValidator, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioValidator = usuarioValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public DadosListagemUsuarioDTO cadastraUsuario(DadosCadastroUsuario usuarioCadastro, UriComponentsBuilder uriBuilder) {

        usuarioValidator.validarEmail(usuarioCadastro.email());

        var usuario = UsuarioMapper.toEntity(usuarioCadastro, passwordEncoder);


        usuarioRepository.save(usuario);
        return new DadosListagemUsuarioDTO(usuario);
    }


    public Page<DadosListagemUsuarioDTO>listarDadosUsuarioAtivos(Pageable pageable){
    return usuarioRepository.findByAtivoTrue(pageable).
            map(DadosListagemUsuarioDTO::new);
    }

    @Transactional
    public DadosListagemUsuarioDTO atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        var usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(dados.id()));
        if (dados.nome() != null && !dados.nome().isBlank()) {
            usuario.setNome(dados.nome());
        }

        if (dados.email() != null && !dados.email().isBlank()) {
            usuario.setEmail(dados.email());
        }
        return new DadosListagemUsuarioDTO(usuario);
    }

    @Transactional
    public void inativar(Long id){
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuario.desativar();
    }

    public Page<DadosListagemUsuarioDTO> listarTodosUsuarios(Long id, String nome, String email, Pageable pageable){
        return usuarioRepository.buscarUsuarios(id, nome, email, pageable)
                .map(DadosListagemUsuarioDTO::new);
    }


}
