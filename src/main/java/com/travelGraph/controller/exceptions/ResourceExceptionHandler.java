package com.travelGraph.controller.exceptions;

import com.auth0.jwt.exceptions.JWTCreationException;

import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import com.travelGraph.services.exceptions.UnprocessableEntityException;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add("Resource not found");

        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), errors, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> databaseException(DatabaseException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add("Database error");

        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), errors, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(
        MethodArgumentNotValidException e,
        HttpServletRequest request
    ) {
        List<String> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.add("O campo '" + field + "' " + message);
        });

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            errors,
            "Erro de validação nos campos",
            request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<StandardError> handleUnprocessableEntityExceptions(UnprocessableEntityException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        StandardError err = new StandardError(Instant.now(), status.value(), errors, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> handleConstraintViolation(
        ConstraintViolationException e,
        HttpServletRequest request
    ) {
        List<String> errors = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> {
            String message = violation.getMessage();
            errors.add(message);
        });

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            errors,
            "Erro de validação na entidade",
            request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<StandardError> handleJWTCreationException(JWTCreationException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(Instant.now(), status.value(), errors, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
