package com.tpe.tournoi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tournament_teams",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "team_id"}))
public class TournamentTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus inscriptionStatut;

    private int seed;

    public TournamentTeam() {
    }

    public TournamentTeam(Long id, Tournament tournament, Team team, RegistrationStatus inscriptionStatut, int seed) {
        this.id = id;
        this.tournament = tournament;
        this.team = team;
        this.inscriptionStatut = inscriptionStatut;
        this.seed = seed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public RegistrationStatus getInscriptionStatut() {
        return inscriptionStatut;
    }

    public void setInscriptionStatut(RegistrationStatus inscriptionStatut) {
        this.inscriptionStatut = inscriptionStatut;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public boolean isInscrit() {
        return RegistrationStatus.INSCRIT.equals(inscriptionStatut);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Tournament tournament;
        private Team team;
        private RegistrationStatus inscriptionStatut;
        private int seed;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder tournament(Tournament tournament) {
            this.tournament = tournament;
            return this;
        }

        public Builder team(Team team) {
            this.team = team;
            return this;
        }

        public Builder inscriptionStatut(RegistrationStatus inscriptionStatut) {
            this.inscriptionStatut = inscriptionStatut;
            return this;
        }

        public Builder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public TournamentTeam build() {
            return new TournamentTeam(id, tournament, team, inscriptionStatut, seed);
        }
    }
}
