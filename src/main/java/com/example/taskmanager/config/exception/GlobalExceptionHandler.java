package com.example.taskmanager.config.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        ErroResponse erroResponse = new ErroResponse("Usuário não encontrado", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException(Exception ex) {
        ErroResponse erroResponse = new ErroResponse("Erro interno no servidor ", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> handleException(ConstraintViolationException ex) {
        ErroResponse erroResponse = new ErroResponse("Falha ma validação ", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataInegrityViolationException(DataIntegrityViolationException ex) {
        ErroResponse erroResponse = new ErroResponse("O e-mail informado já está cadastrado", ex.getMessage());
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }

}
