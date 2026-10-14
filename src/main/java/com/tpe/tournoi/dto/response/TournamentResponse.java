package com.tpe.tournoi.dto.response;

import com.tpe.tournoi.dto.response.TeamSummaryResponse;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.entity.TournamentStatus;
import java.time.LocalDate;
import java.util.List;

public class TournamentResponse {
    private Long id;
    private String nom;
    private String sport;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private int nombreMaxEquipes;
    private String type;
    private String statut;
    private int nombreEquipesInscrites;
    private String statusDisplay;
    private String typeDisplay;
    private String criteresClassement;
    private int pointsVictoire;
    private int pointsNul;
    private int pointsDefaite;
    private String terrains;
    private List<TeamSummaryResponse> registeredTeams;

    public TournamentResponse() {
    }

    public TournamentResponse(Long id, String nom, String sport, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, int nombreMaxEquipes, String type, String statut, int nombreEquipesInscrites) {
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
        this.nombreEquipesInscrites = nombreEquipesInscrites;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNombreEquipesInscrites() {
        return nombreEquipesInscrites;
    }

    public void setNombreEquipesInscrites(int nombreEquipesInscrites) {
        this.nombreEquipesInscrites = nombreEquipesInscrites;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public String getCriteresClassement() {
        return criteresClassement;
    }

    public void setCriteresClassement(String criteresClassement) {
        this.criteresClassement = criteresClassement;
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

    public String getTerrains() {
        return terrains;
    }

    public void setTerrains(String terrains) {
        this.terrains = terrains;
    }

    public List<TeamSummaryResponse> getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(List<TeamSummaryResponse> registeredTeams) {
        this.registeredTeams = registeredTeams;
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
        private String type;
        private String statut;
        private int nombreEquipesInscrites;
        private String statusDisplay;
        private String typeDisplay;
        private String criteresClassement;
        private int pointsVictoire;
        private int pointsNul;
        private int pointsDefaite;
        private String terrains;
        private List<TeamSummaryResponse> registeredTeams;

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

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder statut(String statut) {
            this.statut = statut;
            return this;
        }

        public Builder statusDisplay(String statusDisplay) {
            this.statusDisplay = statusDisplay;
            return this;
        }

        public Builder typeDisplay(String typeDisplay) {
            this.typeDisplay = typeDisplay;
            return this;
        }

        public Builder criteresClassement(String criteresClassement) {
            this.criteresClassement = criteresClassement;
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

        public Builder terrains(String terrains) {
            this.terrains = terrains;
            return this;
        }

        public Builder registeredTeams(List<TeamSummaryResponse> registeredTeams) {
            this.registeredTeams = registeredTeams;
            return this;
        }

        public Builder nombreEquipesInscrites(int nombreEquipesInscrites) {
            this.nombreEquipesInscrites = nombreEquipesInscrites;
            return this;
        }

        public TournamentResponse build() {
            TournamentResponse response = new TournamentResponse(id, nom, sport, description, dateDebut, dateFin, lieu, nombreMaxEquipes, type, statut, nombreEquipesInscrites);
            response.setStatusDisplay(statusDisplay);
            response.setTypeDisplay(typeDisplay);
            response.setCriteresClassement(criteresClassement);
            response.setPointsVictoire(pointsVictoire);
            response.setPointsNul(pointsNul);
            response.setPointsDefaite(pointsDefaite);
            response.setTerrains(terrains);
            response.setRegisteredTeams(registeredTeams);
            return response;
        }
    }
}