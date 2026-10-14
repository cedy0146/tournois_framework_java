package com.tpe.tournoi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.tpe.tournoi.controller.rest")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalid(InvalidOperationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        body.put("path", request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(
            org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Ressource introuvable", request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            org.springframework.web.bind.MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Paramètre manquant : " + ex.getParameterName(), request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Valeur invalide pour le paramètre '" + ex.getName() + "'", request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Méthode HTTP non autorisée", request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Corps de requête invalide", request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaType(
            org.springframework.web.HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Type de contenu non supporté", request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Accès refusé", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Erreur interne non gérée sur {} : {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur", request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}
