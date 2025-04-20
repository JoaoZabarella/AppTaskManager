package com.example.taskmanager.validator;

import com.example.taskmanager.config.exception.classes.categoria.CategoriaNameDuplicateException;
import com.example.taskmanager.config.exception.classes.categoria.CategoriaNotFoundException;
import com.example.taskmanager.config.exception.classes.prioridade.PrioridadeNotFoundException;
import com.example.taskmanager.config.exception.classes.status.StatusNotFoundException;
import com.example.taskmanager.config.exception.classes.tarefa.TarefaNotFoundException;
import com.example.taskmanager.config.exception.classes.usuario.*;
import com.example.taskmanager.dto.categoria.DadosAtualizaCategoria;
import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class EntidadeValidator {
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarefaRepository tarefaRepository;
    private final StatusRepository statusRepository;
    private final PrioridadeRepository prioridadeRepository;

    public EntidadeValidator(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository, StatusRepository statusRepository, PrioridadeRepository prioridadeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
        this.statusRepository = statusRepository;
        this.prioridadeRepository = prioridadeRepository;
    }

    public Usuario validarUsuario(Long usuarioId) {
        return usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
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

    public Tarefa validarTarefa(Long tarefaId,Long usuarioId) {
        return tarefaRepository.findByIdAndAtivoTrue(tarefaId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa com ID " + tarefaId + " não encontrada"));
    }

    public Status validarStatus(String statusTexto) {
        return statusRepository.findByTextoIgnoreCase(statusTexto)
                .orElseThrow(() -> new StatusNotFoundException("Status com o nome de: " + statusTexto + " não encontarado"));
    }

    public Prioridade validarPrioridade(String prioridadeTexto) {
        return prioridadeRepository.findByTextoIgnoreCase(prioridadeTexto)
                .orElseThrow(() -> new PrioridadeNotFoundException("Prioridade com o nome de: " + prioridadeTexto + " não encontrado"));
    }

    public void validarEmailDuplicado(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailDuplicateException("Email inserido ja esta em uso");
        }
    }

    public void validarNomeUsuarioExistente(String nome) {
        if (usuarioRepository.existsByNomeIgnoreCase(nome)) {
            throw new UsernameExistException("Este nome de usuário ja esta em uso");
        }
    }

    public Usuario validarEmailLogin(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EmailNotFoundException("Email não cadastrado: " + email));
    }

    public void validarConfirmacaoDeSenha(String senha, String confirmaSenha) {
        if (!senha.equals(confirmaSenha)) {
            throw new PasswordConfirmationException("A confirmação de senha não é igual a senha");
        }
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

    public void validarTarefaNaoConcluida(Tarefa tarefa) {
        if (tarefa.isConcluida()) {
            throw new RuntimeException("Esta tarefa ja esta concluida");
        }
    }

}


