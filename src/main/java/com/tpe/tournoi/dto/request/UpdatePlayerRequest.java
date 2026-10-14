package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdatePlayerRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le poste est obligatoire")
    private String poste;

    @Positive(message = "Le numéro doit être positif")
    private int numero;

    public UpdatePlayerRequest() {
    }

    public UpdatePlayerRequest(String nom, String poste, int numero) {
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
}
