package com.example.taskmanager.model;

import com.example.taskmanager.dto.tarefa.DadosCriarTarefa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity(name = "Tarefa")
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O titulo não pode estar em branco")
    @Size(max = 100, message = "O titulo deve ter no máximo 100 caractéres")
    private String titulo;

    @Size(max = 500, message = "A descricao não pode exceder 500 caracteres")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "prioridade_id", nullable = false)
    private Prioridade prioridade;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    @Column(name = "prazo")
    private LocalDateTime prazo;

    public Tarefa() {
        this.ativo = true;
        this.comentarios = new ArrayList<>();
    }

    public Tarefa(DadosCriarTarefa dados) {
        this.titulo = dados.titulo();
        this.descricao = dados.descricao();
        this.prazo = dados.prazo();
        this.ativo = true;
    }

    public void concluir() {
        this.dataConclusao = LocalDateTime.now();
    }

    public boolean isConcluida() {
        return this.dataConclusao != null;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void reabrir(){
        this.dataConclusao = null;
    }

}