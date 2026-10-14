package com.tpe.tournoi.dto.response;

import java.util.List;

public class TeamSummaryResponse {
    private Long id;
    private String nom;
    private String ville;
    private String entraineur;
    private String statut;

    public TeamSummaryResponse() {
    }

    public TeamSummaryResponse(Long id, String nom, String ville, String entraineur) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
        this.entraineur = entraineur;
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
}