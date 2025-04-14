package com.example.taskmanager.model;

import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Entity(name = "Usuario")
@Table(name = "usuarios")
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    private String senha;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    public Usuario(DadosCadastroUsuario dados, PasswordEncoder passwordEncoder) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.senha = passwordEncoder.encode(dados.senha());
    }

    public void desativar() {
        this.ativo = false;
    }

    public Usuario() {
    }
}
