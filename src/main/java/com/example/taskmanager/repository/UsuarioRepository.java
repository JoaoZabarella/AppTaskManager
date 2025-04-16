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


    @Query("SELECT DISTINCT u FROM Usuario u WHERE " +
            "((COALESCE(:id, NULL) IS NULL OR u.id = :id) OR " +
            "(COALESCE(:nome, NULL) IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) OR " +
            "(COALESCE(:email, NULL) IS NULL OR LOWER(u.email) = LOWER(:email))) " +
            "AND u.ativo = true")
    Optional<Usuario> buscarUsuario(
            @Param("id") Long id,
            @Param("nome") String nome,
            @Param("email") String email
    );

    Optional<Usuario> findByEmailIgnoreCase(String email);
}



