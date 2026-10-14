package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;

public class CreatePlayerRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le poste est obligatoire")
    private String poste;

    @Positive(message = "Le numéro doit être positif")
    private int numero;

    public CreatePlayerRequest() {
    }

    public CreatePlayerRequest(String nom, String poste, int numero) {
        this.nom = nom;
        this.poste = poste;
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nom;
        private String poste;
        private int numero;

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder poste(String poste) {
            this.poste = poste;
            return this;
        }

        public Builder numero(int numero) {
            this.numero = numero;
            return this;
        }

        public CreatePlayerRequest build() {
            return new CreatePlayerRequest(nom, poste, numero);
        }
    }
}
