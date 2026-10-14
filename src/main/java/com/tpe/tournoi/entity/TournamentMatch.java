package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tournament_matches")
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_domicile_id")
    private Team equipeDomicile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_visiteur_id")
    private Team equipeVisiteur;

    private LocalDate date;

    private LocalTime heure;

    private String terrain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchPhase phase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus statut;

    private Integer scoreDomicile;

    private Integer scoreVisiteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vainqueur_id")
    private Team vainqueur;

    private int tour;

    private Integer bracketPosition;

    public TournamentMatch() {
    }

    public TournamentMatch(Long id, Tournament tournament, Team equipeDomicile, Team equipeVisiteur, LocalDate date, LocalTime heure, String terrain, MatchPhase phase, MatchStatus statut, Integer scoreDomicile, Integer scoreVisiteur, Team vainqueur, int tour, Integer bracketPosition) {
        this.id = id;
        this.tournament = tournament;
        this.equipeDomicile = equipeDomicile;
        this.equipeVisiteur = equipeVisiteur;
        this.date = date;
        this.heure = heure;
        this.terrain = terrain;
        this.phase = phase;
        this.statut = statut;
        this.scoreDomicile = scoreDomicile;
        this.scoreVisiteur = scoreVisiteur;
        this.vainqueur = vainqueur;
        this.tour = tour;
        this.bracketPosition = bracketPosition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getEquipeDomicile() {
        return equipeDomicile;
    }

    public void setEquipeDomicile(Team equipeDomicile) {
        this.equipeDomicile = equipeDomicile;
    }

    public Team getEquipeVisiteur() {
        return equipeVisiteur;
    }

    public void setEquipeVisiteur(Team equipeVisiteur) {
        this.equipeVisiteur = equipeVisiteur;
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

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }

    public MatchStatus getStatut() {
        return statut;
    }

    public void setStatut(MatchStatus statut) {
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

    public Team getVainqueur() {
        return vainqueur;
    }

    public void setVainqueur(Team vainqueur) {
        this.vainqueur = vainqueur;
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

    public boolean hasResult() {
        return scoreDomicile != null && scoreVisiteur != null;
    }

    public Team getGagnant() {
        if (!hasResult()) return null;
        if (scoreDomicile > scoreVisiteur) return equipeDomicile;
        if (scoreVisiteur > scoreDomicile) return equipeVisiteur;
        return null;
    }

    public boolean isNul() {
        return hasResult() && scoreDomicile.equals(scoreVisiteur);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Tournament tournament;
        private Team equipeDomicile;
        private Team equipeVisiteur;
        private LocalDate date;
        private LocalTime heure;
        private String terrain;
        private MatchPhase phase;
        private MatchStatus statut;
        private Integer scoreDomicile;
        private Integer scoreVisiteur;
        private Team vainqueur;
        private int tour;
        private Integer bracketPosition;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder tournament(Tournament tournament) {
            this.tournament = tournament;
            return this;
        }

        public Builder equipeDomicile(Team equipeDomicile) {
            this.equipeDomicile = equipeDomicile;
            return this;
        }

        public Builder equipeVisiteur(Team equipeVisiteur) {
            this.equipeVisiteur = equipeVisiteur;
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

        public Builder phase(MatchPhase phase) {
            this.phase = phase;
            return this;
        }

        public Builder statut(MatchStatus statut) {
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

        public Builder vainqueur(Team vainqueur) {
            this.vainqueur = vainqueur;
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

        public TournamentMatch build() {
            return new TournamentMatch(id, tournament, equipeDomicile, equipeVisiteur, date, heure, terrain, phase, statut, scoreDomicile, scoreVisiteur, vainqueur, tour, bracketPosition);
        }
    }
}
