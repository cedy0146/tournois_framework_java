package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.response.DashboardStatsResponse;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.dto.response.TeamStatsResponse;

import java.util.List;

public interface StatsService {

    /** Indicateurs globaux affichés sur le tableau de bord. */
    DashboardStatsResponse getDashboardStats();

    /** Statistiques d'une équipe calculées à partir de ses matchs. */
    TeamStatsResponse computeTeamStats(List<MatchResponse> matches, Long teamId);
}
