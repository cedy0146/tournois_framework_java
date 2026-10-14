package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String sport;

    private String description;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private String lieu;

    @Column(nullable = false)
    private int nombreMaxEquipes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus statut;

    private int pointsVictoire = 3;
    private int pointsNul = 1;
    private int pointsDefaite = 0;

    @Column(length = 100)
    private String criteresClassement = "POINTS,DIFFERENCE_BUTS,BUTS_MARQUES";

    @Column(length = 200)
    private String terrains = "";

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentTeam> tournamentTeams = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentMatch> matches = new ArrayList<>();

    public Tournament() {
    }

    public Tournament(Long id, String nom, String sport, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, int nombreMaxEquipes, TournamentType type, TournamentStatus statut, List<TournamentTeam> tournamentTeams, List<TournamentMatch> matches) {
        this.id = id;
        this.nom = nom;
        this.sport = sport;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.nombreMaxEquipes = nombreMaxEquipes;
        this.type = type;
        this.statut = statut;
        this.tournamentTeams = tournamentTeams;
        this.matches = matches;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getNombreMaxEquipes() {
        return nombreMaxEquipes;
    }

    public void setNombreMaxEquipes(int nombreMaxEquipes) {
        this.nombreMaxEquipes = nombreMaxEquipes;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public TournamentStatus getStatut() {
        return statut;
    }

    public void setStatut(TournamentStatus statut) {
        this.statut = statut;
    }

    public int getPointsVictoire() {
        return pointsVictoire;
    }

    public void setPointsVictoire(int pointsVictoire) {
        this.pointsVictoire = pointsVictoire;
    }

    public int getPointsNul() {
        return pointsNul;
    }

    public void setPointsNul(int pointsNul) {
        this.pointsNul = pointsNul;
    }

    public int getPointsDefaite() {
        return pointsDefaite;
    }

    public void setPointsDefaite(int pointsDefaite) {
        this.pointsDefaite = pointsDefaite;
    }

    public String getCriteresClassement() {
        return criteresClassement;
    }

    public void setCriteresClassement(String criteresClassement) {
        this.criteresClassement = criteresClassement;
    }

    public String getTerrains() {
        return terrains;
    }

    public void setTerrains(String terrains) {
        this.terrains = terrains;
    }

    public List<TournamentTeam> getTournamentTeams() {
        return tournamentTeams;
    }

    public void setTournamentTeams(List<TournamentTeam> tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    public List<TournamentMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<TournamentMatch> matches) {
        this.matches = matches;
    }

    public void addTournamentTeam(TournamentTeam tt) {
        tournamentTeams.add(tt);
        tt.setTournament(this);
    }

    public void removeTournamentTeam(TournamentTeam tt) {
        tournamentTeams.remove(tt);
        tt.setTournament(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String sport;
        private String description;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String lieu;
        private int nombreMaxEquipes;
        private TournamentType type;
        private TournamentStatus statut;
        private List<TournamentTeam> tournamentTeams = new ArrayList<>();
        private List<TournamentMatch> matches = new ArrayList<>();

        private int pointsVictoire = 3;
        private int pointsNul = 1;
        private int pointsDefaite = 0;
        private String criteresClassement = "POINTS,DIFFERENCE_BUTS,BUTS_MARQUES";

        private String terrains = "";

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder sport(String sport) {
            this.sport = sport;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder dateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
            return this;
        }

        public Builder dateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
            return this;
        }

        public Builder lieu(String lieu) {
            this.lieu = lieu;
            return this;
        }

        public Builder nombreMaxEquipes(int nombreMaxEquipes) {
            this.nombreMaxEquipes = nombreMaxEquipes;
            return this;
        }

        public Builder type(TournamentType type) {
            this.type = type;
            return this;
        }

        public Builder statut(TournamentStatus statut) {
            this.statut = statut;
            return this;
        }

        public Builder pointsVictoire(int pointsVictoire) {
            this.pointsVictoire = pointsVictoire;
            return this;
        }

        public Builder pointsNul(int pointsNul) {
            this.pointsNul = pointsNul;
            return this;
        }

        public Builder pointsDefaite(int pointsDefaite) {
            this.pointsDefaite = pointsDefaite;
            return this;
        }

        public Builder criteresClassement(String criteresClassement) {
            this.criteresClassement = criteresClassement;
            return this;
        }

        public Builder terrains(String terrains) {
            this.terrains = terrains;
            return this;
        }

        public Builder tournamentTeams(List<TournamentTeam> tournamentTeams) {
            this.tournamentTeams = tournamentTeams;
            return this;
        }

        public Builder matches(List<TournamentMatch> matches) {
            this.matches = matches;
            return this;
        }

        public Tournament build() {
            Tournament t = new Tournament(id, nom, sport, description, dateDebut, dateFin, lieu, nombreMaxEquipes, type, statut, tournamentTeams, matches);
            t.setPointsVictoire(pointsVictoire);
            t.setPointsNul(pointsNul);
            t.setPointsDefaite(pointsDefaite);
            t.setCriteresClassement(criteresClassement);
            t.setTerrains(terrains);
            return t;
        }
    }
}
