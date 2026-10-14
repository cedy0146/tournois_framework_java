package com.tpe.tournoi.util;

import com.tpe.tournoi.entity.MatchPhase;

/**
 * Utilitaires de bracket pour les tournois à élimination directe :
 * taille du tableau, nombre de tours et libellé de phase de chaque tour.
 */
public final class BracketUtil {

    private BracketUtil() {
    }

    /** Plus petite puissance de 2 supérieure ou égale à n. */
    public static int nextPowerOf2(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    /** Nombre total de tours d'un bracket de taille {@code bracketSize}. */
    public static int totalTours(int bracketSize) {
        return (int) Math.round(Math.log(bracketSize) / Math.log(2));
    }

    /**
     * Phase (tour) correspondant à un tour donné.
     * Les tours de plus de 16 équipes sont libellés « Tour N » via {@link MatchPhase#PRELIMINAIRE}
     * et les seizièmes via {@link MatchPhase#SEIZIEME}.
     */
    public static MatchPhase determinePhase(int totalTours, int tourActuel) {
        int toursRestants = totalTours - tourActuel;
        return switch (toursRestants) {
            case 0 -> MatchPhase.FINALE;
            case 1 -> MatchPhase.DEMI;
            case 2 -> MatchPhase.QUART;
            case 3 -> MatchPhase.HUITIEME;
            case 4 -> MatchPhase.SEIZIEME;
            default -> MatchPhase.PRELIMINAIRE;
        };
    }

    /**
     * Phase du tour suivant, ou {@code null} si le tour donné est la finale.
     */
    public static MatchPhase nextPhase(int totalTours, int tourActuel) {
        if (tourActuel >= totalTours) {
            return null;
        }
        return determinePhase(totalTours, tourActuel + 1);
    }
}
