package com.example.taskmanager.validator;


import com.example.taskmanager.config.exception.classes.*;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.stereotype.Component;

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

    public Usuario validarUsuario(Long usuarioId){
        return usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
    }

    public Categoria validarCategoria(Long categoriaId){
        if(categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNotFoundException("ID " + categoriaId));
    }

    public Categoria validarCategoriaDoUsuario(Long categoriaId, Long usuarioId){
        if(categoriaId == null) {
            return null;
        }
        return categoriaRepository.findByIdAndUsuarioIdAndAtivoTrue(categoriaId, usuarioId)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria ID " + categoriaId +
                        "Não encontrada ou não pertence ao usuário de ID: " + usuarioId));
    }

    public void validarNomeCategoriaDuplicado(String nome, Long usuarioId){
        if(categoriaRepository.existsByNomeAndUsuarioIdAndAtivoTrue(nome, usuarioId)){
            throw new RuntimeException("Já existe uma categoria com este nome para este usuário");
        }
    }

    public Tarefa validarTarefa(Long tarefaId){
        return tarefaRepository.findByIdAndAtivoTrue(tarefaId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa com ID " +tarefaId+ " não encontrada"));
    }

    public Status validarStatus(String statusTexto){
        return statusRepository.findByTextoIgnoreCase(statusTexto)
                .orElseThrow(() -> new StatusNotFoundException("Status com o nome de: " +statusTexto + " não encontarado"));
    }

    public Prioridade validarPrioridade(String prioridadeTexto){
        return prioridadeRepository.findByTextoIgnoreCase(prioridadeTexto)
                .orElseThrow(() -> new PrioridadeNotFoundException("Prioridade com o nome de: " +prioridadeTexto + " não encontrado"));
    }

    public void validarEmailDuplicado(String email){
        if(usuarioRepository.existsByEmailIgnoreCase(email)){
            throw new EmailDuplicateException("Email inserido ja esta em uso");
        }
    }

    public void validarNomeUsuarioExistente(String nome){
        if(usuarioRepository.existsByNomeIgnoreCase(nome)){
            throw new UsernameExistException("Este nome de usuário ja esta em uso");
        }
    }

}
