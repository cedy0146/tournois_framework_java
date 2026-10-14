package com.tpe.tournoi.util;

import com.tpe.tournoi.entity.Ranking;
import com.tpe.tournoi.entity.RankingCriterion;
import com.tpe.tournoi.entity.TournamentMatch;
import com.tpe.tournoi.exception.InvalidOperationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * Utilitaire de classement : parse et valide les critères de classement
 * configurables d'un tournoi et construit le tri correspondant,
 * confrontation directe comprise.
 */
public final class RankingUtil {

    /** Chaîne utilisée quand le tournoi ne définit aucun critère. */
    public static final String DEFAULT_CRITERES = "POINTS,DIFFERENCE_BUTS,BUTS_MARQUES";

    private RankingUtil() {
    }

    /**
     * Parse la chaîne des critères. Si elle est vide, renvoie les critères par défaut.
     *
     * @throws InvalidOperationException si un critère est inconnu ou dupliqué
     */
    public static List<RankingCriterion> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return defaultCriteres();
        }
        String[] tokens = raw.split(",");
        List<RankingCriterion> criteres = new ArrayList<>(tokens.length);
        Set<RankingCriterion> seen = new LinkedHashSet<>();
        for (String token : tokens) {
            String value = token.trim().toUpperCase();
            if (value.isEmpty()) {
                continue;
            }
            RankingCriterion critere;
            try {
                critere = RankingCriterion.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new InvalidOperationException(
                        "Critère de classement inconnu : '" + token.trim()
                                + "'. Critères acceptés : " + Arrays.toString(RankingCriterion.values()));
            }
            if (!seen.add(critere)) {
                throw new InvalidOperationException(
                        "Critère de classement en double : '" + critere + "'");
            }
            criteres.add(critere);
        }
        if (criteres.isEmpty()) {
            return defaultCriteres();
        }
        return criteres;
    }

    public static List<RankingCriterion> defaultCriteres() {
        return parseCriteresWithoutValidation(DEFAULT_CRITERES);
    }

    private static List<RankingCriterion> parseCriteresWithoutValidation(String raw) {
        List<RankingCriterion> result = new ArrayList<>();
        for (String token : raw.split(",")) {
            result.add(RankingCriterion.valueOf(token.trim()));
        }
        return result;
    }

    /** Valide la chaîne des critères sans la transformer. */
    public static void validate(String raw) {
        parse(raw);
    }

    /** Normalise une liste de critères en chaîne compacte. */
    public static String toChain(List<RankingCriterion> criteres) {
        StringBuilder sb = new StringBuilder();
        for (RankingCriterion critere : criteres) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(critere.name());
        }
        return sb.toString();
    }

    /**
     * Trie les classements selon les critères configurés.
     *
     * @param rankings        classements à trier (mutés par la méthode)
     * @param criteres        critères ordonnés
     * @param finishedMatches matchs terminés du tournoi (pour la confrontation directe)
     * @param pointsVictoire  points attribués pour une victoire
     * @param pointsNul       points attribués pour un match nul
     * @param pointsDefaite   points attribués pour une défaite
     */
    public static List<Ranking> sort(List<Ranking> rankings,
                                     List<RankingCriterion> criteres,
                                     List<TournamentMatch> finishedMatches,
                                     int pointsVictoire, int pointsNul, int pointsDefaite) {
        int idxConfrontation = criteres.indexOf(RankingCriterion.CONFRONTATION_DIRECTE);

        // Critères stricts placés avant la confrontation directe
        List<RankingCriterion> avant = idxConfrontation < 0
                ? criteres
                : new ArrayList<>(criteres.subList(0, idxConfrontation));

        ComparatorBuilder builder = new ComparatorBuilder(avant);
        List<Ranking> sorted = new ArrayList<>(rankings);
        sorted.sort(builder.build().thenComparing(r -> r.getTeam().getNom()));

        if (idxConfrontation < 0) {
            return sorted;
        }

        List<RankingCriterion> apres = idxConfrontation + 1 < criteres.size()
                ? new ArrayList<>(criteres.subList(idxConfrontation + 1, criteres.size()))
                : List.of();

        // Regroupe les équipes à égalité sur les critères précédents
        List<Ranking> result = new ArrayList<>(sorted.size());
        int start = 0;
        while (start < sorted.size()) {
            int end = start + 1;
            while (end < sorted.size() && egaliteSur(avant, sorted.get(start), sorted.get(end))) {
                end++;
            }
            List<Ranking> groupe = new ArrayList<>(sorted.subList(start, end));
            if (groupe.size() > 1) {
                Map<Long, MiniClassement> mini = miniClassement(groupe, finishedMatches,
                        pointsVictoire, pointsNul, pointsDefaite);
                ComparatorBuilder apresBuilder = new ComparatorBuilder(apres);
                groupe.sort((a, b) -> {
                    MiniClassement ma = mini.get(a.getTeam().getId());
                    MiniClassement mb = mini.get(b.getTeam().getId());
                    int cmp = Integer.compare(mb.points, ma.points);
                    if (cmp == 0) cmp = Integer.compare(mb.diff, ma.diff);
                    if (cmp == 0) cmp = Integer.compare(mb.buts, ma.buts);
                    if (cmp == 0) cmp = apresBuilder.build().compare(a, b);
                    if (cmp == 0) cmp = a.getTeam().getNom().compareTo(b.getTeam().getNom());
                    return cmp;
                });
            }
            result.addAll(groupe);
            start = end;
        }
        return result;
    }

    private static boolean egaliteSur(List<RankingCriterion> criteres, Ranking a, Ranking b) {
        for (RankingCriterion critere : criteres) {
            if (valeur(a, critere) != valeur(b, critere)) {
                return false;
            }
        }
        return true;
    }

    private static int valeur(Ranking r, RankingCriterion critere) {
        return switch (critere) {
            case POINTS -> r.getPoints();
            case DIFFERENCE_BUTS -> r.getDiffButs();
            case BUTS_MARQUES -> r.getButsMarques();
            case CONFRONTATION_DIRECTE -> 0;
        };
    }

    /**
     * Mini-classement restreint aux matchs terminés joués entre les équipes du groupe.
     */
    private static Map<Long, MiniClassement> miniClassement(List<Ranking> groupe,
                                                            List<TournamentMatch> finishedMatches,
                                                            int pointsVictoire, int pointsNul, int pointsDefaite) {
        Map<Long, MiniClassement> mini = new HashMap<>();
        for (Ranking r : groupe) {
            mini.put(r.getTeam().getId(), new MiniClassement());
        }
        for (TournamentMatch match : finishedMatches) {
            if (match.getEquipeDomicile() == null || match.getEquipeVisiteur() == null
                    || match.getScoreDomicile() == null || match.getScoreVisiteur() == null) {
                continue;
            }
            MiniClassement domicile = mini.get(match.getEquipeDomicile().getId());
            MiniClassement visiteur = mini.get(match.getEquipeVisiteur().getId());
            if (domicile == null || visiteur == null) {
                continue;
            }
            int sd = match.getScoreDomicile();
            int sv = match.getScoreVisiteur();
            domicile.buts += sd;
            domicile.diff += sd - sv;
            visiteur.buts += sv;
            visiteur.diff += sv - sd;
            if (sd > sv) {
                domicile.points += pointsVictoire;
                visiteur.points += pointsDefaite;
            } else if (sv > sd) {
                visiteur.points += pointsVictoire;
                domicile.points += pointsDefaite;
            } else {
                domicile.points += pointsNul;
                visiteur.points += pointsNul;
            }
        }
        return mini;
    }

    private static final class MiniClassement {
        private int points;
        private int diff;
        private int buts;
    }

    /** Construit un comparateur décroissant à partir d'une liste de critères. */
    private static final class ComparatorBuilder {

        private final List<RankingCriterion> criteres;

        private ComparatorBuilder(List<RankingCriterion> criteres) {
            this.criteres = criteres;
        }

        private java.util.Comparator<Ranking> build() {
            java.util.Comparator<Ranking> comparator = (a, b) -> 0;
            for (int i = criteres.size() - 1; i >= 0; i--) {
                ToIntFunction<Ranking> extracteur = extracteur(criteres.get(i));
                comparator = decroissant(extracteur).thenComparing(comparator);
            }
            return comparator;
        }

        private static ToIntFunction<Ranking> extracteur(RankingCriterion critere) {
            return switch (critere) {
                case POINTS -> Ranking::getPoints;
                case DIFFERENCE_BUTS -> Ranking::getDiffButs;
                case BUTS_MARQUES -> Ranking::getButsMarques;
                case CONFRONTATION_DIRECTE -> r -> 0;
            };
        }

        private static java.util.Comparator<Ranking> decroissant(ToIntFunction<Ranking> extracteur) {
            return (a, b) -> Integer.compare(extracteur.applyAsInt(b), extracteur.applyAsInt(a));
        }
    }
}
