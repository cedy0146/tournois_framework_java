package com.tpe.tournoi.dto.response;

import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentType;
import java.util.List;

/**
 * Statistiques d'un tournoi.
 */
public class TournamentStatisticsResponse {

    private long matchsJoues;
    private long matchsRestants;
    private long totalButs;
    private double moyenneButsParMatch;
    private String meilleureAttaque;
    private String meilleureDefense;
    private String plusLargeVictoire;
    private double tauxVictoiresDomicile;
    private long nuls;

    public TournamentStatisticsResponse() {
    }

    public TournamentStatisticsResponse(long matchsJoues, long matchsRestants, long totalButs,
                                        double moyenneButsParMatch, String meilleureAttaque,
                                        String meilleureDefense, String plusLargeVictoire,
                                        double tauxVictoiresDomicile, long nuls) {
        this.matchsJoues = matchsJoues;
        this.matchsRestants = matchsRestants;
        this.totalButs = totalButs;
        this.moyenneButsParMatch = moyenneButsParMatch;
        this.meilleureAttaque = meilleureAttaque;
        this.meilleureDefense = meilleureDefense;
        this.plusLargeVictoire = plusLargeVictoire;
        this.tauxVictoiresDomicile = tauxVictoiresDomicile;
        this.nuls = nuls;
    }

    public long getMatchsJoues() {
        return matchsJoues;
    }

    public void setMatchsJoues(long matchsJoues) {
        this.matchsJoues = matchsJoues;
    }

    public long getMatchsRestants() {
        return matchsRestants;
    }

    public void setMatchsRestants(long matchsRestants) {
        this.matchsRestants = matchsRestants;
    }

    public long getTotalButs() {
        return totalButs;
    }

    public void setTotalButs(long totalButs) {
        this.totalButs = totalButs;
    }

    public double getMoyenneButsParMatch() {
        return moyenneButsParMatch;
    }

    public void setMoyenneButsParMatch(double moyenneButsParMatch) {
        this.moyenneButsParMatch = moyenneButsParMatch;
    }

    public String getMeilleureAttaque() {
        return meilleureAttaque;
    }

    public void setMeilleureAttaque(String meilleureAttaque) {
        this.meilleureAttaque = meilleureAttaque;
    }

    public String getMeilleureDefense() {
        return meilleureDefense;
    }

    public void setMeilleureDefense(String meilleureDefense) {
        this.meilleureDefense = meilleureDefense;
    }

    public String getPlusLargeVictoire() {
        return plusLargeVictoire;
    }

    public void setPlusLargeVictoire(String plusLargeVictoire) {
        this.plusLargeVictoire = plusLargeVictoire;
    }

    public double getTauxVictoiresDomicile() {
        return tauxVictoiresDomicile;
    }

    public void setTauxVictoiresDomicile(double tauxVictoiresDomicile) {
        this.tauxVictoiresDomicile = tauxVictoiresDomicile;
    }

    public long getNuls() {
        return nuls;
    }

    public void setNuls(long nuls) {
        this.nuls = nuls;
    }
}