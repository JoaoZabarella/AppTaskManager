package com.example.taskmanager.validator;


import com.example.taskmanager.config.exception.CategoriaNotFoundException;
import com.example.taskmanager.config.exception.UsuarioNotFoundException;
import com.example.taskmanager.model.Categoria;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.CategoriaRepository;
import com.example.taskmanager.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class EntidadeValidator {
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public EntidadeValidator(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
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

}
