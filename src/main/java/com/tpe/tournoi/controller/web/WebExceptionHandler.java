package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.exception.BusinessException;
import com.tpe.tournoi.exception.ConflictException;
import com.tpe.tournoi.exception.InvalidOperationException;
import com.tpe.tournoi.exception.ResourceNotFoundException;
import com.tpe.tournoi.exception.TournoiException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Gestion des erreurs côté interface web : les pages d'erreur HTML
 * remplacent les réponses JSON produites pour l'API REST.
 */
@ControllerAdvice(basePackages = "com.tpe.tournoi.controller.web")
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String introuvable(ResourceNotFoundException ex, HttpServletResponse response, Model model) {
        return erreur(response, model, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public String conflit(ConflictException ex, HttpServletResponse response, Model model) {
        return erreur(response, model, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({InvalidOperationException.class, BusinessException.class,
            MethodArgumentNotValidException.class})
    public String requeteInvalide(Exception ex, HttpServletResponse response, Model model) {
        return erreur(response, model, HttpStatus.BAD_REQUEST, message(ex));
    }

    @ExceptionHandler(TournoiException.class)
    public String metier(TournoiException ex, HttpServletResponse response, Model model) {
        HttpStatus status = ex instanceof ResourceNotFoundException ? HttpStatus.NOT_FOUND
                : ex instanceof ConflictException ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return erreur(response, model, status, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public String interne(Exception ex, HttpServletResponse response, Model model) {
        log.error("Erreur interne sur la page web", ex);
        return erreur(response, model, HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue. Merci de réessayer.");
    }

    private String erreur(HttpServletResponse response, Model model, HttpStatus status, String message) {
        response.setStatus(status.value());
        model.addAttribute("status", status.value());
        model.addAttribute("message", message);
        return "error";
    }

    private String message(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validation) {
            return validation.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(f -> f.getField() + " : " + f.getDefaultMessage())
                    .orElse("Données invalides");
        }
        return ex.getMessage();
    }
}
