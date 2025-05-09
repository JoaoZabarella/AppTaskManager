package com.example.taskmanager.repository;

import com.example.taskmanager.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Page<Categoria> findByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);

    Optional<Categoria> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);

    boolean existsByNomeAndUsuarioIdAndAtivoTrue(String nome, Long usuarioId);

}
