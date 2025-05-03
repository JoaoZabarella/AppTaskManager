package com.example.taskmanager.service;

import com.example.taskmanager.config.security.RoleConst;
import com.example.taskmanager.dto.usuario.AlterarSenhaDTO;
import com.example.taskmanager.dto.usuario.DadosAtualizaUsuario;
import com.example.taskmanager.dto.usuario.DadosCadastroUsuario;
import com.example.taskmanager.dto.usuario.DadosListagemUsuarioDTO;
import com.example.taskmanager.model.Usuario;
import com.example.taskmanager.repository.UsuarioRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

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

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private DadosCadastroUsuario dadosCadastro;
    private UriComponentsBuilder uriBuilder;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = 1L;

        usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        usuarioMock.setNome("Teste Usuario");
        usuarioMock.setEmail("teste@email.com");
        usuarioMock.setSenha("senha123");
        usuarioMock.setAtivo(true);
        Set<String> roles = new HashSet<>();
        roles.add(RoleConst.ROLE_USER);
        usuarioMock.setRoles(roles);

        dadosCadastro = new DadosCadastroUsuario(
                "Teste Usuario",
                "teste@email.com",
                "senha123",
                "senha123"
        );

        uriBuilder = UriComponentsBuilder.fromPath("/");

        lenient().when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);
        lenient().when(validator.validarUsuario(usuarioId)).thenReturn(usuarioMock);
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("senhaCodificada");
    }

    @Nested
    @DisplayName("Testes para obterUsuario")
    class ObterUsuarioTests {
        @Test
        @DisplayName("Deve retornar o ID do usuário logado")
        void deveObterIdUsuarioLogado() {
            reset(usuarioAutenticadoService);
            when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

            Long result = usuarioService.obterUsuario();

            assertEquals(usuarioId, result);
            verify(usuarioAutenticadoService).obterIdUsuarioAutenticado();
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não está autenticado")
        void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {
            reset(usuarioAutenticadoService);
            when(usuarioAutenticadoService.obterIdUsuarioAutenticado())
                    .thenThrow(new RuntimeException("Usuário não autenticado"));

            assertThrows(RuntimeException.class, () -> {
                usuarioService.obterUsuario();
            });
        }
    }

    @Nested
    @DisplayName("Testes para cadastraUsuario")
    class CadastrarUsuarioTests {
        @Test
        @DisplayName("Deve cadastrar um novo usuário com sucesso")
        void deveCadastrarNovoUsuario() {
            doAnswer(invocation -> {
                Usuario usuario = invocation.getArgument(0);
                usuario.setId(usuarioId);
                return usuario;
            }).when(usuarioRepository).save(any(Usuario.class));

            ResponseEntity<DadosListagemUsuarioDTO> response = usuarioService.cadastraUsuario(dadosCadastro, uriBuilder);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(usuarioId, response.getBody().id());

            verify(validator).validarEmailDuplicado(dadosCadastro.email());
            verify(validator).validarNomeUsuarioExistente(dadosCadastro.nome());
            verify(validator).validarConfirmacaoDeSenha(dadosCadastro.senha(), dadosCadastro.confirmaSenha());
            verify(passwordEncoder).encode(dadosCadastro.senha());
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void deveLancarExcecaoQuandoEmailJaExiste() {
            doThrow(new RuntimeException("Email já cadastrado")).when(validator)
                    .validarEmailDuplicado(dadosCadastro.email());

            assertThrows(RuntimeException.class, () -> {
                usuarioService.cadastraUsuario(dadosCadastro, uriBuilder);
            });

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome de usuário já existe")
        void deveLancarExcecaoQuandoNomeUsuarioJaExiste() {
            doThrow(new RuntimeException("Nome de usuário já existe")).when(validator)
                    .validarNomeUsuarioExistente(dadosCadastro.nome());

            assertThrows(RuntimeException.class, () -> {
                usuarioService.cadastraUsuario(dadosCadastro, uriBuilder);
            });

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando senhas não conferem")
        void deveLancarExcecaoQuandoSenhasNaoConferem() {
            DadosCadastroUsuario dadosComSenhasDiferentes = new DadosCadastroUsuario(
                    "Teste Usuario",
                    "teste@email.com",
                    "senha123",
                    "senha456"
            );

            doThrow(new RuntimeException("Senhas não conferem")).when(validator)
                    .validarConfirmacaoDeSenha(dadosComSenhasDiferentes.senha(), dadosComSenhasDiferentes.confirmaSenha());

            assertThrows(RuntimeException.class, () -> {
                usuarioService.cadastraUsuario(dadosComSenhasDiferentes, uriBuilder);
            });

            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes para buscarUsuarioLogado")
    class BuscarUsuarioLogadoTests {
        @Test
        @DisplayName("Deve buscar dados do usuário logado")
        void deveBuscarDadosUsuarioLogado() {
            reset(usuarioAutenticadoService);
            when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

            ResponseEntity<DadosListagemUsuarioDTO> response = usuarioService.buscarUsuarioLogado();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(usuarioId, response.getBody().id());

            verify(usuarioAutenticadoService, atLeastOnce()).obterIdUsuarioAutenticado();
            verify(validator).validarUsuario(usuarioId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            reset(validator);
            when(validator.validarUsuario(usuarioId))
                    .thenThrow(new RuntimeException("Usuário não encontrado"));

            assertThrows(RuntimeException.class, () -> {
                usuarioService.buscarUsuarioLogado();
            });
        }
    }

    @Nested
    @DisplayName("Testes para atualizarDadosUsuario")
    class AtualizarDadosUsuarioTests {
        @Test
        @DisplayName("Deve atualizar dados do usuário")
        void deveAtualizarDadosUsuario() {
            DadosAtualizaUsuario dadosAtualizacao = new DadosAtualizaUsuario(
                    usuarioId,
                    "Nome Atualizado",
                    "atualizado@email.com"
            );

            ResponseEntity<DadosListagemUsuarioDTO> response = usuarioService.atualizarDadosUsuario(dadosAtualizacao);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            verify(validator).atualizarUsuario(usuarioMock, dadosAtualizacao);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar dados com ID inválido")
        void deveLancarExcecaoAoAtualizarComIdInvalido() {
            DadosAtualizaUsuario dadosAtualizacao = new DadosAtualizaUsuario(
                    999L,
                    "Nome Atualizado",
                    "atualizado@email.com"
            );

            doThrow(new RuntimeException("ID de usuário inválido")).when(validator)
                    .atualizarUsuario(usuarioMock, dadosAtualizacao);

            assertThrows(RuntimeException.class, () -> {
                usuarioService.atualizarDadosUsuario(dadosAtualizacao);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com email já existente")
        void deveLancarExcecaoAoAtualizarComEmailJaExistente() {
            DadosAtualizaUsuario dadosAtualizacao = new DadosAtualizaUsuario(
                    usuarioId,
                    "Nome Atualizado",
                    "existente@email.com"
            );

            doThrow(new RuntimeException("Email já cadastrado")).when(validator)
                    .atualizarUsuario(usuarioMock, dadosAtualizacao);

            assertThrows(RuntimeException.class, () -> {
                usuarioService.atualizarDadosUsuario(dadosAtualizacao);
            });
        }

        @Nested
        @DisplayName("Testes para inativar")
        class InativarTests {
            @Test
            @DisplayName("Deve inativar o usuário logado")
            void deveInativarUsuarioLogado() {
                reset(usuarioAutenticadoService);
                reset(validator);
                when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);
                when(validator.validarUsuario(usuarioId)).thenReturn(usuarioMock);

                ResponseEntity<Void> response = usuarioService.inativar();

                assertNotNull(response);
                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

                verify(usuarioAutenticadoService, atLeastOnce()).obterIdUsuarioAutenticado();
                verify(validator, atLeastOnce()).validarUsuario(usuarioId);
                verify(usuarioRepository).save(usuarioMock);
            }
            @Test
            @DisplayName("Deve verificar se o método desativar do usuário foi chamado")
            void deveVerificarMetodoDesativar() {
                Usuario usuarioSpy = spy(usuarioMock);
                reset(validator);
                when(validator.validarUsuario(usuarioId)).thenReturn(usuarioSpy);

                usuarioService.inativar();

                verify(usuarioSpy).desativar();
                verify(usuarioRepository).save(usuarioSpy);
            }
        }

        @Nested
        @DisplayName("Testes para alterarSenha")
        class AlterarSenhaTests {
            @Test
            @DisplayName("Deve alterar senha do usuário")
            void deveAlterarSenha() {
                AlterarSenhaDTO dadosAlterarSenha = new AlterarSenhaDTO(
                        "senhaAtual",
                        "novaSenha",
                        "novaSenha"
                );

                ResponseEntity<Void> response = usuarioService.alterarSenha(dadosAlterarSenha);

                assertNotNull(response);
                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

                verify(validator).validarSenhaAtual(dadosAlterarSenha.senhaAtual(), "senha123");
                verify(validator).validarConfirmacaoSenha(dadosAlterarSenha.novaSenha(), dadosAlterarSenha.confirmaSenha());
                verify(passwordEncoder).encode(dadosAlterarSenha.novaSenha());
                verify(usuarioRepository).save(usuarioMock);
            }

            @Test
            @DisplayName("Deve lançar exceção quando senha atual incorreta")
            void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
                AlterarSenhaDTO dadosAlterarSenha = new AlterarSenhaDTO(
                        "senhaErrada",
                        "novaSenha",
                        "novaSenha"
                );

                doThrow(new RuntimeException("Senha atual incorreta")).when(validator)
                        .validarSenhaAtual(dadosAlterarSenha.senhaAtual(), usuarioMock.getSenha());

                assertThrows(RuntimeException.class, () -> {
                    usuarioService.alterarSenha(dadosAlterarSenha);
                });

                verify(usuarioRepository, never()).save(any());
            }

            @Test
            @DisplayName("Deve lançar exceção quando nova senha e confirmação não conferem")
            void deveLancarExcecaoQuandoNovasSenhasNaoConferem() {
                AlterarSenhaDTO dadosAlterarSenha = new AlterarSenhaDTO(
                        "senhaAtual",
                        "novaSenha",
                        "outraSenha"
                );

                doThrow(new RuntimeException("Senhas não conferem")).when(validator)
                        .validarConfirmacaoSenha(dadosAlterarSenha.novaSenha(), dadosAlterarSenha.confirmaSenha());

                assertThrows(RuntimeException.class, () -> {
                    usuarioService.alterarSenha(dadosAlterarSenha);
                });

                verify(usuarioRepository, never()).save(any());
            }
        }
    }

    @Test
    @DisplayName("Deve testar o método getUsuarioLogado")
    void deveTestarGetUsuarioLogado() {
        reset(usuarioAutenticadoService);
        when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);

        usuarioService.buscarUsuarioLogado();

        verify(usuarioAutenticadoService, atLeastOnce()).obterIdUsuarioAutenticado();
        verify(validator).validarUsuario(usuarioId);
    }
}