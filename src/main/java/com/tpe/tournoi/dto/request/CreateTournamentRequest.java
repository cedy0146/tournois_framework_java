package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class CreateTournamentRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le sport est obligatoire")
    private String sport;

    @Size(max = 500)
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être dans le futur ou présent")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @FutureOrPresent(message = "La date de fin doit être dans le futur ou présent")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private String lieu;

    @Positive(message = "Le nombre max d'équipes doit être positif")
    @Min(2)
    private int nombreMaxEquipes;

    @NotNull(message = "Le type de tournoi est obligatoire")
    private String type;

    @Min(value = 0, message = "Les points de victoire ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de victoire ne peuvent pas dépasser 20")
    private int pointsVictoire = 3;

    @Min(value = 0, message = "Les points de match nul ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de match nul ne peuvent pas dépasser 20")
    private int pointsNul = 1;

    @Min(value = 0, message = "Les points de défaite ne peuvent pas être négatifs")
    @Max(value = 20, message = "Les points de défaite ne peuvent pas dépasser 20")
    private int pointsDefaite = 0;
    private String criteresClassement;

    /** Ordre des critères de classement choisis dans le formulaire (1er, 2e, 3e, 4e). */
    private String critere1;
    private String critere2;
    private String critere3;
    private String critere4;

    /** Terrains disponibles, séparés par des virgules. */
    private String terrains;

    public CreateTournamentRequest() {
    }

    public CreateTournamentRequest(String nom, String sport, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, int nombreMaxEquipes, String type) {
        this.nom = nom;
        this.sport = sport;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.nombreMaxEquipes = nombreMaxEquipes;
        this.type = type;
    }

    public CreateTournamentRequest(String nom, String sport, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, int nombreMaxEquipes, String type, int pointsVictoire, int pointsNul, int pointsDefaite, String criteresClassement) {
        this.nom = nom;
        this.sport = sport;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.nombreMaxEquipes = nombreMaxEquipes;
        this.type = type;
        this.pointsVictoire = pointsVictoire;
        this.pointsNul = pointsNul;
        this.pointsDefaite = pointsDefaite;
        this.criteresClassement = criteresClassement;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        private int nombreMaxEquipes;
        private String type;
        private int pointsVictoire = 3;
        private int pointsNul = 1;
        private int pointsDefaite = 0;
        private String criteresClassement;

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

        public Builder type(String type) {
            this.type = type;
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

        public CreateTournamentRequest build() {
            return new CreateTournamentRequest(nom, sport, description, dateDebut, dateFin, lieu, nombreMaxEquipes, type, this.pointsVictoire, this.pointsNul, this.pointsDefaite, this.criteresClassement);
        }
    }
}
