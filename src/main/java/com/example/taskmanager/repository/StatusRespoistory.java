package com.example.taskmanager.repository;

import com.example.taskmanager.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRespoistory extends JpaRepository<Status, Integer> {
}
