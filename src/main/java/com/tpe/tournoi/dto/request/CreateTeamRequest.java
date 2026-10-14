package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;

public class CreateTeamRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    private String logo;

    @Size(max = 100)
    private String ville;

    @Size(max = 100)
    private String entraineur;

    public CreateTeamRequest() {
    }

    public CreateTeamRequest(String nom, String logo, String ville, String entraineur) {
        this.nom = nom;
        this.logo = logo;
        this.ville = ville;
        this.entraineur = entraineur;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nom;
        private String logo;
        private String ville;
        private String entraineur;

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

        public CreateTeamRequest build() {
            return new CreateTeamRequest(nom, logo, ville, entraineur);
        }
    }
}
