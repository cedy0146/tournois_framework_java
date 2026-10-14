package com.tpe.tournoi.dto.response;

/**
 * Indicateurs globaux affichés sur le tableau de bord.
 */
public class DashboardStatsResponse {

    private long tournoisActifs;
    private long tournoisTotal;
    private long equipesActives;
    private long joueursTotal;
    private long matchsProgrammes;
    private long matchsTermines;
    private long matchsAJour;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long tournoisActifs, long tournoisTotal, long equipesActives,
                                  long joueursTotal, long matchsProgrammes, long matchsTermines,
                                  long matchsAJour) {
        this.tournoisActifs = tournoisActifs;
        this.tournoisTotal = tournoisTotal;
        this.equipesActives = equipesActives;
        this.joueursTotal = joueursTotal;
        this.matchsProgrammes = matchsProgrammes;
        this.matchsTermines = matchsTermines;
        this.matchsAJour = matchsAJour;
    }

    public long getTournoisActifs() {
        return tournoisActifs;
    }

    public void setTournoisActifs(long tournoisActifs) {
        this.tournoisActifs = tournoisActifs;
    }

    public long getTournoisTotal() {
        return tournoisTotal;
    }

    public void setTournoisTotal(long tournoisTotal) {
        this.tournoisTotal = tournoisTotal;
    }

    public long getEquipesActives() {
        return equipesActives;
    }

    public void setEquipesActives(long equipesActives) {
        this.equipesActives = equipesActives;
    }

    public long getJoueursTotal() {
        return joueursTotal;
    }

    public void setJoueursTotal(long joueursTotal) {
        this.joueursTotal = joueursTotal;
    }

    public long getMatchsProgrammes() {
        return matchsProgrammes;
    }

    public void setMatchsProgrammes(long matchsProgrammes) {
        this.matchsProgrammes = matchsProgrammes;
    }

    public long getMatchsTermines() {
        return matchsTermines;
    }

    public void setMatchsTermines(long matchsTermines) {
        this.matchsTermines = matchsTermines;
    }

    public long getMatchsAJour() {
        return matchsAJour;
    }

    public void setMatchsAJour(long matchsAJour) {
        this.matchsAJour = matchsAJour;
    }
}
