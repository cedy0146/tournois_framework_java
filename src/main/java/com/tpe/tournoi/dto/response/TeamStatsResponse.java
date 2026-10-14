package com.tpe.tournoi.dto.response;

/**
 * Statistiques d'une équipe sur l'ensemble de ses matchs joués.
 */
public class TeamStatsResponse {

    private int matchsJoues;
    private int victoires;
    private int nuls;
    private int defaites;
    private int butsMarques;
    private int butsEncaisses;

    public TeamStatsResponse() {
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

    public int getDifferenceButs() {
        return butsMarques - butsEncaisses;
    }

    public int getPoints() {
        return victoires * 3 + nuls;
    }
}
