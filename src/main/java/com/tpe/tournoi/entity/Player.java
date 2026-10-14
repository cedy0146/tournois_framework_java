package com.tpe.tournoi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerPosition poste;

    private int numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Team equipe;

    public Player() {
    }

    public Player(Long id, String nom, PlayerPosition poste, int numero, Team equipe) {
        this.id = id;
        this.nom = nom;
        this.poste = poste;
        this.numero = numero;
        this.equipe = equipe;
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

    public PlayerPosition getPoste() {
        return poste;
    }

    public void setPoste(PlayerPosition poste) {
        this.poste = poste;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Team getEquipe() {
        return equipe;
    }

    public void setEquipe(Team equipe) {
        this.equipe = equipe;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private PlayerPosition poste;
        private int numero;
        private Team equipe;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder poste(PlayerPosition poste) {
            this.poste = poste;
            return this;
        }

        public Builder numero(int numero) {
            this.numero = numero;
            return this;
        }

        public Builder equipe(Team equipe) {
            this.equipe = equipe;
            return this;
        }

        public Player build() {
            return new Player(id, nom, poste, numero, equipe);
        }
    }
}
