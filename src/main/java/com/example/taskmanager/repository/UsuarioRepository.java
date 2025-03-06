package com.example.taskmanager.repository;

import com.example.taskmanager.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findByAtivoTrue(Pageable pageable);
    Boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);


    //Responsável por filtrar dados para buscar usuarios
    @Query("SELECT u FROM Usuario u WHERE " +
            "(:id IS NULL OR u.id = :id) AND"+
            "(:nome IS NULL OR u.nome LIKE %:nome%) AND " +
            "(:email IS NULL OR u.email = :email) AND " +
            "u.ativo = true")
    Page<Usuario> buscarUsuarios(
        @Param("id") Long id,
        @Param("nome") String nome,
        @Param("email") String email,
        Pageable pageable
        );

}
