package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.request.UpdateMatchScheduleRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.MatchMapper;
import com.tpe.tournoi.repository.*;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.RankingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MatchServiceImpl implements MatchService {

    private final TournamentMatchRepository matchRepo;
    private final TournamentRepository tournamentRepo;
    private final MatchMapper mapper;
    private final RankingService rankingService;

    public MatchServiceImpl(TournamentMatchRepository matchRepo, TournamentRepository tournamentRepo,
                            MatchMapper mapper, RankingService rankingService) {
        this.matchRepo = matchRepo;
        this.tournamentRepo = tournamentRepo;
        this.mapper = mapper;
        this.rankingService = rankingService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchResponse> getMatches(Pageable pageable, Long tournamentId, MatchPhase phase,
                                          MatchStatus statut, LocalDate date, Long equipeId) {
        return matchRepo.findByFilters(tournamentId, phase, statut, date, equipeId, pageable)
                .map(mapper::toResponse);
    }

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
        Tournament tournament = match.getTournament();

        if (tournament.getStatut() == TournamentStatus.TERMINE
                || tournament.getStatut() == TournamentStatus.ANNULE) {
            throw new InvalidOperationException(
                    "Impossible de modifier le résultat d'un tournoi " + tournament.getStatut());
        }

        if (match.getStatut() == MatchStatus.REPORTE || match.getStatut() == MatchStatus.ANNULE
                || match.getStatut() == MatchStatus.EN_ATTENTE) {
            throw new InvalidOperationException("Ce match est " + match.getStatut());
        }

        validerScores(request);

        boolean elimination = tournament.getType() == TournamentType.ELIMINATION_DIRECTE;

        if (elimination) {
            if (match.getEquipeDomicile() == null || match.getEquipeVisiteur() == null) {
                throw new InvalidOperationException(
                        "Impossible de saisir un résultat pour un match dont les équipes ne sont pas définies");
            }
            if (request.getScoreDomicile().equals(request.getScoreVisiteur())) {
                throw new InvalidOperationException(
                        "Un match à élimination directe ne peut pas se terminer sur un nul");
            }
        }

        Team ancienVainqueur = match.getVainqueur();
        match.setScoreDomicile(request.getScoreDomicile());
        match.setScoreVisiteur(request.getScoreVisiteur());
        match.setStatut(MatchStatus.TERMINE);
        match.setVainqueur(gagnant(match));

        // En élimination directe, interdire toute modification qui rendrait
        // l'arbre incohérent : si le match suivant a déjà un résultat saisi,
        // changer le vainqueur ici invaliderait une qualification déjà jouée.
        if (elimination) {
            Team nouveauVainqueur = match.getVainqueur();
            boolean vainqueurChange = !Objects.equals(
                    ancienVainqueur != null ? ancienVainqueur.getId() : null,
                    nouveauVainqueur != null ? nouveauVainqueur.getId() : null);
            if (vainqueurChange) {
                TournamentMatch suivant = trouverMatchSuivant(match);
                if (suivant != null && suivant.getStatut() == MatchStatus.TERMINE) {
                    throw new ConflictException(
                            "Impossible de modifier ce résultat : le match suivant ("
                                    + libelle(suivant) + ") a déjà un résultat saisi. "
                                    + "Modifiez d'abord le résultat du match suivant.");
                }
            }
        }

        matchRepo.save(match);

        // Recalculer les classements (chaîne de critères configurable du tournoi)
        rankingService.recalculateRankings(tournament.getId());

        if (elimination) {
            propagerQualification(match, ancienVainqueur, match.getVainqueur());
        }

        majStatutTournoi(tournament, elimination);

        return request;
    }

    @Override
    @Transactional
    public MatchResponse updateSchedule(Long matchId, UpdateMatchScheduleRequest request) {
        TournamentMatch match = findMatch(matchId);
        Tournament tournament = match.getTournament();

        if (tournament.getStatut() == TournamentStatus.TERMINE
                || tournament.getStatut() == TournamentStatus.ANNULE) {
            throw new InvalidOperationException(
                    "Impossible de replanifier un match d'un tournoi " + tournament.getStatut());
        }
        if (request.getDate().isBefore(tournament.getDateDebut())
                || request.getDate().isAfter(tournament.getDateFin())) {
            throw new InvalidOperationException(
                    "La date du match doit être comprise entre le " + tournament.getDateDebut()
                            + " et le " + tournament.getDateFin() + " (tournoi " + tournament.getNom() + ")");
        }

        verifierConflitPlanning(match.getId(), request.getDate(), request.getHeure(),
                request.getTerrain(), equipesDuMatch(match));

        match.setDate(request.getDate());
        match.setHeure(request.getHeure());
        match.setTerrain(request.getTerrain());
        matchRepo.save(match);
        return mapper.toResponse(match);
    }

    private void validerScores(MatchResultRequest request) {
        if (request.getScoreDomicile() == null || request.getScoreVisiteur() == null) {
            throw new InvalidOperationException("Les scores domicile et visiteur sont obligatoires");
        }
        if (request.getScoreDomicile() < 0 || request.getScoreVisiteur() < 0) {
            throw new InvalidOperationException("Les scores ne peuvent pas être négatifs");
        }
    }

    private Team gagnant(TournamentMatch match) {
        if (match.getEquipeDomicile() == null || match.getEquipeVisiteur() == null) {
            return null;
        }
        if (match.getScoreDomicile() > match.getScoreVisiteur()) {
            return match.getEquipeDomicile();
        }
        if (match.getScoreVisiteur() > match.getScoreDomicile()) {
            return match.getEquipeVisiteur();
        }
        return null;
    }

    /**
     * Repousse le vainqueur dans le match du tour suivant en remplaçant
     * éventuellement l'ancien qualifié.
     */
    private void propagerQualification(TournamentMatch match, Team ancienVainqueur, Team nouveauVainqueur) {
        TournamentMatch suivant = trouverMatchSuivant(match);
        if (suivant == null) {
            return;
        }
        if (ancienVainqueur != null) {
            if (suivant.getEquipeDomicile() != null
                    && ancienVainqueur.getId().equals(suivant.getEquipeDomicile().getId())) {
                suivant.setEquipeDomicile(null);
            }
            if (suivant.getEquipeVisiteur() != null
                    && ancienVainqueur.getId().equals(suivant.getEquipeVisiteur().getId())) {
                suivant.setEquipeVisiteur(null);
            }
        }
        if (nouveauVainqueur != null) {
            if (suivant.getEquipeDomicile() == null) {
                suivant.setEquipeDomicile(nouveauVainqueur);
            } else if (suivant.getEquipeVisiteur() == null) {
                suivant.setEquipeVisiteur(nouveauVainqueur);
            } else if (!nouveauVainqueur.getId().equals(idEquipe(suivant.getEquipeDomicile()))
                    && !nouveauVainqueur.getId().equals(idEquipe(suivant.getEquipeVisiteur()))) {
                throw new ConflictException(
                        "Le match suivant (" + libelle(suivant) + ") contient déjà deux équipes");
            }
        }
        suivant.setStatut(suivant.getEquipeDomicile() != null && suivant.getEquipeVisiteur() != null
                ? MatchStatus.PROGRAMME : MatchStatus.EN_ATTENTE);
        matchRepo.save(suivant);
    }

    private TournamentMatch trouverMatchSuivant(TournamentMatch match) {
        if (match.getBracketPosition() == null) {
            return null;
        }
        int totalTours = totalTours(match.getTournament().getId());
        if (match.getTour() >= totalTours) {
            return null; // finale
        }
        int positionSuivante = match.getBracketPosition() / 2;
        return matchRepo.findByTournamentIdAndTourAndBracketPosition(
                        match.getTournament().getId(), match.getTour() + 1, positionSuivante)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private int totalTours(Long tournamentId) {
        return matchRepo.findByTournamentId(tournamentId).stream()
                .mapToInt(TournamentMatch::getTour)
                .max()
                .orElse(1);
    }

    private void majStatutTournoi(Tournament tournament, boolean elimination) {
        // Premier résultat saisi : passage à EN_COURS
        if (tournament.getStatut() == TournamentStatus.PROGRAMME) {
            tournament.setStatut(TournamentStatus.EN_COURS);
            tournamentRepo.save(tournament);
        }
        if (tournament.getStatut() != TournamentStatus.EN_COURS) {
            return;
        }

        boolean complet;
        if (elimination) {
            // En élimination directe, le tournoi est terminé quand la finale est jouée
            int totalTours = totalTours(tournament.getId());
            complet = matchRepo.findByTournamentIdAndStatut(tournament.getId(), MatchStatus.TERMINE)
                    .stream()
                    .anyMatch(m -> m.getTour() == totalTours);
        } else {
            // Championnat : quand tous les matchs sont terminés
            long total = matchRepo.countByTournamentId(tournament.getId());
            long termine = matchRepo.countByTournamentIdAndStatut(tournament.getId(), MatchStatus.TERMINE);
            complet = total > 0 && termine == total;
        }

        if (complet) {
            tournament.setStatut(TournamentStatus.TERMINE);
            tournamentRepo.save(tournament);
        }
    }

    /**
     * Vérifie qu'aucune équipe n'a deux matchs au même jour/créneau et
     * qu'aucun terrain n'est réservé deux fois au même créneau.
     */
    private void verifierConflitPlanning(Long matchId, LocalDate date, LocalTime heure,
                                         String terrain, List<Long> teamIds) {
        if (!teamIds.isEmpty()) {
            List<TournamentMatch> conflits = matchRepo.findConflictsForTeams(matchId, date, heure, teamIds);
            if (!conflits.isEmpty()) {
                TournamentMatch conflit = conflits.get(0);
                String nomEquipe = nomEquipeCommune(conflit, teamIds);
                throw new ConflictException(
                        "Conflit de planning : l'équipe '" + nomEquipe + "' a déjà un match le "
                                + date + " à " + heure + " (match n°" + conflit.getId() + ")");
            }
        }
        if (terrain != null && !terrain.isBlank()) {
            List<TournamentMatch> conflitsTerrain = matchRepo.findConflictsForTerrain(matchId, date, heure, terrain);
            if (!conflitsTerrain.isEmpty()) {
                throw new ConflictException(
                        "Conflit de planning : le terrain '" + terrain + "' est déjà occupé le "
                                + date + " à " + heure + " (match n°"
                                + conflitsTerrain.get(0).getId() + ")");
            }
        }
    }

    private List<Long> equipesDuMatch(TournamentMatch match) {
        List<Long> ids = new ArrayList<>(2);
        if (match.getEquipeDomicile() != null) {
            ids.add(match.getEquipeDomicile().getId());
        }
        if (match.getEquipeVisiteur() != null) {
            ids.add(match.getEquipeVisiteur().getId());
        }
        return ids;
    }

    private String nomEquipeCommune(TournamentMatch conflit, List<Long> teamIds) {
        if (conflit.getEquipeDomicile() != null && teamIds.contains(conflit.getEquipeDomicile().getId())) {
            return conflit.getEquipeDomicile().getNom();
        }
        if (conflit.getEquipeVisiteur() != null && teamIds.contains(conflit.getEquipeVisiteur().getId())) {
            return conflit.getEquipeVisiteur().getNom();
        }
        return "?";
    }

    private String libelle(TournamentMatch match) {
        String domicile = match.getEquipeDomicile() != null ? match.getEquipeDomicile().getNom() : "?";
        String visiteur = match.getEquipeVisiteur() != null ? match.getEquipeVisiteur().getNom() : "?";
        return domicile + " vs " + visiteur + " (tour " + match.getTour() + ")";
    }

    private Long idEquipe(Team team) {
        return team != null ? team.getId() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getEliminationTree(Long tournamentId) {
        tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi", tournamentId));

        List<TournamentMatch> matches = matchRepo.findByTournamentIdOrderByTourAsc(tournamentId);
        return matches.stream().map(mapper::toResponse).toList();
    }

    private TournamentMatch findMatch(Long id) {
        return matchRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }
}
