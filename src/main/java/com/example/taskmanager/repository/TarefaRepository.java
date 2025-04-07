package com.example.taskmanager.repository;

import com.example.taskmanager.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Page<Tarefa> findByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);

}
