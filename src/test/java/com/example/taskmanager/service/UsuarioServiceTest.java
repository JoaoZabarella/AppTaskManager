package com.example.taskmanager.service;

import com.example.taskmanager.config.exception.classes.usuario.UsuarioNotFoundException;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EntidadeValidator validator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private DadosCadastroUsuario dados;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste Usuario");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha_encoded");
        usuario.setAtivo(true);

        dados = new DadosCadastroUsuario("Teste Usuario", "teste@example.com", "senha_encoded", "senha_encoded");

        uriBuilder = UriComponentsBuilder.fromHttpUrl("/");
    }

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    void testCadastrarUsuario() {
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encoded");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setId(1L);
                    return usuario;
                } );
        doNothing().when(validator).validarEmailDuplicado(anyString());

        ResponseEntity<DadosListagemUsuarioDTO> response = usuarioService.cadastraUsuario(dados, uriBuilder);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(usuario.getId(), response.getBody().id());
        assertEquals(usuario.getNome(), response.getBody().nome());
        assertEquals(usuario.getEmail(), response.getBody().email());

        verify(validator).validarEmailDuplicado(dados.email());
        verify(validator).validarNomeUsuarioExistente(dados.nome());
        verify(passwordEncoder).encode(dados.senha());
        verify(usuarioRepository).save(any(Usuario.class));

    }

    @Test
    @DisplayName("Deve listar usuários ativos")
    void testListarUsuariosAtivos() {
        Page<Usuario> page = new PageImpl<>(List.of(usuario));
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findByAtivoTrue(pageable)).thenReturn(page);

        ResponseEntity<DadosListagemUsuarioDTO> result = usuarioService.buscarUsuarioLogado();

        assertNotNull(result);
        assertEquals(1, result.getBody());
        assertEquals(usuario.getId(), result.getBody());

        verify(usuarioRepository).findByAtivoTrue( pageable);
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário com sucesso")
    void testAtualizarUsuario() {
        DadosAtualizaUsuario dadosAtualizaUsuario = new DadosAtualizaUsuario(1L, "Nome Atualizado", "autalizado@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<DadosListagemUsuarioDTO> result = usuarioService.atualizarDadosUsuario(dadosAtualizaUsuario);

        assertNotNull(result);
        assertEquals(dadosAtualizaUsuario.nome(), result.getBody().nome());
        assertEquals(dadosAtualizaUsuario.email(), result.getBody().email());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar usuario inexistente")
    void testAtualizarDadosUsuarioInexistente() {
        DadosAtualizaUsuario dadosAtualizaUsuario = new DadosAtualizaUsuario(999L, "Nome Atualizado", "autalizado@example.com");

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.atualizarDadosUsuario(dadosAtualizaUsuario);
        });

        verify(usuarioRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve inativar um usuário com sucesso")
    void testInativar(){
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        usuarioService.inativar();
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        assertFalse(usuarioCaptor.getValue().isAtivo());
    }
}