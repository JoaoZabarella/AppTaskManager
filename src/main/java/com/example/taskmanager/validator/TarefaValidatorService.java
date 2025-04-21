package com.example.taskmanager.validator;

import com.example.taskmanager.config.exception.classes.tarefa.TarefaNotFoundException;
import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.dto.tarefa.DadosAtualizacaoTarefaResposta;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.PrioridadeRepository;
import com.example.taskmanager.repository.StatusRepository;
import com.example.taskmanager.repository.TarefaRepository;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TarefaValidatorService {

    private final TarefaRepository tarefaRepository;
    private final EntidadeValidator validator;

    public TarefaValidatorService(TarefaRepository tarefaRepository,
                                  EntidadeValidator validator) {
        this.tarefaRepository = tarefaRepository;
        this.validator = validator;
    }

    public Tarefa validaEObterTarefa(Long tarefaId, Long usuarioId) {
        return validator.validarTarefa(tarefaId, usuarioId);
    }

    public Tarefa validadosObterTarefaDoUsuario(Long tarefaId, Long usuarioId) {
        return tarefaRepository.findByIdAndUsuarioIdAndAtivoTrue(tarefaId, usuarioId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa não encontrada "));

    }

    public Status validadorObterStatus(String status) {
        return validator.validarStatus(status);
    }

    public DadosAtualizacaoTarefaResposta atualizacaoTarefaComDados(Tarefa tarefa, DadosAtualizaTarefa dados){
        List<String> camposAtualizados = atualizarCampos(tarefa, dados);

        List<String> camposFormatados = camposAtualizados.stream()
                .map(String::toUpperCase)
                .toList();

        String mensagem = camposFormatados.isEmpty()
                ? "Nenhum campo foi atualizado"
                : "Tarefa atualizada com sucesso! Campos atualizados: " + String.join(", ", camposFormatados);

        return new DadosAtualizacaoTarefaResposta(
                new DadosListagemTarefa(tarefa),
                camposFormatados,
                mensagem
        );
    }


    public List<String> atualizarCampos(Tarefa tarefa, DadosAtualizaTarefa dados) {
        validarTarefaNaoConcluida(tarefa);

        List<String> camposAtualizados = new ArrayList<>();

        if (dados.titulo() != null && !dados.titulo().equals(tarefa.getTitulo())) {
            tarefa.setTitulo(dados.titulo());
            camposAtualizados.add("título");
        }

        if (dados.descricao() != null && !dados.descricao().equals(tarefa.getDescricao())) {
            tarefa.setDescricao(dados.descricao());
            camposAtualizados.add("descrição");
        }

        if (dados.statusTexto() != null) {
            String statusAtual = tarefa.getStatus() != null ? tarefa.getStatus().getTexto() : null;
            if (!dados.statusTexto().equals(statusAtual)) {
                tarefa.setStatus(validator.validarStatus(dados.statusTexto()));
                camposAtualizados.add("status");
            }
        }

        if (dados.prioridadeTexto() != null) {
            String prioridadeAtual = tarefa.getPrioridade() != null ? tarefa.getPrioridade().getTexto() : null;
            if (!dados.prioridadeTexto().equals(prioridadeAtual)) {
                tarefa.setPrioridade(validator.validarPrioridade(dados.prioridadeTexto()));
                camposAtualizados.add("prioridade");
            }
        }

        if (dados.prazo() != null && !dados.prazo().equals(tarefa.getPrazo())) {
            tarefa.setPrazo(dados.prazo());
            camposAtualizados.add("prazo");
        }

        return camposAtualizados;
    }
    public void validarTarefaNaoConcluida(Tarefa tarefa) {
        if (tarefa.isConcluida()) {
            throw new RuntimeException("Esta tarefa já está concluída");
        }
    }
}