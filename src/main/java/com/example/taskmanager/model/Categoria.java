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

    public void inativar() {
        this.ativo = false;
    }

    public static Categoria criarParaUsuario(String nome, Usuario usuario) {
        Categoria categoria = new Categoria(nome);
        categoria.setUsuario(usuario);
        return categoria;
    }



}
