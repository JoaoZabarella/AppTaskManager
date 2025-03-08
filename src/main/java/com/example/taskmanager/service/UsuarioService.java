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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
    public ResponseEntity<DadosListagemUsuarioDTO> cadastraUsuario(DadosCadastroUsuario usuarioCadastro, UriComponentsBuilder uriBuilder) {

        usuarioValidator.validarEmail(usuarioCadastro.email());

        var usuario = UsuarioMapper.toEntity(usuarioCadastro, passwordEncoder);

            usuarioRepository.save(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemUsuarioDTO(usuario));
    }


    public Page<DadosListagemUsuarioDTO>listarDadosUsuarioAtivos(Pageable pageable){
    return usuarioRepository.findByAtivoTrue(pageable).
            map(DadosListagemUsuarioDTO::new);
    }

    @Transactional
    public DadosListagemUsuarioDTO atualizarDadosUsuario(DadosAtualizaUsuario dados) {
        var usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("ID:  " + dados.id()));
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
                .orElseThrow(() -> new UsuarioNaoEncontradoException("ID: " + id));
        usuario.desativar();
        usuarioRepository.save(usuario);
    }

    public DadosListagemUsuarioDTO buscarUsuario(Long id, String nome, String email){
        return usuarioRepository.buscarUsuario(id, nome, email)
                .map(DadosListagemUsuarioDTO::new)
                .orElseThrow(() -> {
                    String criterio = (id != null) ? "Id: " + id:
                                      (nome != null && nome.isBlank()) ? "Nome: " + nome:
                                      (email != null && nome.isBlank()) ? "Email: " + email : "Desconhecido";
                   return new UsuarioNaoEncontradoException(criterio);
                });
    }

 }

