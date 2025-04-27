package com.example.taskmanager.config.exception;

import com.example.taskmanager.config.exception.classes.auth.CredenciaisInvalidasException;
import com.example.taskmanager.config.exception.classes.auth.UsuarioNaoAutenticadoException;
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

import javax.management.ListenerNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErroResponse> createErrorResponse(String message, Map<String, Object> details, HttpStatus status) {
        ErroResponse erroResponse = new ErroResponse(message, details);
        return new ResponseEntity<>(erroResponse, status);
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErroResponse> handleEmailDuplicateException(EmailDuplicateException ex) {
        log.error("Email já cadastrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Este email já está registrado em nossa base de dados");
        details.put("solucao", "Tente outro email ou recupere a senha");
        return createErrorResponse("Erro com email", details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ErroResponse> handleUsernameExistException(UsernameExistException ex) {
        log.error("Nome de usuário já em uso: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Este nome já está em uso atualmente");
        details.put("solucao", "Tente outro nome de usuário");
        return createErrorResponse("Erro com username", details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordConfirmationException.class)
    public ResponseEntity<ErroResponse> handlePasswordConfirmationException(PasswordConfirmationException ex) {
        log.error("Senha não confere com confirmação: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "A confirmação de senha não é igual a senha");
        details.put("solucao", "Tente colocar a confirmação de senha idêntica à senha");
        return createErrorResponse("Erro com a confirmação de senha", details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErroResponse> handleEmailNotFoundException(EmailNotFoundException ex) {
        log.error("Email não cadastrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Este email não foi cadastrado ainda");
        details.put("solucao", "Tente registrar esse email antes de realizar o login");
        return createErrorResponse("Erro com email para login", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNameDuplicateException.class)
    public ResponseEntity<ErroResponse> handleCategoriaNameDuplicateException(CategoriaNameDuplicateException ex) {
        log.error("Já existe uma categoria com esse nome: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Já existe uma categoria com esse nome");
        details.put("solucao", "Tente outro nome para essa categoria");
        return createErrorResponse("Erro com categoria", details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(UsuarioNotFoundException ex) {
        log.error("Usuário não encontrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Este usuário não foi encontrado");
        details.put("solucao", "Tente buscar por outro usuário");
        return createErrorResponse("Usuário não encontrado", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErroResponse> handleCategoriaNaoEncontrada(CategoriaNotFoundException ex) {
        log.error("Categoria não encontrada: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Esta categoria não foi localizada");
        details.put("solucao", "Tente buscar por outra categoria");
        return createErrorResponse("Categoria não encontrada", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TarefaNotFoundException.class)
    public ResponseEntity<ErroResponse> handleTarefaNaoEncontrada(TarefaNotFoundException ex) {
        log.error("Tarefa não encontrada: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Esta tarefa não foi localizada");
        details.put("solucao", "Tente buscar outra tarefa");
        return createErrorResponse("Tarefa não encontrada", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ListenerNotFoundException.class)
    public ResponseEntity<ErroResponse> handleListenerNotFoundException(ListenerNotFoundException ex) {
        log.error("A lista de tarefas está vazia : {} ", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "A lista de tarefas está vazia");
        details.put("solucao", "Tente adicionar tarefas na lista");
        return createErrorResponse("Lista de tarefas vazias", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro -> {
            String nomeCampo = erro.getField();
            String mensagemErro = erro.getDefaultMessage();
            erros.put(nomeCampo, mensagemErro);
        });

        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("campos_invalidos", erros);
        detalhes.put("total_erros", erros.size());

        return createErrorResponse("Erro de validação", detalhes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErroResponse> handleStatusNaoEncontrado(StatusNotFoundException ex) {
        log.error("Status não encontrado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Este status não foi localizado");
        details.put("solucao", "Tente buscar por outro Status");
        return createErrorResponse("Status não encontrado", details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Erro de integridade de dados no banco: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Erro interno do sistema");
        details.put("solucao", "Tente novamente mais tarde ou entre em contato com o suporte.");
        return createErrorResponse("Erro de integridade no banco de dados", details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoAutenticadoException(UsuarioNaoAutenticadoException ex) {
        log.error("Usuário não está autenticado: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Usuário não logado");
        details.put("solucao", "Tente logar antes");
        return createErrorResponse("Erro no usuário autenticado", details, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PasswordNotActualException.class)
    public ResponseEntity<ErroResponse> handlePasswordNotActualException(PasswordNotActualException ex) {
        log.error("Senha não confere: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "Senha não confere");
        details.put("solucao", "Tente novamente com a senha correta");
        return createErrorResponse("Erro com a senha atual", details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> handleCredenciaisInvalidasException(CredenciaisInvalidasException ex) {
        log.error("Credenciais inválidas: {}", ex.getMessage(), ex);
        Map<String, Object> details = new HashMap<>();
        details.put("motivo", "O email ou a senha estão incorretos");
        details.put("solucao", "Verifique suas credenciais e tente novamente");
        return createErrorResponse("Erro de autenticação", details, HttpStatus.UNAUTHORIZED);
    }

}