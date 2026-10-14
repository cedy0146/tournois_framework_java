package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.response.DashboardStatsResponse;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.dto.response.TeamStatsResponse;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.entity.TeamStatus;
import com.tpe.tournoi.entity.TournamentStatus;
import com.tpe.tournoi.repository.PlayerRepository;
import com.tpe.tournoi.repository.TeamRepository;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.service.StatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    private static final List<TournamentStatus> STATUTS_ACTIFS = List.of(
            TournamentStatus.INSCRIPTION, TournamentStatus.PROGRAMME, TournamentStatus.EN_COURS);

    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final TournamentMatchRepository matchRepo;

    public StatsServiceImpl(TournamentRepository tournamentRepo, TeamRepository teamRepo,
                            PlayerRepository playerRepo, TournamentMatchRepository matchRepo) {
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
        this.matchRepo = matchRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        LocalDate aujourdhui = LocalDate.now();
        return new DashboardStatsResponse(
                tournamentRepo.countByStatutIn(STATUTS_ACTIFS),
                tournamentRepo.count(),
                teamRepo.countByStatut(TeamStatus.ACTIVE),
                playerRepo.count(),
                matchRepo.countByStatut(MatchStatus.PROGRAMME)
                        + matchRepo.countByStatut(MatchStatus.EN_COURS),
                matchRepo.countByStatut(MatchStatus.TERMINE),
                matchRepo.countByStatutAndDateGreaterThanEqual(MatchStatus.PROGRAMME, aujourdhui));
    }

    @Override
    public TeamStatsResponse computeTeamStats(List<MatchResponse> matches, Long teamId) {
        TeamStatsResponse stats = new TeamStatsResponse();
        if (matches == null || teamId == null) {
            return stats;
        }
        for (MatchResponse match : matches) {
            if (!MatchStatus.TERMINE.name().equals(match.getStatut())
                    || match.getScoreDomicile() == null || match.getScoreVisiteur() == null) {
                continue;
            }
            boolean domicile = teamId.equals(match.getEquipeDomicileId());
            if (!domicile && !teamId.equals(match.getEquipeVisiteurId())) {
                continue;
            }
            int marques = domicile ? match.getScoreDomicile() : match.getScoreVisiteur();
            int encaisses = domicile ? match.getScoreVisiteur() : match.getScoreDomicile();

            stats.setMatchsJoues(stats.getMatchsJoues() + 1);
            stats.setButsMarques(stats.getButsMarques() + marques);
            stats.setButsEncaisses(stats.getButsEncaisses() + encaisses);
            if (marques > encaisses) {
                stats.setVictoires(stats.getVictoires() + 1);
            } else if (marques < encaisses) {
                stats.setDefaites(stats.getDefaites() + 1);
            } else {
                stats.setNuls(stats.getNuls() + 1);
            }
        }
        return stats;
    }
}
