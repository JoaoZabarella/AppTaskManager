package com.example.taskmanager.validator;

import com.example.taskmanager.config.exception.classes.tarefa.ListTaskEmptyException;
import com.example.taskmanager.config.exception.classes.tarefa.TarefaNotFoundException;
import com.example.taskmanager.config.exception.classes.usuario.OperacaoInvalidaException;
import com.example.taskmanager.dto.tarefa.DadosAtualizaTarefa;
import com.example.taskmanager.dto.tarefa.DadosAtualizacaoTarefaResposta;
import com.example.taskmanager.dto.tarefa.DadosListagemTarefa;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return validator.validarTarefa(tarefaId);
    }

    public Tarefa validadosObterTarefaDoUsuario(Long tarefaId, Long usuarioId) {
        return tarefaRepository.findByIdAndUsuarioIdAndAtivoTrue(tarefaId, usuarioId)
                .orElseThrow(() -> new TarefaNotFoundException("Tarefa não encontrada "));

    }

    public Status validadorObterStatus(Long status) {
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

        if (dados.statusId() != null) {
            Long statusAtualId = tarefa.getStatus() != null ? tarefa.getStatus().getId() : null;
            if (!dados.statusId().equals(statusAtualId)) {
                tarefa.setStatus(validator.validarStatus(dados.statusId()));
                camposAtualizados.add("status");
            }
        }

        if (dados.prioridadeId() != null) {
            Long prioridadeAtualId = tarefa.getPrioridade() != null ? tarefa.getPrioridade().getId() : null;
            if (!dados.prioridadeId().equals(prioridadeAtualId)) {
                tarefa.setPrioridade(validator.validarPrioridade(dados.prioridadeId()));
                camposAtualizados.add("prioridade");
            }
        }

        if(dados.categoriaId() != null){
            Long categoriaAtualId = tarefa.getCategoria() != null ? tarefa.getCategoria().getId() : null;
            if(!dados.categoriaId().equals(categoriaAtualId)){
                tarefa.setCategoria(validator.validarCategoria(dados.categoriaId()));
                camposAtualizados.add("categoria");
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

    public List<Long> verificarTarefas(List<Long> tarefasId){
        if(tarefasId == null || tarefasId.isEmpty()){
            throw new ListTaskEmptyException("A lista de tarefas não pode estar vazia");
        }
        return tarefasId.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toList());
    }

    public boolean isTarefaConcluida(Tarefa tarefa){
        return tarefa.isConcluida();
    }

    public void validarTarefaPodeSerReaberta(Tarefa tarefa){
        if(!isTarefaConcluida(tarefa)){
            throw new OperacaoInvalidaException("Esta tarefa não esta concluida e não pode ser reaberta");
        }
    }

    public void validarTarefaPodeSerEditada(Tarefa tarefa){
        if(isTarefaConcluida(tarefa)){
            throw new OperacaoInvalidaException("Esta tarefa está concluida e não pode ser editada, rebra a tarefa primeiro");
        }
    }

}