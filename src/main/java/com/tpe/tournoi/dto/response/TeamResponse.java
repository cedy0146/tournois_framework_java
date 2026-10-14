package com.tpe.tournoi.dto.response;

import java.util.List;

public class TeamResponse {
    private Long id;
    private String nom;
    private String logo;
    private String ville;
    private String entraineur;
    private String statut;
    private int nombreJoueurs;
    private List<PlayerResponse> joueurs;

    public TeamResponse() {
    }

    public TeamResponse(Long id, String nom, String logo, String ville, String entraineur, String statut, int nombreJoueurs, List<PlayerResponse> joueurs) {
        this.id = id;
        this.nom = nom;
        this.logo = logo;
        this.ville = ville;
        this.entraineur = entraineur;
        this.statut = statut;
        this.nombreJoueurs = nombreJoueurs;
        this.joueurs = joueurs;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getEntraineur() {
        return entraineur;
    }

    public void setEntraineur(String entraineur) {
        this.entraineur = entraineur;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNombreJoueurs() {
        return nombreJoueurs;
    }

    public void setNombreJoueurs(int nombreJoueurs) {
        this.nombreJoueurs = nombreJoueurs;
    }

    public List<PlayerResponse> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<PlayerResponse> joueurs) {
        this.joueurs = joueurs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String logo;
        private String ville;
        private String entraineur;
        private String statut;
        private int nombreJoueurs;
        private List<PlayerResponse> joueurs;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder ville(String ville) {
            this.ville = ville;
            return this;
        }

        public Builder entraineur(String entraineur) {
            this.entraineur = entraineur;
            return this;
        }

        public Builder statut(String statut) {
            this.statut = statut;
            return this;
        }

        public Builder nombreJoueurs(int nombreJoueurs) {
            this.nombreJoueurs = nombreJoueurs;
            return this;
        }

        public Builder joueurs(List<PlayerResponse> joueurs) {
            this.joueurs = joueurs;
            return this;
        }

        public TeamResponse build() {
            return new TeamResponse(id, nom, logo, ville, entraineur, statut, nombreJoueurs, joueurs);
        }
    }
}
