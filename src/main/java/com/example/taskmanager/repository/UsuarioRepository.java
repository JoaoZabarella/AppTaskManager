package com.example.taskmanager.repository;

import com.example.taskmanager.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findByAtivoTrue(Pageable pageable);
    Boolean existsByEmailIgnoreCase(String email);
    Boolean existsByNomeIgnoreCase(String nome);
    Optional<Usuario> findByIdAndAtivoTrue(Long id);
    Page<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email, Pageable pageable);

    Optional<Usuario> findByEmailIgnoreCase(String email);
}



