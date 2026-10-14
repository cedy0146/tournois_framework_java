package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String logo;

    private String ville;

    private String entraineur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus statut;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> joueurs = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentTeam> tournamentTeams = new ArrayList<>();

    public Team() {
    }

    public Team(Long id, String nom, String logo, String ville, String entraineur, TeamStatus statut, List<Player> joueurs, List<TournamentTeam> tournamentTeams) {
        this.id = id;
        this.nom = nom;
        this.logo = logo;
        this.ville = ville;
        this.entraineur = entraineur;
        this.statut = statut;
        this.joueurs = joueurs;
        this.tournamentTeams = tournamentTeams;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getEntraineur() { return entraineur; }
    public void setEntraineur(String entraineur) { this.entraineur = entraineur; }
    public TeamStatus getStatut() { return statut; }
    public void setStatut(TeamStatus statut) { this.statut = statut; }
    public List<Player> getJoueurs() { return joueurs; }
    public void setJoueurs(List<Player> joueurs) { this.joueurs = joueurs; }
    public List<TournamentTeam> getTournamentTeams() { return tournamentTeams; }
    public void setTournamentTeams(List<TournamentTeam> tournamentTeams) { this.tournamentTeams = tournamentTeams; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nom;
        private String logo;
        private String ville;
        private String entraineur;
        private TeamStatus statut;
        private List<Player> joueurs = new ArrayList<>();
        private List<TournamentTeam> tournamentTeams = new ArrayList<>();
        public Builder id(Long id) { this.id = id; return this; }
        public Builder nom(String nom) { this.nom = nom; return this; }
        public Builder logo(String logo) { this.logo = logo; return this; }
        public Builder ville(String ville) { this.ville = ville; return this; }
        public Builder entraineur(String entraineur) { this.entraineur = entraineur; return this; }
        public Builder statut(TeamStatus statut) { this.statut = statut; return this; }
        public Builder joueurs(List<Player> joueurs) { this.joueurs = joueurs; return this; }
        public Builder tournamentTeams(List<TournamentTeam> tournamentTeams) { this.tournamentTeams = tournamentTeams; return this; }
        public Team build() { return new Team(id, nom, logo, ville, entraineur, statut, joueurs, tournamentTeams); }
    }

    public void addJoueur(Player player) {
        joueurs.add(player);
        player.setEquipe(this);
    }

    public void removeJoueur(Player player) {
        joueurs.remove(player);
        player.setEquipe(null);
    }
}
