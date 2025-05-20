package com.example.taskmanager.service;

import com.example.taskmanager.dto.tarefa.*;
import com.example.taskmanager.mapper.TarefaMapper;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.TarefaRepository;
import com.example.taskmanager.validator.EntidadeValidator;
import com.example.taskmanager.validator.TarefaValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private TarefaMapper mapper;

    @Mock
    private TarefaValidatorService validatorService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private EntidadeValidator validator;

    @InjectMocks
    private TarefaService tarefaService;

    private Usuario usuario;
    private Tarefa tarefa;
    private DadosCriarTarefa dadosCriarTarefa;
    private DadosListagemTarefa dadosListagemTarefa;
    private Status statusEmAndamento;
    private Status statusConcluido;
    private Prioridade prioridade;
    private Categoria categoria;
    private UriComponentsBuilder uriBuilder;
    private Long usuarioId = 1L;
    private Long tarefaId = 1L;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");

        statusEmAndamento = new Status();
        statusEmAndamento.setId(2L);
        statusEmAndamento.setTexto("Em andamento");

        statusConcluido = new Status();
        statusConcluido.setId(3L);
        statusConcluido.setTexto("Concluído");

        prioridade = new Prioridade();
        prioridade.setId(1L);
        prioridade.setTexto("Alta");

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Trabalho");

        tarefa = new Tarefa();
        tarefa.setId(tarefaId);
        tarefa.setTitulo("Tarefa teste");
        tarefa.setDescricao("Descrição da tarefa teste");
        tarefa.setStatus(statusEmAndamento);
        tarefa.setPrioridade(prioridade);
        tarefa.setCategoria(categoria);
        tarefa.setUsuario(usuario);
        tarefa.setAtivo(true);
        tarefa.setDataCriacao(OffsetDateTime.from(OffsetDateTime.now()));
        tarefa.setPrazo(OffsetDateTime.now().plusDays(1));

        dadosCriarTarefa = new DadosCriarTarefa(
                "Tarefa teste",
                "Descrição da tarefa teste",
                2L,
                1L,
                OffsetDateTime.now().plusDays(1),
                1L
        );

        dadosListagemTarefa = new DadosListagemTarefa(tarefa);

        uriBuilder = UriComponentsBuilder.fromPath("/");

        when(usuarioAutenticadoService.obterIdUsuarioAutenticado()).thenReturn(usuarioId);
    }

    @Test
    @DisplayName("Deve criar uma tarefa com sucesso")
    void criarTarefa() {
        when(mapper.prepararTarefa(any(DadosCriarTarefa.class))).thenReturn(tarefa);
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        URI uri = URI.create("/tarefas/1");
        when(mapper.construirUri(any(Tarefa.class), any(UriComponentsBuilder.class))).thenReturn(uri);

        ResponseEntity<DadosListagemTarefa> response = tarefaService.criarTarefa(dadosCriarTarefa, 1L, uriBuilder);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(uri, response.getHeaders().getLocation());

        verify(mapper).prepararTarefa(dadosCriarTarefa);
        verify(tarefaRepository).save(tarefa);
        verify(mapper).construirUri(tarefa, uriBuilder);
    }

    @Test
    @DisplayName("Deve atualizar uma tarefa com sucesso")
    void atualizarTarefa() {
        DadosAtualizaTarefa dadosAtualizaTarefa = new DadosAtualizaTarefa(
                tarefaId,
                "Tarefa atualizada",
                "Descrição atualizada",
                2L,
                1L,
                OffsetDateTime.now().plusDays(2),
                1L
        );

        DadosAtualizacaoTarefaResposta resposta = new DadosAtualizacaoTarefaResposta(
                dadosListagemTarefa,
                Arrays.asList("titulo", "descricao"),
                "Tarefa atualizada com sucesso"
        );

        when(validatorService.validadosObterTarefaDoUsuario(tarefaId, usuarioId)).thenReturn(tarefa);
        when(validatorService.atualizacaoTarefaComDados(tarefa, dadosAtualizaTarefa)).thenReturn(resposta);

        ResponseEntity<DadosAtualizacaoTarefaResposta> response = tarefaService.atualizarTarefa(tarefaId, dadosAtualizaTarefa);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(resposta, response.getBody());

        verify(validatorService).validadosObterTarefaDoUsuario(tarefaId, usuarioId);
        verify(validatorService).validarTarefaPodeSerEditada(tarefa);
        verify(validatorService).atualizacaoTarefaComDados(tarefa, dadosAtualizaTarefa);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas ativas do usuário")
    void listarTarefasAtivas() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        Page<Tarefa> tarefasPage = new PageImpl<>(tarefas, pageable, tarefas.size());

        when(tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)).thenReturn(tarefasPage);

        ResponseEntity<PaginadoTarefaDTO> response = tarefaService.listarTarefasAtivas(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().tarefas().size());

        verify(tarefaRepository).findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
    }

    @Test
    @DisplayName("Deve filtrar tarefas por status, prioridade e categoria")
    void filtrarTarefasPorStatusPrioridadeCategoria() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltrosStatusPrioridadeCategoriaDTO filtro = new FiltrosStatusPrioridadeCategoriaDTO(2L, 1L, 1L);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        Page<Tarefa> tarefasPage = new PageImpl<>(tarefas, pageable, tarefas.size());

        when(mapper.filtroVazio(filtro)).thenReturn(false);
        when(tarefaRepository.buscarComFiltros(usuarioId, 2L, 1L, 1L, pageable)).thenReturn(tarefasPage);

        ResponseEntity<PaginadoTarefaDTO> response = tarefaService.filtrarTarefasPorStatusPrioridadeCategoria(filtro, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().tarefas().size());

        verify(mapper).filtroVazio(filtro);
        verify(tarefaRepository).buscarComFiltros(usuarioId, 2L, 1L, 1L, pageable);
    }

    @Test
    @DisplayName("Deve buscar tarefas por palavra-chave")
    void buscarTarefasPorPalavraChave() {
        Pageable pageable = PageRequest.of(0, 10);
        String palavraChave = "teste";
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        Page<Tarefa> tarefasPage = new PageImpl<>(tarefas, pageable, tarefas.size());

        when(tarefaRepository.buscarPorPalavraChave(usuarioId, palavraChave, pageable)).thenReturn(tarefasPage);

        ResponseEntity<PaginadoTarefaDTO> response = tarefaService.buscarTarefasPorPalavraChave(palavraChave, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().tarefas().size());

        verify(tarefaRepository).buscarPorPalavraChave(usuarioId, palavraChave, pageable);
    }

    @Test
    @DisplayName("Deve deletar uma tarefa")
    void deletarTarefa() {
        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);

        ResponseEntity<Void> response = tarefaService.deletarTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(tarefaRepository).delete(tarefa);
    }

    @Test
    @DisplayName("Deve arquivar uma tarefa")
    void arquivarTarefa() {
        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);

        ResponseEntity<Void> response = tarefaService.arquivarTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        assertFalse(tarefa.getAtivo());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    @DisplayName("Deve arquivar múltiplas tarefas")
    void arquivarMultiplasTarefas() {
        List<Long> tarefasIds = Collections.singletonList(tarefaId);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);

        when(tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId)).thenReturn(tarefas);

        tarefaService.arquivarMultiplasTarefas(tarefasIds);

        assertFalse(tarefa.getAtivo());

        verify(validatorService).verificarTarefas(tarefasIds);
        verify(tarefaRepository).findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId);
        verify(tarefaRepository).saveAll(tarefas);
    }

    @Test
    @DisplayName("Deve excluir múltiplas tarefas")
    void excluirMultiplasTarefas() {
        List<Long> tarefasIds = Collections.singletonList(tarefaId);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);

        when(tarefaRepository.findAllByIdsAndUsuarioId(tarefasIds, usuarioId)).thenReturn(tarefas);

        tarefaService.excluirMultiplasTarefas(tarefasIds);

        verify(validatorService).verificarTarefas(tarefasIds);
        verify(tarefaRepository).findAllByIdsAndUsuarioId(tarefasIds, usuarioId);
        verify(tarefaRepository).deleteAll(tarefas);
    }

    @Test
    @DisplayName("Deve concluir uma tarefa")
    void concluirTarefa() {
        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);
        when(validatorService.isTarefaConcluida(tarefa)).thenReturn(false);
        when(validatorService.validadorObterStatus(3L)).thenReturn(statusConcluido);

        ResponseEntity<DadosListagemTarefa> response = tarefaService.concluirTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(statusConcluido, tarefa.getStatus());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(validatorService).isTarefaConcluida(tarefa);
        verify(validator).validarTarefa(tarefaId);
        verify(validatorService).validadorObterStatus(3L);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    @DisplayName("Não deve concluir tarefa já concluída")
    void naoDeveConcluirTarefaJaConcluida() {
        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);
        when(validatorService.isTarefaConcluida(tarefa)).thenReturn(true);

        ResponseEntity<DadosListagemTarefa> response = tarefaService.concluirTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(validatorService).isTarefaConcluida(tarefa);
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve concluir múltiplas tarefas")
    void concluirMultiplasTarefas() {
        List<Long> tarefasIds = Collections.singletonList(tarefaId);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);

        when(tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId)).thenReturn(tarefas);
        when(validatorService.validadorObterStatus(3L)).thenReturn(statusConcluido);

        ResponseEntity<List<DadosListagemTarefa>> response = tarefaService.concluirMultiplasTarefas(tarefasIds);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(statusConcluido, tarefa.getStatus());

        verify(validatorService).verificarTarefas(tarefasIds);
        verify(tarefaRepository).findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId);
        verify(validatorService).validadorObterStatus(3L);
        verify(tarefaRepository).saveAll(anyList());
    }
    @Test
    @DisplayName("Deve reabrir uma tarefa")
    void reabrirTarefa() {
        tarefa.setStatus(statusConcluido);
        tarefa.concluir();

        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);
        when(validatorService.isTarefaConcluida(tarefa)).thenReturn(true);
        when(validatorService.validadorObterStatus(2L)).thenReturn(statusEmAndamento);
        when(mapper.prepararTarefaParaReabertura(tarefa, statusEmAndamento)).thenReturn(tarefa);

        ResponseEntity<DadosListagemTarefa> response = tarefaService.reabrirTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(validatorService).isTarefaConcluida(tarefa);
        verify(validator).validarTarefa(tarefaId);
        verify(validatorService).validadorObterStatus(2L);
        verify(mapper).prepararTarefaParaReabertura(tarefa, statusEmAndamento);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    @DisplayName("Não deve reabrir tarefa já aberta")
    void naoDeveReabrirTarefaJaAberta() {
        when(validatorService.validaEObterTarefa(tarefaId, usuarioId)).thenReturn(tarefa);
        when(validatorService.isTarefaConcluida(tarefa)).thenReturn(false);

        ResponseEntity<DadosListagemTarefa> response = tarefaService.reabrirTarefa(tarefaId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        verify(validatorService).validaEObterTarefa(tarefaId, usuarioId);
        verify(validatorService).isTarefaConcluida(tarefa);
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve reabrir múltiplas tarefas")
    void reabrirMultiplasTarefas() {
        List<Long> tarefasIds = Collections.singletonList(tarefaId);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        List<Tarefa> tarefasReabertas = Collections.singletonList(tarefa);
        List<DadosListagemTarefa> resultado = Collections.singletonList(dadosListagemTarefa);

        when(tarefaRepository.findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId)).thenReturn(tarefas);
        when(validatorService.validadorObterStatus(2L)).thenReturn(statusEmAndamento);
        when(mapper.filtrarTarefasParaReabrir(tarefas, statusEmAndamento, validatorService)).thenReturn(tarefasReabertas);
        when(mapper.converterParaDTOs(tarefas)).thenReturn(resultado);

        ResponseEntity<List<DadosListagemTarefa>> response = tarefaService.reabrirMultiplasTarefas(tarefasIds);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());

        verify(validatorService).verificarTarefas(tarefasIds);
        verify(tarefaRepository).findAllByIdsAndUsuarioIdAndAtivoTrue(tarefasIds, usuarioId);
        verify(validatorService).validadorObterStatus(2L);
        verify(mapper).filtrarTarefasParaReabrir(tarefas, statusEmAndamento, validatorService);
        verify(tarefaRepository).saveAll(tarefasReabertas);
        verify(mapper).converterParaDTOs(tarefas);
    }

    @Test
    @DisplayName("Deve lidar com pesquisa com palavra-chave vazia")
    void lidarComPalavraChaveVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        Page<Tarefa> tarefasPage = new PageImpl<>(tarefas, pageable, tarefas.size());

        when(tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)).thenReturn(tarefasPage);

        ResponseEntity<PaginadoTarefaDTO> response = tarefaService.buscarTarefasPorPalavraChave("", pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        verify(tarefaRepository).findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
        verify(tarefaRepository, never()).buscarPorPalavraChave(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lidar com filtro vazio")
    void lidarComFiltroVazio() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltrosStatusPrioridadeCategoriaDTO filtro = new FiltrosStatusPrioridadeCategoriaDTO(null, null, null);
        List<Tarefa> tarefas = Collections.singletonList(tarefa);
        Page<Tarefa> tarefasPage = new PageImpl<>(tarefas, pageable, tarefas.size());

        when(mapper.filtroVazio(filtro)).thenReturn(true);
        when(tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable)).thenReturn(tarefasPage);

        ResponseEntity<PaginadoTarefaDTO> response = tarefaService.filtrarTarefasPorStatusPrioridadeCategoria(filtro, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        verify(mapper).filtroVazio(filtro);
        verify(tarefaRepository).findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
        verify(tarefaRepository, never()).buscarComFiltros(anyLong(), any(), any(), any(), any(Pageable.class));
    }
}