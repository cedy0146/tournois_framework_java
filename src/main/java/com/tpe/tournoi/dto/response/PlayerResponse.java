package com.tpe.tournoi.dto.response;

public class PlayerResponse {
    private Long id;
    private String nom;
    private String poste;
    private int numero;
    private Long equipeId;
    private String equipeNom;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String nom, String poste, int numero, Long equipeId, String equipeNom) {
        this.id = id;
        this.nom = nom;
        this.poste = poste;
        this.numero = numero;
        this.equipeId = equipeId;
        this.equipeNom = equipeNom;
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

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    public String getEquipeNom() {
        return equipeNom;
    }

    public void setEquipeNom(String equipeNom) {
        this.equipeNom = equipeNom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String poste;
        private int numero;
        private Long equipeId;
        private String equipeNom;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder equipeId(Long equipeId) {
            this.equipeId = equipeId;
            return this;
        }

        public Builder equipeNom(String equipeNom) {
            this.equipeNom = equipeNom;
            return this;
        }

        public PlayerResponse build() {
            return new PlayerResponse(id, nom, poste, numero, equipeId, equipeNom);
        }
    }
}
