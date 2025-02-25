package com.example.taskmanager.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.boot.BootLogging;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Usuario")
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O nome não pode estar em branco")
    private String nome;

    @NotNull(message = "O campo email não pode estar em branco")
    @Email(message = "O email deve ser válido")
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 6, max = 255, message = "A senha deve ter no minimo 6 digitos")
    private String senha;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    private Boolean ativo = true;



}
