package com.example.taskmanager.model;

import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Usuario")
@Table(name = "usuarios")
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();


    public Usuario(DadosCadastroUsuario dados, PasswordEncoder passwordEncoder) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.senha = passwordEncoder.encode(dados.senha());
    }

    public void desativar() {
        this.ativo = false;
    }
    public void ativar() {
        this.ativo = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public void adicionarRole(String role) {
        if(this.roles == null){
            this.roles = new HashSet<>();
        }
        if(!role.startsWith("ROLE_")){
            role = "ROLE_" +role;
        }
        this.roles.add(role);
    }

    public void removerRole(String role) {
        if (this.roles == null || !this.roles.contains(role)) {
            return;
        }
        this.roles.remove(role);
    }

    public boolean possuiRole(String role){
        return this.roles != null && this.roles.contains(role);
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

}
