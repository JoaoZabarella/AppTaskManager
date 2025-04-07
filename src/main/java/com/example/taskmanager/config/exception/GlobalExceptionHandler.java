package com.example.taskmanager.config.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(UsuarioNotFoundException ex) {
        log.error("Usuário não encontrado: {}", ex.getMessage(), ex);
        ErroResponse erroResponse = new ErroResponse("Usuário não encontrado", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErroResponse> handleCategoriaNaoEncontrada(CategoriaNotFoundException ex) {
        log.error("Categoria não encontrada: {}", ex.getMessage(), ex);
        ErroResponse erroResponse = new ErroResponse("Categoria não encontrada", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneralException(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        ErroResponse erroResponse = new ErroResponse("Erro interno no servidor", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Falha de validação: {}", ex.getMessage(), ex);
        ErroResponse erroResponse = new ErroResponse("Falha na validação", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        if (ex.getCause() != null && ex.getCause().getMessage().contains("unique")) {
            log.error("Erro de duplicação de e-mail: {}", ex.getMessage(), ex);
            ErroResponse erroResponse = new ErroResponse("O e-mail informado já está cadastrado", ex.getMessage());
            return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
        }

        log.error("Erro de integridade de dados no banco: {}", ex.getMessage(), ex);
        ErroResponse erroResponse = new ErroResponse("Erro de integridade no banco de dados", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }
}
