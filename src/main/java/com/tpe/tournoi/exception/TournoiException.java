package com.tpe.tournoi.exception;

/**
 * Exception racine de l'application "Mahery Foot".
 * Toutes les exceptions métier en héritent afin d'être traitées
 * de façon homogène par le gestionnaire global d'erreurs.
 */
public abstract class TournoiException extends RuntimeException {

    protected TournoiException(String message) {
        super(message);
    }

    protected TournoiException(String message, Throwable cause) {
        super(message, cause);
    }
}
