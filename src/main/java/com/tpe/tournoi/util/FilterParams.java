package com.tpe.tournoi.util;

import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

/**
 * Aide à la lecture des paramètres de listes paginées (pagination, tri, filtres).
 * Toutes les valeurs sont invalidées proprement si elles sont absentes ou erronées.
 */
public final class FilterParams {

    private FilterParams() {
    }

    public static int page(Integer page) {
        return (page == null || page < 0) ? 0 : page;
    }

    public static int size(Integer size) {
        return size(size, 10);
    }

    public static int size(Integer size, int defaut) {
        if (size == null || size < 1) {
            return defaut;
        }
        return Math.min(size, 100);
    }

    /** Champ de tri refusé s'il n'est pas dans la liste blanche. */
    public static String champTri(String tri, String defaut, Set<String> autorises) {
        if (tri == null || tri.isBlank() || !autorises.contains(tri)) {
            return defaut;
        }
        return tri;
    }

    public static Sort.Direction sensTri(String ordre) {
        return "desc".equalsIgnoreCase(ordre) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    public static Sort tri(String champ, String ordre, String champDefaut, String ordreDefaut) {
        String champFinal = (champ == null || champ.isBlank()) ? champDefaut : champ;
        Sort.Direction sens = ordre == null || ordre.isBlank() ? null : sensTri(ordre);
        if (sens == null) {
            sens = sensTri(ordreDefaut);
        }
        return Sort.by(sens, champFinal);
    }

    /** Transforme une chaîne en valeur d'énumération, ou {@code null} si absente/inconnue. */
    public static <E extends Enum<E>> E enumOrNull(Class<E> type, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, valeur.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Transforme une chaîne en {@link Long}, ou {@code null} si absente/invalide. */
    public static Long longOrNull(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valeur.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Transforme une chaîne en {@link LocalDate}, ou {@code null} si absente/invalide. */
    public static LocalDate dateOrNull(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valeur.trim());
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    /** Chaîne réutilisable dans les liens de pagination pour conserver les filtres courants. */
    public static String texte(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }
}
