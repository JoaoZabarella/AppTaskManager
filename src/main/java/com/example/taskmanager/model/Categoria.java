package com.example.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Categoria")
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O nome da categoria é obrigatório.")
    @Column(unique = true)
    private String nome;

    @OneToMany(mappedBy = "categoria")
    private List<Tarefa> tarefas;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "BOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    public Categoria(String nome) {
        this.nome = nome;
        this.ativo = true;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(@NotNull(message = "O nome da categoria é obrigatório.") String nome) {
        this.nome = nome;
    }

    //Lomboock não reconhece o getNome
    public String getNome() {
        return nome;
    }
    //Lomboock não reconhece o getId
    public Long getId() {
        return id;
    }

    public void desativar(){
        this.ativo = false;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
