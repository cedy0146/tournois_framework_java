package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.response.RankingResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.mapper.RankingMapper;
import com.tpe.tournoi.repository.RankingRepository;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.repository.TournamentTeamRepository;
import com.tpe.tournoi.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepo;
    private final TournamentMatchRepository matchRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final TournamentRepository tournamentRepo;
    private final RankingMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> getRanking(Long tournamentId) {
        List<Ranking> rankings = rankingRepo.findByTournamentIdOrderByClassementAsc(tournamentId);
        if (rankings.isEmpty()) {
            // Calculer si pas encore de classement
            recalculateRankings(tournamentId);
            rankings = rankingRepo.findByTournamentIdOrderByClassementAsc(tournamentId);
        }
        return rankings.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void recalculateRankings(Long tournamentId) {
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new com.tpe.tournoi.exception.ResourceNotFoundException("Tournoi", tournamentId));

        // Supprimer les anciens classements
        rankingRepo.deleteByTournamentId(tournamentId);

        List<Team> teams = tournamentTeamRepo.findByTournamentIdAndInscriptionStatut(
                        tournamentId, RegistrationStatus.INSCRIT)
                .stream()
                .map(TournamentTeam::getTeam)
                .toList();

        List<TournamentMatch> finishedMatches = matchRepo.findByTournamentId(tournamentId)
                .stream()
                .filter(m -> m.getStatut() == MatchStatus.TERMINE && m.getPhase() == MatchPhase.POULE)
                .toList();

        Map<Long, Ranking> rankingMap = new HashMap<>();

        for (Team team : teams) {
            Ranking ranking = Ranking.builder()
                    .tournament(tournament)
                    .team(team)
                    .matchsJoues(0)
                    .victoires(0)
                    .nuls(0)
                    .defaites(0)
                    .points(0)
                    .butsMarques(0)
                    .butsEncaisses(0)
                    .build();
            rankingMap.put(team.getId(), ranking);
        }

        for (TournamentMatch match : finishedMatches) {
            Ranking homeRank = rankingMap.get(match.getEquipeDomicile().getId());
            Ranking awayRank = rankingMap.get(match.getEquipeVisiteur().getId());

            if (homeRank == null || awayRank == null) continue;

            int sd = match.getScoreDomicile();
            int sv = match.getScoreVisiteur();

            homeRank.setMatchsJoues(homeRank.getMatchsJoues() + 1);
            awayRank.setMatchsJoues(awayRank.getMatchsJoues() + 1);

            homeRank.setButsMarques(homeRank.getButsMarques() + sd);
            homeRank.setButsEncaisses(homeRank.getButsEncaisses() + sv);
            awayRank.setButsMarques(awayRank.getButsMarques() + sv);
            awayRank.setButsEncaisses(awayRank.getButsEncaisses() + sd);

            if (sd > sv) {
                homeRank.setVictoires(homeRank.getVictoires() + 1);
                homeRank.setPoints(homeRank.getPoints() + 3);
                awayRank.setDefaites(awayRank.getDefaites() + 1);
            } else if (sd < sv) {
                awayRank.setVictoires(awayRank.getVictoires() + 1);
                awayRank.setPoints(awayRank.getPoints() + 3);
                homeRank.setDefaites(homeRank.getDefaites() + 1);
            } else {
                homeRank.setNuls(homeRank.getNuls() + 1);
                awayRank.setNuls(awayRank.getNuls() + 1);
                homeRank.setPoints(homeRank.getPoints() + 1);
                awayRank.setPoints(awayRank.getPoints() + 1);
            }
        }

        // Tri : points DESC, différence de buts DESC, buts marqués DESC
        List<Ranking> sortedRankings = rankingMap.values().stream()
                .sorted(Comparator
                        .comparingInt(Ranking::getPoints).reversed()
                        .thenComparing(r -> r.getDiffButs(), Comparator.reverseOrder())
                        .thenComparingInt(Ranking::getButsMarques).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedRankings.size(); i++) {
            Ranking r = sortedRankings.get(i);
            r.setClassement(i + 1);
        }

        rankingRepo.saveAll(sortedRankings);
    }
}
