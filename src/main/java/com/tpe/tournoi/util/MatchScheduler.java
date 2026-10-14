package com.tpe.tournoi.util;

import com.tpe.tournoi.entity.TournamentMatch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Générateur de créneaux de matchs.
 *
 * <p>Une journée va de {@link #HEURE_DEBUT} à {@link #HEURE_FIN} ; chaque créneau dure
 * {@link #DUREE_CRENEAU_MINUTES} minutes. Quand la journée est pleine, le générateur
 * passe automatiquement au jour suivant. Les matchs sont répartis sur les terrains
 * du tournoi (champ {@code terrains} séparé par des virgules, sinon un terrain par défaut).</p>
 */
public final class MatchScheduler {

    public static final LocalTime HEURE_DEBUT = LocalTime.of(9, 0);
    public static final LocalTime HEURE_FIN = LocalTime.of(20, 0);
    public static final int DUREE_CRENEAU_MINUTES = 120;
    public static final String TERRAIN_DEFAUT = "Terrain 1";

    private final List<LocalTime> creneaux;
    private final List<String> terrains;

    private LocalDate jourCourant;
    private int idxCreneau;
    private int idxTerrain;

    public MatchScheduler(LocalDate dateDebut) {
        this(dateDebut, null, HEURE_DEBUT, HEURE_FIN, DUREE_CRENEAU_MINUTES);
    }

    public MatchScheduler(LocalDate dateDebut, String terrainsCsv) {
        this(dateDebut, terrainsCsv, HEURE_DEBUT, HEURE_FIN, DUREE_CRENEAU_MINUTES);
    }

    public MatchScheduler(LocalDate dateDebut, String terrainsCsv,
                          LocalTime heureDebut, LocalTime heureFin, int dureeMinutes) {
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début du planning est obligatoire");
        }
        if (dureeMinutes <= 0 || !heureFin.isAfter(heureDebut)) {
            throw new IllegalArgumentException("Créneau de planning invalide");
        }
        this.jourCourant = dateDebut;
        this.creneaux = calculerCreneaux(heureDebut, heureFin, dureeMinutes);
        this.terrains = parserTerrains(terrainsCsv);
    }

    public static List<LocalTime> calculerCreneaux(LocalTime heureDebut, LocalTime heureFin, int dureeMinutes) {
        List<LocalTime> creneaux = new ArrayList<>();
        LocalTime courant = heureDebut;
        while (!courant.plusMinutes(dureeMinutes).isAfter(heureFin)) {
            creneaux.add(courant);
            courant = courant.plusMinutes(dureeMinutes);
        }
        if (creneaux.isEmpty()) {
            creneaux.add(heureDebut);
        }
        return creneaux;
    }

    public static List<String> parserTerrains(String terrainsCsv) {
        List<String> terrains = new ArrayList<>();
        if (terrainsCsv != null) {
            for (String terrain : terrainsCsv.split(",")) {
                String value = terrain.trim();
                if (!value.isEmpty()) {
                    terrains.add(value);
                }
            }
        }
        if (terrains.isEmpty()) {
            terrains.add(TERRAIN_DEFAUT);
        }
        return terrains;
    }

    /**
     * Renvoie le prochain créneau libre à partir de la date demandée.
     * Le curseur avance toujours : deux appels ne renvoient jamais le même créneau.
     */
    public Slot next(LocalDate dateAuPlusTot) {
        if (dateAuPlusTot != null && dateAuPlusTot.isAfter(jourCourant)) {
            jourCourant = dateAuPlusTot;
            idxCreneau = 0;
            idxTerrain = 0;
        }
        Slot slot = new Slot(jourCourant, creneaux.get(idxCreneau), terrains.get(idxTerrain));
        avancer();
        return slot;
    }

    private void avancer() {
        idxTerrain++;
        if (idxTerrain >= terrains.size()) {
            idxTerrain = 0;
            idxCreneau++;
            if (idxCreneau >= creneaux.size()) {
                idxCreneau = 0;
                jourCourant = jourCourant.plusDays(1);
            }
        }
    }

    public List<String> getTerrains() {
        return List.copyOf(terrains);
    }

    public List<LocalTime> getCreneaux() {
        return List.copyOf(creneaux);
    }

    /** Un créneau de planning : date + heure + terrain. */
    public record Slot(LocalDate date, LocalTime heure, String terrain) {

        public static Slot parse(LocalDate date, String heure, String terrain) {
            try {
                return new Slot(date, LocalTime.parse(heure), terrain);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Heure invalide : " + heure);
            }
        }
    }

    /**
     * Vérifie qu'aucune équipe n'a deux matchs au même jour/créneau et
     * qu'aucun terrain n'est utilisé deux fois au même créneau.
     *
     * @throws com.tpe.tournoi.exception.ConflictException en cas de chevauchement
     */
    public static void verifierConflits(List<TournamentMatch> matches) {
        for (int i = 0; i < matches.size(); i++) {
            TournamentMatch a = matches.get(i);
            if (a.getDate() == null || a.getHeure() == null) {
                continue;
            }
            for (int j = i + 1; j < matches.size(); j++) {
                TournamentMatch b = matches.get(j);
                if (b.getDate() == null || b.getHeure() == null
                        || !a.getDate().equals(b.getDate())
                        || !a.getHeure().equals(b.getHeure())) {
                    continue;
                }
                String equipeConflit = equipeCommune(a, b);
                if (equipeConflit != null) {
                    throw new com.tpe.tournoi.exception.ConflictException(
                            "Conflit de planning : l'équipe " + equipeConflit
                                    + " a deux matchs le " + a.getDate() + " à " + a.getHeure());
                }
                if (a.getTerrain() != null && a.getTerrain().equals(b.getTerrain())) {
                    throw new com.tpe.tournoi.exception.ConflictException(
                            "Conflit de planning : le terrain '" + a.getTerrain()
                                    + "' est déjà occupé le " + a.getDate() + " à " + a.getHeure());
                }
            }
        }
    }

    private static String equipeCommune(TournamentMatch a, TournamentMatch b) {
        List<Long> equipesA = equipeIds(a);
        for (Long id : equipeIds(b)) {
            if (equipesA.contains(id)) {
                return nomEquipe(a, id);
            }
        }
        return null;
    }

    private static List<Long> equipeIds(TournamentMatch m) {
        List<Long> ids = new ArrayList<>(2);
        if (m.getEquipeDomicile() != null) {
            ids.add(m.getEquipeDomicile().getId());
        }
        if (m.getEquipeVisiteur() != null) {
            ids.add(m.getEquipeVisiteur().getId());
        }
        return ids;
    }

    private static String nomEquipe(TournamentMatch m, Long id) {
        if (m.getEquipeDomicile() != null && id.equals(m.getEquipeDomicile().getId())) {
            return "'" + m.getEquipeDomicile().getNom() + "'";
        }
        if (m.getEquipeVisiteur() != null && id.equals(m.getEquipeVisiteur().getId())) {
            return "'" + m.getEquipeVisiteur().getNom() + "'";
        }
        return "n°" + id;
    }
}
