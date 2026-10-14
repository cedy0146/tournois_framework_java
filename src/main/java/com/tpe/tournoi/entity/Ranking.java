package com.tpe.tournoi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rankings")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private int classement;

    private int matchsJoues;

    private int victoires;

    private int nuls;

    private int defaites;

    private int points;

    private int butsMarques;

    private int butsEncaisses;

    public Ranking() {
    }

    public Ranking(Long id, Tournament tournament, Team team, int classement, int matchsJoues, int victoires, int nuls, int defaites, int points, int butsMarques, int butsEncaisses) {
        this.id = id;
        this.tournament = tournament;
        this.team = team;
        this.classement = classement;
        this.matchsJoues = matchsJoues;
        this.victoires = victoires;
        this.nuls = nuls;
        this.defaites = defaites;
        this.points = points;
        this.butsMarques = butsMarques;
        this.butsEncaisses = butsEncaisses;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public int getClassement() { return classement; }
    public void setClassement(int classement) { this.classement = classement; }
    public int getMatchsJoues() { return matchsJoues; }
    public void setMatchsJoues(int matchsJoues) { this.matchsJoues = matchsJoues; }
    public int getVictoires() { return victoires; }
    public void setVictoires(int victoires) { this.victoires = victoires; }
    public int getNuls() { return nuls; }
    public void setNuls(int nuls) { this.nuls = nuls; }
    public int getDefaites() { return defaites; }
    public void setDefaites(int defaites) { this.defaites = defaites; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getButsMarques() { return butsMarques; }
    public void setButsMarques(int butsMarques) { this.butsMarques = butsMarques; }
    public int getButsEncaisses() { return butsEncaisses; }
    public void setButsEncaisses(int butsEncaisses) { this.butsEncaisses = butsEncaisses; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Tournament tournament;
        private Team team;
        private int classement;
        private int matchsJoues;
        private int victoires;
        private int nuls;
        private int defaites;
        private int points;
        private int butsMarques;
        private int butsEncaisses;
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder tournament(Tournament tournament) { this.tournament = tournament; return this; }
        public Builder team(Team team) { this.team = team; return this; }
        public Builder classement(int classement) { this.classement = classement; return this; }
        public Builder matchsJoues(int matchsJoues) { this.matchsJoues = matchsJoues; return this; }
        public Builder victoires(int victoires) { this.victoires = victoires; return this; }
        public Builder nuls(int nuls) { this.nuls = nuls; return this; }
        public Builder defaites(int defaites) { this.defaites = defaites; return this; }
        public Builder points(int points) { this.points = points; return this; }
        public Builder butsMarques(int butsMarques) { this.butsMarques = butsMarques; return this; }
        public Builder butsEncaisses(int butsEncaisses) { this.butsEncaisses = butsEncaisses; return this; }
        public Ranking build() { return new Ranking(id, tournament, team, classement, matchsJoues, victoires, nuls, defaites, points, butsMarques, butsEncaisses); }
    }

    public int getDiffButs() {
        return butsMarques - butsEncaisses;
    }
}
