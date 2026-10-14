package com.tpe.tournoi.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchResponse {
    private Long id;
    private Long tournamentId;
    private String tournamentNom;
    private Long equipeDomicileId;
    private String equipeDomicileNom;
    private Long equipeVisiteurId;
    private String equipeVisiteurNom;
    private LocalDate date;
    private LocalTime heure;
    private String terrain;
    private String phase;
    private String statut;
    private Integer scoreDomicile;
    private Integer scoreVisiteur;
    private Long vainqueurId;
    private String vainqueurNom;
    private int tour;
    private Integer bracketPosition;

    public MatchResponse() {
    }

    public MatchResponse(Long id, Long tournamentId, String tournamentNom, Long equipeDomicileId, String equipeDomicileNom, Long equipeVisiteurId, String equipeVisiteurNom, LocalDate date, LocalTime heure, String terrain, String phase, String statut, Integer scoreDomicile, Integer scoreVisiteur, Long vainqueurId, String vainqueurNom, int tour, Integer bracketPosition) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.tournamentNom = tournamentNom;
        this.equipeDomicileId = equipeDomicileId;
        this.equipeDomicileNom = equipeDomicileNom;
        this.equipeVisiteurId = equipeVisiteurId;
        this.equipeVisiteurNom = equipeVisiteurNom;
        this.date = date;
        this.heure = heure;
        this.terrain = terrain;
        this.phase = phase;
        this.statut = statut;
        this.scoreDomicile = scoreDomicile;
        this.scoreVisiteur = scoreVisiteur;
        this.vainqueurId = vainqueurId;
        this.vainqueurNom = vainqueurNom;
        this.tour = tour;
        this.bracketPosition = bracketPosition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentNom() {
        return tournamentNom;
    }

    public void setTournamentNom(String tournamentNom) {
        this.tournamentNom = tournamentNom;
    }

    public Long getEquipeDomicileId() {
        return equipeDomicileId;
    }

    public void setEquipeDomicileId(Long equipeDomicileId) {
        this.equipeDomicileId = equipeDomicileId;
    }

    public String getEquipeDomicileNom() {
        return equipeDomicileNom;
    }

    public void setEquipeDomicileNom(String equipeDomicileNom) {
        this.equipeDomicileNom = equipeDomicileNom;
    }

    public Long getEquipeVisiteurId() {
        return equipeVisiteurId;
    }

    public void setEquipeVisiteurId(Long equipeVisiteurId) {
        this.equipeVisiteurId = equipeVisiteurId;
    }

    public String getEquipeVisiteurNom() {
        return equipeVisiteurNom;
    }

    public void setEquipeVisiteurNom(String equipeVisiteurNom) {
        this.equipeVisiteurNom = equipeVisiteurNom;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getScoreDomicile() {
        return scoreDomicile;
    }

    public void setScoreDomicile(Integer scoreDomicile) {
        this.scoreDomicile = scoreDomicile;
    }

    public Integer getScoreVisiteur() {
        return scoreVisiteur;
    }

    public void setScoreVisiteur(Integer scoreVisiteur) {
        this.scoreVisiteur = scoreVisiteur;
    }

    public Long getVainqueurId() {
        return vainqueurId;
    }

    public void setVainqueurId(Long vainqueurId) {
        this.vainqueurId = vainqueurId;
    }

    public String getVainqueurNom() {
        return vainqueurNom;
    }

    public void setVainqueurNom(String vainqueurNom) {
        this.vainqueurNom = vainqueurNom;
    }

    public int getTour() {
        return tour;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }

    public Integer getBracketPosition() {
        return bracketPosition;
    }

    public void setBracketPosition(Integer bracketPosition) {
        this.bracketPosition = bracketPosition;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long tournamentId;
        private String tournamentNom;
        private Long equipeDomicileId;
        private String equipeDomicileNom;
        private Long equipeVisiteurId;
        private String equipeVisiteurNom;
        private LocalDate date;
        private LocalTime heure;
        private String terrain;
        private String phase;
        private String statut;
        private Integer scoreDomicile;
        private Integer scoreVisiteur;
        private Long vainqueurId;
        private String vainqueurNom;
        private int tour;
        private Integer bracketPosition;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder tournamentId(Long tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public Builder tournamentNom(String tournamentNom) {
            this.tournamentNom = tournamentNom;
            return this;
        }

        public Builder equipeDomicileId(Long equipeDomicileId) {
            this.equipeDomicileId = equipeDomicileId;
            return this;
        }

        public Builder equipeDomicileNom(String equipeDomicileNom) {
            this.equipeDomicileNom = equipeDomicileNom;
            return this;
        }

        public Builder equipeVisiteurId(Long equipeVisiteurId) {
            this.equipeVisiteurId = equipeVisiteurId;
            return this;
        }

        public Builder equipeVisiteurNom(String equipeVisiteurNom) {
            this.equipeVisiteurNom = equipeVisiteurNom;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder heure(LocalTime heure) {
            this.heure = heure;
            return this;
        }

        public Builder terrain(String terrain) {
            this.terrain = terrain;
            return this;
        }

        public Builder phase(String phase) {
            this.phase = phase;
            return this;
        }

        public Builder statut(String statut) {
            this.statut = statut;
            return this;
        }

        public Builder scoreDomicile(Integer scoreDomicile) {
            this.scoreDomicile = scoreDomicile;
            return this;
        }

        public Builder scoreVisiteur(Integer scoreVisiteur) {
            this.scoreVisiteur = scoreVisiteur;
            return this;
        }

        public Builder vainqueurId(Long vainqueurId) {
            this.vainqueurId = vainqueurId;
            return this;
        }

        public Builder vainqueurNom(String vainqueurNom) {
            this.vainqueurNom = vainqueurNom;
            return this;
        }

        public Builder tour(int tour) {
            this.tour = tour;
            return this;
        }

        public Builder bracketPosition(Integer bracketPosition) {
            this.bracketPosition = bracketPosition;
            return this;
        }

        public MatchResponse build() {
            return new MatchResponse(id, tournamentId, tournamentNom, equipeDomicileId, equipeDomicileNom, equipeVisiteurId, equipeVisiteurNom, date, heure, terrain, phase, statut, scoreDomicile, scoreVisiteur, vainqueurId, vainqueurNom, tour, bracketPosition);
        }
    }
}
