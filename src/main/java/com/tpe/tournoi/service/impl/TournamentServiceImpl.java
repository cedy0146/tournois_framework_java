package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.CreateTournamentRequest;
import com.tpe.tournoi.dto.request.UpdateTournamentRequest;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.TournamentMapper;
import com.tpe.tournoi.repository.*;
import com.tpe.tournoi.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final TournamentMatchRepository matchRepo;
    private final RankingRepository rankingRepo;
    private final TournamentMapper mapper;

    @Override
    @Transactional
    public TournamentResponse create(CreateTournamentRequest request) {
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }

        TournamentType type;
        try {
            type = TournamentType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Type de tournoi invalide: " + request.getType());
        }

        Tournament tournament = Tournament.builder()
                .nom(request.getNom())
                .sport(request.getSport())
                .description(request.getDescription())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .lieu(request.getLieu())
                .nombreMaxEquipes(request.getNombreMaxEquipes())
                .type(type)
                .statut(TournamentStatus.BROUILLON)
                .build();

        tournament = tournamentRepo.save(tournament);
        return mapper.toResponse(tournament);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getById(Long id) {
        Tournament t = findTournament(id);
        return mapper.toResponse(t);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TournamentResponse> getAll(Pageable pageable, String statut, String sport, String nom) {
        TournamentStatus status = null;
        if (statut != null && !statut.isBlank()) {
            try {
                status = TournamentStatus.valueOf(statut);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Statut invalide: " + statut);
            }
        }
        Page<Tournament> page = tournamentRepo.findByFilters(status, sport, nom, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public TournamentResponse update(Long id, UpdateTournamentRequest request) {
        Tournament t = findTournament(id);

        if (t.getStatut() == TournamentStatus.TERMINE || t.getStatut() == TournamentStatus.ANNULE) {
            throw new InvalidOperationException(
                    "Impossible de modifier un tournoi au statut " + t.getStatut());
        }

        if (request.getNom() != null) t.setNom(request.getNom());
        if (request.getSport() != null) t.setSport(request.getSport());
        if (request.getDescription() != null) t.setDescription(request.getDescription());
        if (request.getLieu() != null) t.setLieu(request.getLieu());
        if (request.getNombreMaxEquipes() > 0) t.setNombreMaxEquipes(request.getNombreMaxEquipes());

        if (request.getStatut() != null) {
            TournamentStatus newStatus = TournamentStatus.valueOf(request.getStatut());
            validateStatusTransition(t.getStatut(), newStatus);
            t.setStatut(newStatus);
        }

        t = tournamentRepo.save(t);
        return mapper.toResponse(t);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tournament t = findTournament(id);
        if (t.getStatut() == TournamentStatus.EN_COURS) {
            throw new InvalidOperationException("Impossible de supprimer un tournoi en cours");
        }
        tournamentRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void registerTeam(Long tournamentId, Long teamId) {
        Tournament t = findTournament(tournamentId);
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe", teamId));

        if (t.getStatut() != TournamentStatus.BROUILLON && t.getStatut() != TournamentStatus.INSCRIPTION) {
            throw new InvalidOperationException(
                    "Inscription impossible: le tournoi est au statut " + t.getStatut());
        }

        if (tournamentTeamRepo.existsByTournamentIdAndTeamId(tournamentId, teamId)) {
            throw new ConflictException("L'équipe '" + team.getNom() + "' est déjà inscrite à ce tournoi");
        }

        long inscrits = tournamentTeamRepo.countByTournamentIdAndInscriptionStatut(tournamentId, RegistrationStatus.INSCRIT);
        if (inscrits >= t.getNombreMaxEquipes()) {
            throw new InvalidOperationException("Le tournoi est complet (" + t.getNombreMaxEquipes() + " équipes max)");
        }

        TournamentTeam tt = TournamentTeam.builder()
                .tournament(t)
                .team(team)
                .inscriptionStatut(RegistrationStatus.INSCRIT)
                .seed((int) inscrits + 1)
                .build();
        tournamentTeamRepo.save(tt);
    }

    @Override
    @Transactional
    public void removeTeam(Long tournamentId, Long teamId) {
        Tournament t = findTournament(tournamentId);
        if (t.getStatut() == TournamentStatus.EN_COURS || t.getStatut() == TournamentStatus.TERMINE) {
            throw new InvalidOperationException("Retrait impossible: le tournoi est en cours ou terminé");
        }

        TournamentTeam tt = tournamentTeamRepo.findByTournamentIdAndTeamId(tournamentId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription"));

        tournamentTeamRepo.delete(tt);
    }

    @Override
    @Transactional
    public void generateMatches(Long tournamentId) {
        Tournament t = findTournament(tournamentId);

        if (t.getStatut() == TournamentStatus.TERMINE || t.getStatut() == TournamentStatus.ANNULE) {
            throw new InvalidOperationException("Génération impossible: tournoi terminé ou annulé");
        }

        List<Team> teams = tournamentTeamRepo.findByTournamentIdAndInscriptionStatut(tournamentId, RegistrationStatus.INSCRIT)
                .stream().map(TournamentTeam::getTeam).collect(Collectors.toList());

        if (teams.size() < 2) {
            throw new InvalidOperationException("Il faut au moins 2 équipes inscrites pour générer des matchs");
        }

        if (matchRepo.existsByTournamentId(tournamentId)) {
            throw new ConflictException("Des matchs existent déjà pour ce tournoi. Supprimez-les d'abord.");
        }

        switch (t.getType()) {
            case CHAMPIONNAT_SIMPLE -> generateRoundRobin(t, teams, false);
            case CHAMPIONNAT_ALLER_RETOUR -> generateRoundRobin(t, teams, true);
            case ELIMINATION_DIRECTE -> generateElimination(t, teams);
        }

        t.setStatut(TournamentStatus.PROGRAMME);
        tournamentRepo.save(t);
    }

    // --- Round-Robin : Algorithme du cercle (Circle Method) ---
    private void generateRoundRobin(Tournament tournament, List<Team> teams, boolean allerRetour) {
        int n = teams.size();
        List<Team> teamList = new ArrayList<>(teams);

        // Si nombre impair, ajouter un "bye" (null)
        if (n % 2 != 0) {
            teamList.add(null);
            n++;
        }

        int totalRounds = n - 1;
        int matchesPerRound = n / 2;

        LocalDate baseDate = tournament.getDateDebut();
        LocalTime baseTime = LocalTime.of(10, 0);

        List<TournamentMatch> allMatches = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            LocalDate roundDate = baseDate.plusDays(round * 7L);

            for (int match = 0; match < matchesPerRound; match++) {
                Team home = teamList.get(match);
                Team away = teamList.get(n - 1 - match);

                if (home == null || away == null) continue;

                if (matchRepo.findConflictsForTeam(tournament.getId(), roundDate, baseTime, home.getId()).isEmpty() &&
                    matchRepo.findConflictsForTeam(tournament.getId(), roundDate, baseTime, away.getId()).isEmpty()) {

                    TournamentMatch m = TournamentMatch.builder()
                            .tournament(tournament)
                            .equipeDomicile(home)
                            .equipeVisiteur(away)
                            .date(roundDate)
                            .heure(baseTime.plusHours(match))
                            .phase(MatchPhase.POULE)
                            .statut(MatchStatus.PROGRAMME)
                            .tour(round + 1)
                            .build();
                    allMatches.add(m);
                }
            }

            // Rotation circulaire : on fixe la première équipe et on fait tourner les autres
            Team last = teamList.remove(n - 1);
            teamList.add(1, last);
        }

        matchRepo.saveAll(allMatches);

        if (allerRetour) {
            // Générer les matchs retour avec domicile/visiteur inversés
            List<TournamentMatch> retourMatches = new ArrayList<>();
            int retourBase = totalRounds;

            for (TournamentMatch m : allMatches) {
                TournamentMatch retour = TournamentMatch.builder()
                        .tournament(tournament)
                        .equipeDomicile(m.getEquipeVisiteur())
                        .equipeVisiteur(m.getEquipeDomicile())
                        .date(m.getDate().plusDays(totalRounds * 7L))
                        .heure(m.getHeure())
                        .phase(MatchPhase.POULE)
                        .statut(MatchStatus.PROGRAMME)
                        .tour(retourBase + m.getTour())
                        .build();
                retourMatches.add(retour);
            }
            matchRepo.saveAll(retourMatches);
        }
    }

    // --- Élimination directe ---
    private void generateElimination(Tournament tournament, List<Team> teams) {
        int n = teams.size();
        int bracketSize = nextPowerOf2(n);
        int byes = bracketSize - n;

        // Construire le bracket en alternant équipes et byes pour que les byes
        // soient toujours appariés à une vraie équipe (jamais bye vs bye)
        List<Team> bracket = new ArrayList<>(Collections.nCopies(bracketSize, null));
        int byeIdx = 0;
        int teamIdx = 0;
        for (int i = 0; i < bracketSize; i++) {
            if (byeIdx < byes && (i % 2 == 0)) {
                bracket.set(i, null); // bye
                byeIdx++;
            } else if (teamIdx < n) {
                bracket.set(i, teams.get(teamIdx++));
            }
        }

        int totalTours = (int) (Math.log(bracketSize) / Math.log(2));
        LocalDate baseDate = tournament.getDateDebut();
        List<TournamentMatch> allMatches = new ArrayList<>();

        // Map pour fusionner les byes du tour 1 → placeholders du tour 2
        // clé = bracketPosition du tour 2, valeur = match placeholder en cours de construction
        Map<Integer, TournamentMatch> nextRoundPlaceholders = new HashMap<>();

        int matchesInFirstRound = bracketSize / 2;
        MatchPhase firstPhase = determinePhase(totalTours, 1);
        MatchPhase nextPhase = determinePhase(totalTours, 2);

        for (int i = 0; i < matchesInFirstRound; i++) {
            Team home = bracket.get(i * 2);
            Team away = bracket.get(i * 2 + 1);

            if (home != null && away != null) {
                // Match réel : deux équipes
                TournamentMatch m = TournamentMatch.builder()
                        .tournament(tournament)
                        .equipeDomicile(home)
                        .equipeVisiteur(away)
                        .date(baseDate)
                        .heure(LocalTime.of(14 + i, 0))
                        .phase(firstPhase)
                        .statut(MatchStatus.PROGRAMME)
                        .tour(1)
                        .bracketPosition(i)
                        .build();
                allMatches.add(m);
            } else if (home == null && away == null) {
                // Deux byes qui se rencontrent : on saute (pas de match)
                continue;
            } else {
                // Bye : une seule équipe qualifiée pour le tour suivant
                Team qualified = home != null ? home : away;
                int nextBracketPosition = i / 2;

                TournamentMatch existing = nextRoundPlaceholders.get(nextBracketPosition);
                if (existing == null) {
                    // Premier bye vers ce bracketPosition : créer le placeholder
                    TournamentMatch placeholder = TournamentMatch.builder()
                            .tournament(tournament)
                            .equipeDomicile(qualified)
                            .equipeVisiteur(null)
                            .date(baseDate.plusDays(7L))
                            .heure(LocalTime.of(14 + nextBracketPosition, 0))
                            .phase(nextPhase)
                            .statut(MatchStatus.EN_ATTENTE)
                            .tour(2)
                            .bracketPosition(nextBracketPosition)
                            .build();
                    nextRoundPlaceholders.put(nextBracketPosition, placeholder);
                    allMatches.add(placeholder);
                } else {
                    // Deuxième bye vers le même bracketPosition : fusionner
                    existing.setEquipeVisiteur(qualified);
                    existing.setStatut(MatchStatus.PROGRAMME);
                }
            }
        }

        matchRepo.saveAll(allMatches);
    }

    private MatchPhase determinePhase(int totalTours, int tourActuel) {
        int toursRestants = totalTours - tourActuel;
        return switch (toursRestants) {
            case 0 -> MatchPhase.FINALE;
            case 1 -> MatchPhase.DEMI;
            case 2 -> MatchPhase.QUART;
            case 3 -> MatchPhase.HUITIEME;
            default -> MatchPhase.POULE;
        };
    }

    private int nextPowerOf2(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    private void validateStatusTransition(TournamentStatus current, TournamentStatus next) {
        boolean valid = switch (current) {
            case BROUILLON -> next == TournamentStatus.INSCRIPTION || next == TournamentStatus.ANNULE;
            case INSCRIPTION -> next == TournamentStatus.PROGRAMME || next == TournamentStatus.ANNULE;
            case PROGRAMME -> next == TournamentStatus.EN_COURS || next == TournamentStatus.ANNULE;
            case EN_COURS -> next == TournamentStatus.TERMINE || next == TournamentStatus.ANNULE;
            case TERMINE, ANNULE -> false;
        };
        if (!valid) {
            throw new InvalidOperationException(
                    "Transition de " + current + " vers " + next + " non autorisée");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllActive() {
        List<TournamentStatus> activeStatuses = List.of(
                TournamentStatus.BROUILLON, TournamentStatus.INSCRIPTION,
                TournamentStatus.PROGRAMME, TournamentStatus.EN_COURS);
        return tournamentRepo.findByStatutIn(activeStatuses).stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Tournament findTournament(Long id) {
        return tournamentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi", id));
    }
}
