package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class UpdateTournamentRequest {

    @Size(min = 2, max = 100)
    private String nom;

    private String sport;

    @Size(max = 500)
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private String lieu;

    @Min(value = 2, message = "Le tournoi doit accueillir au moins 2 équipes")
    private Integer nombreMaxEquipes;

    @Min(value = 0, message = "Les points de victoire ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de victoire ne peuvent pas dépasser 20")
    private Integer pointsVictoire;

    @Min(value = 0, message = "Les points de match nul ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de match nul ne peuvent pas dépasser 20")
    private Integer pointsNul;

    @Min(value = 0, message = "Les points de défaite ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de défaite ne peuvent pas dépasser 20")
    private Integer pointsDefaite;

    private String criteresClassement;

    /** Ordre des critères de classement choisis dans le formulaire (1er, 2e, 3e, 4e). */
    private String critere1;
    private String critere2;
    private String critere3;
    private String critere4;

    /** Terrains disponibles, séparés par des virgules. */
    private String terrains;

    private String statut;

    public UpdateTournamentRequest() {
    }

    public UpdateTournamentRequest(String nom, String sport, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, Integer nombreMaxEquipes, Integer pointsVictoire, Integer pointsNul, Integer pointsDefaite, String criteresClassement, String statut) {
        this.nom = nom;
        this.sport = sport;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.nombreMaxEquipes = nombreMaxEquipes;
        this.pointsVictoire = pointsVictoire;
        this.pointsNul = pointsNul;
        this.pointsDefaite = pointsDefaite;
        this.criteresClassement = criteresClassement;
        this.statut = statut;
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

    public Integer getNombreMaxEquipes() {
        return nombreMaxEquipes;
    }

    public void setNombreMaxEquipes(Integer nombreMaxEquipes) {
        this.nombreMaxEquipes = nombreMaxEquipes;
    }

    public Integer getPointsVictoire() {
        return pointsVictoire;
    }

    public void setPointsVictoire(Integer pointsVictoire) {
        this.pointsVictoire = pointsVictoire;
    }

    public Integer getPointsNul() {
        return pointsNul;
    }

    public void setPointsNul(Integer pointsNul) {
        this.pointsNul = pointsNul;
    }

    public Integer getPointsDefaite() {
        return pointsDefaite;
    }

    public void setPointsDefaite(Integer pointsDefaite) {
        this.pointsDefaite = pointsDefaite;
    }

    public String getCriteresClassement() {
        return criteresClassement;
    }

    public void setCriteresClassement(String criteresClassement) {
        this.criteresClassement = criteresClassement;
    }

    public String getCritere1() {
        return critere1;
    }

    public void setCritere1(String critere1) {
        this.critere1 = critere1;
    }

    public String getCritere2() {
        return critere2;
    }

    public void setCritere2(String critere2) {
        this.critere2 = critere2;
    }

    public String getCritere3() {
        return critere3;
    }

    public void setCritere3(String critere3) {
        this.critere3 = critere3;
    }

    public String getCritere4() {
        return critere4;
    }

    public void setCritere4(String critere4) {
        this.critere4 = critere4;
    }

    public String getTerrains() {
        return terrains;
    }

    public void setTerrains(String terrains) {
        this.terrains = terrains;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nom;
        private String sport;
        private String description;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String lieu;
        private Integer nombreMaxEquipes;
        private Integer pointsVictoire;
        private Integer pointsNul;
        private Integer pointsDefaite;
        private String criteresClassement;
        private String statut;

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

        public Builder nombreMaxEquipes(Integer nombreMaxEquipes) {
            this.nombreMaxEquipes = nombreMaxEquipes;
            return this;
        }

        public Builder pointsVictoire(Integer pointsVictoire) {
            this.pointsVictoire = pointsVictoire;
            return this;
        }

        public Builder pointsNul(Integer pointsNul) {
            this.pointsNul = pointsNul;
            return this;
        }

        public Builder pointsDefaite(Integer pointsDefaite) {
            this.pointsDefaite = pointsDefaite;
            return this;
        }

        public Builder criteresClassement(String criteresClassement) {
            this.criteresClassement = criteresClassement;
            return this;
        }

        public Builder statut(String statut) {
            this.statut = statut;
            return this;
        }

        public UpdateTournamentRequest build() {
            return new UpdateTournamentRequest(nom, sport, description, dateDebut, dateFin, lieu, nombreMaxEquipes, pointsVictoire, pointsNul, pointsDefaite, criteresClassement, statut);
        }
    }
}
