package com.example.taskmanager.config.exception;

import com.example.taskmanager.config.exception.classes.categoria.CategoriaNameDuplicateException;
import com.example.taskmanager.config.exception.classes.categoria.CategoriaNotFoundException;
import com.example.taskmanager.config.exception.classes.status.StatusNotFoundException;
import com.example.taskmanager.config.exception.classes.tarefa.TarefaNotFoundException;
import com.example.taskmanager.config.exception.classes.usuario.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErroResponse> handleEmailDuplicateException(EmailDuplicateException ex) {
        log.error("Email já cadastrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Este email já está registrado em nossa base de dados");
        details.put("solucao", "Tente outro email ou recupere a senha");

        ErroResponse erroResponse = new ErroResponse("Erro com email", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ErroResponse> handleUsernameExistException(UsernameExistException ex) {
        log.error("Nome de usuário ja em uso: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Este nome ja esta em uso atualmente");
        details.put("solucao", "Tente outro nome de usuário");

        ErroResponse erroResponse = new ErroResponse("Erro com username", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordConfirmationException.class)
    public ResponseEntity<ErroResponse> handlePasswordConfirmationException(PasswordConfirmationException ex) {
        log.error("Senha não confere com confirmação {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","A confirmação de senha não é igual a senha");
        details.put("solucao", "Tente colocar a confirmação de senha idêntica a senha");

        ErroResponse erroResponse = new ErroResponse("Erro com a confirmação de senha", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErroResponse> handleEmailNotFoundException(EmailNotFoundException ex) {
        log.error("Email não cadastrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Este não foi cadastrado ainda");
        details.put("solucao", "Tente registar esse email antes de realizar o login");

        ErroResponse erroResponse = new ErroResponse("Erro com email para login", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNameDuplicateException.class)
    public ResponseEntity<ErroResponse> handleCategoriaNameDuplicateException(CategoriaNameDuplicateException ex) {
        log.error("Já existe uma categoria com esse nome: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Já existe uma categoria com esse nome");
        details.put("solucao", "Tente outro nome para essa categoria");

        ErroResponse erroResponse = new ErroResponse("Erro com categoria", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(UsuarioNotFoundException ex) {
        log.error("Usuário não encontrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();


        details.put("motivo","Este usuário não foi encontrado");
        details.put("solucao", "Tente buscar por outro usuário");

        ErroResponse erroResponse = new ErroResponse("Usuário não encontrado", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErroResponse> handleCategoriaNaoEncontrada(CategoriaNotFoundException ex) {
        log.error("Categoria não encontrada: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Esta categoria não foi localizada");
        details.put("solucao", "Tente buscar por outra categoria");

        ErroResponse erroResponse = new ErroResponse("Categoria não encontrada", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TarefaNotFoundException.class)
    public ResponseEntity<ErroResponse> handleTarefaNaoEncontrada(TarefaNotFoundException ex) {
        log.error("Tarefa não encontrada: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Esta tarefa não foi localizada");
        details.put("solucao", "Tente buscar outra tarefa");
        ErroResponse erroResponse = new ErroResponse("Tarefa não encontrada", details);

        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro -> {
            String nomeCampo = erro.getField();
            String mensagemErro = erro.getDefaultMessage();
            erros.put(nomeCampo, mensagemErro);
        });

        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("campos_invalidos", erros);
        detalhes.put("total_erros", erros.size());

        ErroResponse erroResponse = new ErroResponse("Erro de validação", detalhes);
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErroResponse> handleStatusNaoEncontrado(StatusNotFoundException ex) {
        log.error("Status não encontrada: {}", ex.getMessage(), ex);

        Map<String, Object> details = new HashMap<>();

        details.put("motivo","Este status não foi localizado");
        details.put("solucao", "Tente buscar por outro Status");

        ErroResponse erroResponse = new ErroResponse("Status não encontrado", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Erro de integridade de dados no banco: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();

        details.put("ERRO DE INTEGRAÇÃO COM O BANCO DE DADOS", ex.getMessage());
        ErroResponse erroResponse = new ErroResponse("Erro de integridade no banco de dados", details);
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }
}
