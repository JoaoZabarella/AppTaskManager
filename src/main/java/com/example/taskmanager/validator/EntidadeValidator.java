package com.example.taskmanager.validator;

import com.example.taskmanager.config.exception.classes.categoria.CategoriaNameDuplicateException;
import com.example.taskmanager.config.exception.classes.categoria.CategoriaNotFoundException;
import com.example.taskmanager.config.exception.classes.prioridade.PrioridadeNotFoundException;
import com.example.taskmanager.config.exception.classes.status.StatusNotFoundException;
import com.example.taskmanager.config.exception.classes.tarefa.TarefaNotFoundException;
import com.example.taskmanager.config.exception.classes.usuario.*;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntidadeValidator {
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarefaRepository tarefaRepository;
    private final StatusRepository statusRepository;
    private final PrioridadeRepository prioridadeRepository;
    private final PasswordEncoder passwordEncoder;

    public EntidadeValidator(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository, StatusRepository statusRepository, PrioridadeRepository prioridadeRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
        this.statusRepository = statusRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario validarUsuario(Long usuarioId) {
        return usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
    }

    public Usuario validarUsuarioInativado(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
    }


    public void validarNomeUsuarioExistente(String nome) {
        if (usuarioRepository.existsByNomeIgnoreCase(nome)) {
            throw new UsernameExistException("Este nome de usuário ja esta em uso");
        }
    }

    public void validarEmailDuplicado(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailDuplicateException("Email inserido ja esta em uso");
        }
    }

    public void validarConfirmacaoDeSenha(String senha, String confirmaSenha) {
        if (!senha.equals(confirmaSenha)) {
            throw new PasswordConfirmationException("A confirmação de senha não é igual a senha");
        }
    }

    public Usuario validarEmailLogin(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EmailNotFoundException("Email não cadastrado: " + email));
    }

    public void validarSenhaAtual(String senhaAtual, String senhaCodificada){
        if(!passwordEncoder.matches(senhaAtual, senhaCodificada)){
            throw new PasswordNotActualException("Senha atual incorreta");
        }
    }
    public void validarConfirmacaoSenha(String novaSenha, String confirmacaoSenha) {
        if(!novaSenha.equals(confirmacaoSenha)){
            throw new PasswordConfirmationException("A nova senha e a confirmação não são iguais");
        }
    }

    public void validarUsuarioNaoAdmin(Usuario usuario) {
        if(usuario.getRoles().contains("ROLE_ADMIN")){
            throw new RuntimeException("Este usuario já é admin");
        }
    }

    public void validarUsuarioEhAdmin(Usuario usuario){
        if(usuario.getRoles().contains("ROLE_ADMIN")){
            throw new RuntimeException("Este usuário não é Admin");
        }
    }

    public Page<Usuario> validarBusca(String filtro, Pageable pageable) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return usuarioRepository.findAll(pageable);
        }

        Page<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(filtro, filtro, pageable);

        if(usuarios.isEmpty()){
            throw new RuntimeException("Nenhum usuario encontrado com o filtro informado");
        }

        return usuarios;
    }


    public void atualizarUsuario(Usuario usuario, DadosAtualizaUsuario dados) {

        if (dados.nome() != null && !dados.nome().isBlank() && !dados.nome().equals(usuario.getNome())) {
            validarNomeUsuarioExistente(dados.nome());
            usuario.setNome(dados.nome());
        }

        if (dados.email() != null && !dados.email().isBlank() && !dados.email().equals(usuario.getEmail())) {
            validarEmailDuplicado(dados.email());
            usuario.setEmail(dados.email());
        }
    }

    public Categoria validarCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNotFoundException("ID " + categoriaId));
    }

    public Categoria validarCategoriaDoUsuario(Long categoriaId, Long usuarioId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findByIdAndUsuarioIdAndAtivoTrue(categoriaId, usuarioId)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria ID " + categoriaId +
                        "Não encontrada ou não pertence ao usuário de ID: " + usuarioId));
    }

    public void validarNomeCategoriaDuplicado(String nome, Long usuarioId) {
        if (categoriaRepository.existsByNomeAndUsuarioIdAndAtivoTrue(nome, usuarioId)) {
            throw new CategoriaNameDuplicateException("Já existe uma categoria com este nome para este usuário");
        }
    }

    public Tarefa validarTarefa(Long tarefaId) {
        return tarefaRepository.findByIdAndAtivoTrue(tarefaId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa com ID " + tarefaId + " não encontrada"));
    }

    public Status validarStatus(Long statusId) {
        return statusRepository.findById(statusId)
                .orElseThrow(() -> new StatusNotFoundException("Status com o nome de: " + statusId + " não encontarado"));
    }

    public Prioridade validarPrioridade(Long prioridadeId) {
        return prioridadeRepository.findById(prioridadeId)
                .orElseThrow(() -> new PrioridadeNotFoundException("Prioridade com o nome de: " + prioridadeId + " não encontrado"));
    }

    public Tarefa validarTarefaDoUsuario(Long tarefaId, Long usuarioId){
        return tarefaRepository.findByIdAndUsuarioId(tarefaId, usuarioId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa com ID " +tarefaId+  "não ncontrado"));
    }

    public Tarefa validarTarefaArquivadaDoUsuario(Long tarefaId, Long usuarioId){
        return tarefaRepository.findByIdAndUsuarioId(tarefaId, usuarioId)
                .filter(tarefa -> !tarefa.isAtivo())
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa arquivada com ID " + tarefaId + "Não encontrado"));
    }







}


