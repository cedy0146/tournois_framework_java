package com.tpe.tournoi.dto.response;

public class RankingResponse {
    private int classement;
    private Long tournamentId;
    private String tournamentNom;
    private Long teamId;
    private String teamNom;
    private String teamInitiales;
    private int matchsJoues;
    private int victoires;
    private int nuls;
    private int defaites;
    private int points;
    private int butsMarques;
    private int butsEncaisses;
    private int diffButs;

    public RankingResponse() {
    }

    public RankingResponse(int classement, Long tournamentId, String tournamentNom, Long teamId, String teamNom, String teamInitiales, int matchsJoues, int victoires, int nuls, int defaites, int points, int butsMarques, int butsEncaisses, int diffButs) {
        this.classement = classement;
        this.tournamentId = tournamentId;
        this.tournamentNom = tournamentNom;
        this.teamId = teamId;
        this.teamNom = teamNom;
        this.teamInitiales = teamInitiales;
        this.matchsJoues = matchsJoues;
        this.victoires = victoires;
        this.nuls = nuls;
        this.defaites = defaites;
        this.points = points;
        this.butsMarques = butsMarques;
        this.butsEncaisses = butsEncaisses;
        this.diffButs = diffButs;
    }

    public int getClassement() {
        return classement;
    }

    public void setClassement(int classement) {
        this.classement = classement;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentNom() {
        return tournamentNom;
    }

    public void setTournamentNom(String tournamentNom) {
        this.tournamentNom = tournamentNom;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamNom() {
        return teamNom;
    }

    public void setTeamNom(String teamNom) {
        this.teamNom = teamNom;
    }

    public String getTeamInitiales() {
        return teamInitiales;
    }

    public void setTeamInitiales(String teamInitiales) {
        this.teamInitiales = teamInitiales;
    }

    public int getMatchsJoues() {
        return matchsJoues;
    }

    public void setMatchsJoues(int matchsJoues) {
        this.matchsJoues = matchsJoues;
    }

    public int getVictoires() {
        return victoires;
    }

    public void setVictoires(int victoires) {
        this.victoires = victoires;
    }

    public int getNuls() {
        return nuls;
    }

    public void setNuls(int nuls) {
        this.nuls = nuls;
    }

    public int getDefaites() {
        return defaites;
    }

    public void setDefaites(int defaites) {
        this.defaites = defaites;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getButsMarques() {
        return butsMarques;
    }

    public void setButsMarques(int butsMarques) {
        this.butsMarques = butsMarques;
    }

    public int getButsEncaisses() {
        return butsEncaisses;
    }

    public void setButsEncaisses(int butsEncaisses) {
        this.butsEncaisses = butsEncaisses;
    }

    public int getDiffButs() {
        return diffButs;
    }

    public void setDiffButs(int diffButs) {
        this.diffButs = diffButs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int classement;
        private Long tournamentId;
        private String tournamentNom;
        private Long teamId;
        private String teamNom;
        private String teamInitiales;
        private int matchsJoues;
        private int victoires;
        private int nuls;
        private int defaites;
        private int points;
        private int butsMarques;
        private int butsEncaisses;
        private int diffButs;

        public Builder classement(int classement) {
            this.classement = classement;
            return this;
        }

        public Builder tournamentId(Long tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public Builder tournamentNom(String tournamentNom) {
            this.tournamentNom = tournamentNom;
            return this;
        }

        public Builder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder teamNom(String teamNom) {
            this.teamNom = teamNom;
            return this;
        }

        public Builder teamInitiales(String teamInitiales) {
            this.teamInitiales = teamInitiales;
            return this;
        }

        public Builder matchsJoues(int matchsJoues) {
            this.matchsJoues = matchsJoues;
            return this;
        }

        public Builder victoires(int victoires) {
            this.victoires = victoires;
            return this;
        }

        public Builder nuls(int nuls) {
            this.nuls = nuls;
            return this;
        }

        public Builder defaites(int defaites) {
            this.defaites = defaites;
            return this;
        }

        public Builder points(int points) {
            this.points = points;
            return this;
        }

        public Builder butsMarques(int butsMarques) {
            this.butsMarques = butsMarques;
            return this;
        }

        public Builder butsEncaisses(int butsEncaisses) {
            this.butsEncaisses = butsEncaisses;
            return this;
        }

        public Builder diffButs(int diffButs) {
            this.diffButs = diffButs;
            return this;
        }

        public RankingResponse build() {
            return new RankingResponse(classement, tournamentId, tournamentNom, teamId, teamNom, teamInitiales, matchsJoues, victoires, nuls, defaites, points, butsMarques, butsEncaisses, diffButs);
        }
    }
}
