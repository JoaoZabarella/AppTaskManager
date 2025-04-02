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
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    public Tarefa(DadosCriarTarefa dados) {
        this.titulo = dados.titulo();
        this.descricao = dados.descricao();
        this.prioridade = Prioridade.MEDIA;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }


    public @NotNull(message = "O titulo não pode estar em branco") @Size(max = 100, message = "O titulo deve ter no máximo 100 caractéres") String getTitulo() {
        return titulo;
    }

    public @Size(max = 500, message = "A descricao não pode exceder 500 caracteres") String getDescricao() {
        return descricao;
    }

    public Status getStatus() {
        return status;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(@NotNull(message = "O titulo não pode estar em branco") @Size(max = 100, message = "O titulo deve ter no máximo 100 caractéres") String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(@Size(max = 500, message = "A descricao não pode exceder 500 caracteres") String descricao) {
        this.descricao = descricao;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}