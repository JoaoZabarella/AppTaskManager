package com.example.taskmanager.dto.tarefa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


public record DadosCriarTarefa (String titulo, String descricao, LocalDateTime prazo, Boolean concluida) {
}
