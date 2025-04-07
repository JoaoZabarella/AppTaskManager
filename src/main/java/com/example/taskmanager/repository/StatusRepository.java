package com.example.taskmanager.repository;

import com.example.taskmanager.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    @Query("SELECT s FROM Status s WHERE LOWER(s.texto) = LOWER(:texto)")
    Optional<Status> findByTextoIgnoreCase(@Param("texto") String texto);

}
