package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.response.RankingResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.mapper.RankingMapper;
import com.tpe.tournoi.repository.RankingRepository;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.repository.TournamentTeamRepository;
import com.tpe.tournoi.exception.ResourceNotFoundException;
import com.tpe.tournoi.service.RankingService;
import com.tpe.tournoi.util.RankingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepo;
    private final TournamentMatchRepository matchRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final TournamentRepository tournamentRepo;
    private final RankingMapper mapper;

    public RankingServiceImpl(RankingRepository rankingRepo, TournamentMatchRepository matchRepo,
                              TournamentTeamRepository tournamentTeamRepo, TournamentRepository tournamentRepo,
                              RankingMapper mapper) {
        this.rankingRepo = rankingRepo;
        this.matchRepo = matchRepo;
        this.tournamentTeamRepo = tournamentTeamRepo;
        this.tournamentRepo = tournamentRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> getAllRankings() {
        return rankingRepo.findAllWithTournamentAndTeam().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<RankingResponse> getRanking(Long tournamentId) {
        recalculateRankings(tournamentId);
        List<Ranking> rankings = rankingRepo.findByTournamentIdOrderByClassementAsc(tournamentId);
        return rankings.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void recalculateRankings(Long tournamentId) {
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi", tournamentId));

        // Supprimer les anciens classements
        rankingRepo.deleteByTournamentId(tournamentId);

        // Le classement n'a de sens que pour les championnats :
        // en élimination directe on ne produit pas de classement de poule trompeur.
        if (tournament.getType() == TournamentType.ELIMINATION_DIRECTE) {
            return;
        }

        List<Team> teams = tournamentTeamRepo.findByTournamentIdAndInscriptionStatut(
                        tournamentId, RegistrationStatus.INSCRIT)
                .stream()
                .map(TournamentTeam::getTeam)
                .toList();

        List<TournamentMatch> finishedMatches = matchRepo.findByTournamentId(tournamentId)
                .stream()
                .filter(m -> m.getStatut() == MatchStatus.TERMINE)
                .filter(m -> m.getEquipeDomicile() != null && m.getEquipeVisiteur() != null)
                .filter(m -> m.getScoreDomicile() != null && m.getScoreVisiteur() != null)
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

        int pointsVictoire = tournament.getPointsVictoire();
        int pointsNul = tournament.getPointsNul();
        int pointsDefaite = tournament.getPointsDefaite();

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

            boolean homeGagne = sd > sv;
            boolean awayGagne = sv > sd;

            if (homeGagne) {
                homeRank.setVictoires(homeRank.getVictoires() + 1);
                homeRank.setPoints(homeRank.getPoints() + pointsVictoire);
                awayRank.setDefaites(awayRank.getDefaites() + 1);
                awayRank.setPoints(awayRank.getPoints() + pointsDefaite);
            } else if (awayGagne) {
                awayRank.setVictoires(awayRank.getVictoires() + 1);
                awayRank.setPoints(awayRank.getPoints() + pointsVictoire);
                homeRank.setDefaites(homeRank.getDefaites() + 1);
                homeRank.setPoints(homeRank.getPoints() + pointsDefaite);
            } else {
                homeRank.setNuls(homeRank.getNuls() + 1);
                awayRank.setNuls(awayRank.getNuls() + 1);
                homeRank.setPoints(homeRank.getPoints() + pointsNul);
                awayRank.setPoints(awayRank.getPoints() + pointsNul);
            }
        }

        // Tri dynamique selon les critères configurés (confrontation directe comprise)
        List<RankingCriterion> criteres = RankingUtil.parse(tournament.getCriteresClassement());
        List<Ranking> sortedRankings = RankingUtil.sort(
                new ArrayList<>(rankingMap.values()), criteres, finishedMatches,
                pointsVictoire, pointsNul, pointsDefaite);

        for (int i = 0; i < sortedRankings.size(); i++) {
            Ranking r = sortedRankings.get(i);
            r.setClassement(i + 1);
        }

        rankingRepo.saveAll(sortedRankings);
    }
}
