package com.example.taskmanager.repository;

import com.example.taskmanager.dto.tarefa.TarefaEstatisticaDTO;
import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Page<Tarefa> findByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);
    Optional<Tarefa> findByIdAndAtivoTrue(Long id);
    Optional<Tarefa> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId );

    @Query("SELECT t from Tarefa t WHERE t.id IN :ids AND t.usuario.id = :usuarioId AND t.ativo = true")
    List<Tarefa> findAllByIdsAndUsuarioIdAndAtivoTrue(@Param("ids") List<Long> ids, @Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM Tarefa t WHERE t.id IN :ids AND t.usuario.id = :usuarioId")
    List<Tarefa> findAllByIdsAndUsuarioId(@Param("ids") List<Long> ids, @Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT t FROM Tarefa t WHERE t.usuario.id = :usuarioId AND t.ativo = true " +
            "AND (:statusId IS NULL OR t.status.id = :statusId) " +
            "AND (:prioridadeId IS NULL OR t.prioridade.id = :prioridadeId) " +
            "AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)",
            countQuery = "SELECT COUNT(t) FROM Tarefa t WHERE t.usuario.id = :usuarioId AND t.ativo = true " +
                    "AND (:statusId IS NULL OR t.status.id = :statusId) " +
                    "AND (:prioridadeId IS NULL OR t.prioridade.id = :prioridadeId) " +
                    "AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)")
    Page<Tarefa> buscarComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("statusId") Long statusId,
            @Param("prioridadeId") Long prioridadeId,
            @Param("categoriaId") Long categoriaId,
            Pageable pageable);


    @Query("SELECT t FROM Tarefa t WHERE t.usuario.id = :usuarioId AND t.ativo = true AND " +
            "(UPPER(t.titulo) LIKE UPPER(CONCAT('%', :termo, '%')) OR UPPER(t.descricao) LIKE UPPER(CONCAT('%', :termo, '%')))")
    Page<Tarefa> buscarPorPalavraChave(@Param("usuarioId") Long usuarioId, @Param("termo") String termo, Pageable pageable);

    @Query("""
    SELECT new com.example.taskmanager.dto.tarefa.TarefaEstatisticaDTO(
        COUNT(t),
        SUM(CASE WHEN t.dataConclusao IS NOT NULL THEN 1 ELSE 0 END),
        SUM(CASE WHEN t.status.id = 2 AND t.dataConclusao IS NULL THEN 1 ELSE 0 END),
        SUM(CASE WHEN t.prazo IS NOT NULL THEN 1 ELSE 0 END)
    )
    FROM Tarefa t
    WHERE t.usuario.id = :usuarioId AND t.ativo = true
""")
    TarefaEstatisticaDTO obterEstatisticasPorUsuario(@Param("usuarioId") Long usuarioId);

}
