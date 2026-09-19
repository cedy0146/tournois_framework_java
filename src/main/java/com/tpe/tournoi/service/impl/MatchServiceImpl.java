package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.MatchMapper;
import com.tpe.tournoi.repository.*;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final TournamentMatchRepository matchRepo;
    private final TournamentRepository tournamentRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final MatchMapper mapper;
    private final RankingService rankingService;

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getByTournament(Long tournamentId) {
        return matchRepo.findByTournamentIdOrderByTourAsc(tournamentId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getByTournamentAndPhase(Long tournamentId, MatchPhase phase) {
        return matchRepo.findByTournamentIdAndPhase(tournamentId, phase).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse getById(Long id) {
        TournamentMatch match = findMatch(id);
        return mapper.toResponse(match);
    }

    @Override
    @Transactional
    public MatchResultRequest submitResult(Long matchId, MatchResultRequest request) {
        TournamentMatch match = findMatch(matchId);

        if (match.getStatut() == MatchStatus.TERMINE) {
            throw new InvalidOperationException("Ce match est déjà terminé");
        }
        if (match.getStatut() == MatchStatus.REPORTE || match.getStatut() == MatchStatus.ANNULE
                || match.getStatut() == MatchStatus.EN_ATTENTE) {
            throw new InvalidOperationException("Ce match est " + match.getStatut());
        }

        match.setScoreDomicile(request.getScoreDomicile());
        match.setScoreVisiteur(request.getScoreVisiteur());
        match.setStatut(MatchStatus.TERMINE);

        Team gagnant = match.getGagnant();
        if (gagnant != null) {
            match.setVainqueur(gagnant);
        }

        matchRepo.save(match);

        // Recalculer les classements
        rankingService.recalculateRankings(match.getTournament().getId());

        // Pour élimination directe : créer le match suivant si les deux qualifiés sont connus
        if (match.getTournament().getType() == TournamentType.ELIMINATION_DIRECTE && gagnant != null) {
            handleEliminationQualification(match, gagnant);
        }

        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getEliminationTree(Long tournamentId) {
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi", tournamentId));

        List<TournamentMatch> matches = matchRepo.findByTournamentIdOrderByTourAsc(tournamentId);
        return matches.stream().map(mapper::toResponse).toList();
    }

    private void handleEliminationQualification(TournamentMatch completedMatch, Team qualified) {
        Tournament tournament = completedMatch.getTournament();
        MatchPhase currentPhase = completedMatch.getPhase();
        int currentTour = completedMatch.getTour();

        MatchPhase nextPhase = getNextPhase(currentPhase);
        if (nextPhase == null) return;

        int nextTour = currentTour + 1;
        int nextBracketPosition = completedMatch.getBracketPosition() / 2;

        // Chercher un match existant au tour suivant à cette position
        List<TournamentMatch> existing = matchRepo.findByTournamentAndPhaseAndTourAndBracketPosition(
                tournament.getId(), nextPhase, nextTour, nextBracketPosition);

        if (!existing.isEmpty()) {
            TournamentMatch nextMatch = existing.get(0);
            // Compléter le slot vide
            if (nextMatch.getEquipeDomicile() == null) {
                nextMatch.setEquipeDomicile(qualified);
            } else if (nextMatch.getEquipeVisiteur() == null) {
                nextMatch.setEquipeVisiteur(qualified);
            }
            // Si les deux sont maintenant connus, le match devient jouable
            if (nextMatch.getEquipeDomicile() != null && nextMatch.getEquipeVisiteur() != null) {
                nextMatch.setStatut(MatchStatus.PROGRAMME);
            }
            matchRepo.save(nextMatch);
        } else {
            // Créer un nouveau match placeholder
            TournamentMatch nextMatch = TournamentMatch.builder()
                    .tournament(tournament)
                    .equipeDomicile(qualified)
                    .equipeVisiteur(null)
                    .date(tournament.getDateDebut().plusDays((long) nextTour * 7))
                    .phase(nextPhase)
                    .statut(MatchStatus.EN_ATTENTE)
                    .tour(nextTour)
                    .bracketPosition(nextBracketPosition)
                    .build();
            matchRepo.save(nextMatch);
        }
    }

    private MatchPhase getNextPhase(MatchPhase current) {
        return switch (current) {
            case HUITIEME -> MatchPhase.QUART;
            case QUART -> MatchPhase.DEMI;
            case DEMI -> MatchPhase.FINALE;
            default -> null;
        };
    }

    private TournamentMatch findMatch(Long id) {
        return matchRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }
}
