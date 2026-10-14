package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.CreateTournamentRequest;
import com.tpe.tournoi.dto.request.UpdateTournamentRequest;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.TournamentMapper;
import com.tpe.tournoi.repository.*;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.util.BracketUtil;
import com.tpe.tournoi.util.MatchScheduler;
import com.tpe.tournoi.util.RankingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final TournamentMatchRepository matchRepo;
    private final RankingRepository rankingRepo;
    private final TournamentMapper mapper;

    public TournamentServiceImpl(TournamentRepository tournamentRepo, TeamRepository teamRepo,
                                 TournamentTeamRepository tournamentTeamRepo, TournamentMatchRepository matchRepo,
                                 RankingRepository rankingRepo, TournamentMapper mapper) {
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
        this.tournamentTeamRepo = tournamentTeamRepo;
        this.matchRepo = matchRepo;
        this.rankingRepo = rankingRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TournamentResponse create(CreateTournamentRequest request) {
        validateDates(request.getDateDebut(), request.getDateFin());
        if (request.getNombreMaxEquipes() < 2) {
            throw new InvalidOperationException("Le nombre maximum d'équipes doit être au moins 2");
        }

        TournamentType type;
        try {
            type = TournamentType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Type de tournoi invalide: " + request.getType());
        }

        String criteres = resolveCriteres(request.getCriteresClassement(),
                request.getCritere1(), request.getCritere2(), request.getCritere3(), request.getCritere4());
        if (criteres == null) {
            criteres = RankingUtil.DEFAULT_CRITERES;
        }
        RankingUtil.validate(criteres);

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
                .pointsVictoire(request.getPointsVictoire())
                .pointsNul(request.getPointsNul())
                .pointsDefaite(request.getPointsDefaite())
                .criteresClassement(criteres)
                .terrains(request.getTerrains())
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

        LocalDate nouvelleDateDebut = request.getDateDebut() != null ? request.getDateDebut() : t.getDateDebut();
        LocalDate nouvelleDateFin = request.getDateFin() != null ? request.getDateFin() : t.getDateFin();
        validateDates(nouvelleDateDebut, nouvelleDateFin);

        if (request.getNombreMaxEquipes() != null && request.getNombreMaxEquipes() > 0) {
            if (request.getNombreMaxEquipes() < 2) {
                throw new InvalidOperationException("Le nombre maximum d'équipes doit être au moins 2");
            }
            t.setNombreMaxEquipes(request.getNombreMaxEquipes());
        }

        if (request.getNom() != null) t.setNom(request.getNom());
        if (request.getSport() != null) t.setSport(request.getSport());
        if (request.getDescription() != null) t.setDescription(request.getDescription());
        if (request.getLieu() != null) t.setLieu(request.getLieu());
        t.setDateDebut(nouvelleDateDebut);
        t.setDateFin(nouvelleDateFin);

        if (request.getPointsVictoire() != null) t.setPointsVictoire(request.getPointsVictoire());
        if (request.getPointsNul() != null) t.setPointsNul(request.getPointsNul());
        if (request.getPointsDefaite() != null) t.setPointsDefaite(request.getPointsDefaite());
        if (request.getTerrains() != null) t.setTerrains(request.getTerrains());

        String criteres = resolveCriteres(request.getCriteresClassement(),
                request.getCritere1(), request.getCritere2(), request.getCritere3(), request.getCritere4());
        if (criteres != null && !criteres.equals(t.getCriteresClassement())) {
            RankingUtil.validate(criteres);
            t.setCriteresClassement(criteres);
        }

        if (request.getStatut() != null) {
            TournamentStatus newStatus;
            try {
                newStatus = TournamentStatus.valueOf(request.getStatut());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Statut invalide: " + request.getStatut());
            }
            validateStatusTransition(t.getStatut(), newStatus);
            t.setStatut(newStatus);
        }

        t = tournamentRepo.save(t);
        return mapper.toResponse(t);
    }

    private void validateDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new BusinessException("La date de début et la date de fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new InvalidOperationException("La date de fin ne peut pas être avant la date de début");
        }
    }

    /**
     * Résout la chaîne des critères de classement : soit la chaîne complète fournie
     * par l'API, soit les critères choisis et ordonnés dans le formulaire.
     *
     * @return la chaîne à utiliser, ou null si aucun critère n'est fourni (conserve l'existant)
     */
    private String resolveCriteres(String criteres, String critere1, String critere2,
                                   String critere3, String critere4) {
        if (criteres != null && !criteres.isBlank()) {
            return criteres.trim();
        }
        StringBuilder sb = new StringBuilder();
        for (String critere : new String[]{critere1, critere2, critere3, critere4}) {
            if (critere != null && !critere.isBlank()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(critere.trim());
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tournament t = findTournament(id);
        if (t.getStatut() == TournamentStatus.EN_COURS) {
            throw new InvalidOperationException("Impossible de supprimer un tournoi en cours");
        }
        // Les classements ne sont pas une collection en cascade sur Tournament.
        // Ils doivent donc être supprimés explicitement avant le tournoi.
        rankingRepo.deleteByTournamentId(id);
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

        MatchScheduler scheduler = new MatchScheduler(t.getDateDebut(), t.getTerrains());

        List<TournamentMatch> allMatches = switch (t.getType()) {
            case CHAMPIONNAT_SIMPLE -> genererRoundRobin(t, teams, false, scheduler);
            case CHAMPIONNAT_ALLER_RETOUR -> genererRoundRobin(t, teams, true, scheduler);
            case ELIMINATION_DIRECTE -> genererElimination(t, teams, scheduler);
        };

        verifierPlanning(allMatches, t.getDateFin());
        matchRepo.saveAll(allMatches);

        validateStatusTransition(t.getStatut(), TournamentStatus.PROGRAMME);
        t.setStatut(TournamentStatus.PROGRAMME);
        tournamentRepo.save(t);
    }

    /**
     * Vérifie qu'aucun conflit de planning n'existe et que le calendrier
     * tient dans les dates du tournoi.
     */
    private void verifierPlanning(List<TournamentMatch> matches, LocalDate dateFin) {
        MatchScheduler.verifierConflits(matches);
        matches.stream()
                .map(TournamentMatch::getDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .filter(derniere -> dateFin != null && derniere.isAfter(dateFin))
                .ifPresent(derniere -> {
                    throw new InvalidOperationException(
                            "Le planning généré dépasserait la date de fin du tournoi ("
                                    + dateFin + ") : dernier match le " + derniere
                                    + ". Prolongez la date de fin du tournoi.");
                });
    }

    // --- Round-Robin : Algorithme du cercle (Circle Method) ---
    private List<TournamentMatch> genererRoundRobin(Tournament tournament, List<Team> teams,
                                                    boolean allerRetour, MatchScheduler scheduler) {
        int n = teams.size();
        List<Team> teamList = new ArrayList<>(teams);

        // Si nombre impair, ajouter un "bye" (null)
        if (n % 2 != 0) {
            teamList.add(null);
            n++;
        }

        int demiSaison = n - 1;
        int totalRounds = allerRetour ? 2 * demiSaison : demiSaison;
        int matchesPerRound = n / 2;

        LocalDate baseDate = tournament.getDateDebut();
        List<TournamentMatch> allMatches = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            LocalDate roundDate = baseDate.plusDays(round * 7L);
            boolean retour = round >= demiSaison;

            for (int match = 0; match < matchesPerRound; match++) {
                Team home = teamList.get(match);
                Team away = teamList.get(n - 1 - match);

                if (home == null || away == null) continue;

                // En aller-retour, la seconde demi-saison inverse domicile/visiteur
                Team domicile = retour ? away : home;
                Team visiteur = retour ? home : away;

                MatchScheduler.Slot slot = scheduler.next(roundDate);

                allMatches.add(TournamentMatch.builder()
                        .tournament(tournament)
                        .equipeDomicile(domicile)
                        .equipeVisiteur(visiteur)
                        .date(slot.date())
                        .heure(slot.heure())
                        .terrain(slot.terrain())
                        .phase(MatchPhase.POULE)
                        .statut(MatchStatus.PROGRAMME)
                        .tour(round + 1)
                        .build());
            }

            // Rotation circulaire : on fixe la première équipe et on fait tourner les autres
            Team last = teamList.remove(n - 1);
            teamList.add(1, last);
        }

        return allMatches;
    }

    // --- Élimination directe ---
    private List<TournamentMatch> genererElimination(Tournament tournament, List<Team> teams,
                                                     MatchScheduler scheduler) {
        int n = teams.size();
        int bracketSize = BracketUtil.nextPowerOf2(n);
        int byes = bracketSize - n;
        int totalTours = BracketUtil.totalTours(bracketSize);
        LocalDate baseDate = tournament.getDateDebut();

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

        List<TournamentMatch> allMatches = new ArrayList<>();
        List<QualificationBye> qualifications = new ArrayList<>();

        // 1. Tour 1 : matchs réels + collecte des exemptions (byes)
        int matchesInFirstRound = bracketSize / 2;
        MatchPhase firstPhase = BracketUtil.determinePhase(totalTours, 1);

        for (int i = 0; i < matchesInFirstRound; i++) {
            Team home = bracket.get(i * 2);
            Team away = bracket.get(i * 2 + 1);

            if (home != null && away != null) {
                MatchScheduler.Slot slot = scheduler.next(baseDate);
                allMatches.add(TournamentMatch.builder()
                        .tournament(tournament)
                        .equipeDomicile(home)
                        .equipeVisiteur(away)
                        .date(slot.date())
                        .heure(slot.heure())
                        .terrain(slot.terrain())
                        .phase(firstPhase)
                        .statut(MatchStatus.PROGRAMME)
                        .tour(1)
                        .bracketPosition(i)
                        .build());
            } else if (home != null || away != null) {
                // Bye : une seule équipe qualifiée d'office pour le tour suivant
                qualifications.add(new QualificationBye(i / 2, home != null ? home : away));
            }
        }

        // 2. Placeholders des tours 2 à finale : un match par position de bracket
        for (int tour = 2; tour <= totalTours; tour++) {
            int nbMatchs = bracketSize / (1 << tour);
            MatchPhase phase = BracketUtil.determinePhase(totalTours, tour);
            LocalDate dateTour = baseDate.plusDays((tour - 1) * 7L);
            for (int position = 0; position < nbMatchs; position++) {
                MatchScheduler.Slot slot = scheduler.next(dateTour);
                allMatches.add(TournamentMatch.builder()
                        .tournament(tournament)
                        .date(slot.date())
                        .heure(slot.heure())
                        .terrain(slot.terrain())
                        .phase(phase)
                        .statut(MatchStatus.EN_ATTENTE)
                        .tour(tour)
                        .bracketPosition(position)
                        .build());
            }
        }

        // 3. Affectation des exemptions dans les matchs du tour suivant
        for (QualificationBye qualification : qualifications) {
            TournamentMatch suivant = allMatches.stream()
                    .filter(m -> m.getTour() == 2 && m.getBracketPosition() != null
                            && m.getBracketPosition() == qualification.position())
                    .findFirst()
                    .orElseThrow(() -> new ConflictException(
                            "Impossible de placer l'exemption de l'équipe '"
                                    + qualification.equipe().getNom() + "'"));
            if (suivant.getEquipeDomicile() == null) {
                suivant.setEquipeDomicile(qualification.equipe());
            } else if (suivant.getEquipeVisiteur() == null) {
                suivant.setEquipeVisiteur(qualification.equipe());
            }
            if (suivant.getEquipeDomicile() != null && suivant.getEquipeVisiteur() != null) {
                suivant.setStatut(MatchStatus.PROGRAMME);
            }
        }

        return allMatches;
    }

    private record QualificationBye(int position, Team equipe) {
    }

    private void validateStatusTransition(TournamentStatus current, TournamentStatus next) {
        boolean valid = switch (current) {
            case BROUILLON -> next == TournamentStatus.INSCRIPTION || next == TournamentStatus.PROGRAMME
                    || next == TournamentStatus.ANNULE;
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

    @Override
    @Transactional
    public void startTournament(Long id) {
        Tournament t = findTournament(id);
        if (t.getStatut() != TournamentStatus.PROGRAMME) {
            throw new InvalidOperationException("Démarrage impossible: tournoi au statut " + t.getStatut());
        }
        long total = matchRepo.countByTournamentId(t.getId());
        if (total < 1) {
            throw new InvalidOperationException("Impossible de démarrer: aucun match généré");
        }
        t.setStatut(TournamentStatus.EN_COURS);
        tournamentRepo.save(t);
    }

    @Override
    @Transactional
    public void cancelTournament(Long id) {
        Tournament t = findTournament(id);
        validateStatusTransition(t.getStatut(), TournamentStatus.ANNULE);
        t.setStatut(TournamentStatus.ANNULE);
        tournamentRepo.save(t);
    }

    @Override
    public List<TournamentResponse> getRegisteredTeams(Long tournamentId) {
        return tournamentTeamRepo.findByTournamentIdAndInscriptionStatut(tournamentId, RegistrationStatus.INSCRIT)
                .stream()
                .map(tt -> {
                    TournamentResponse tr = TournamentResponse.builder()
                            .id(tt.getTeam().getId())
                            .nom(tt.getTeam().getNom())
                            .build();
                    return tr;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TournamentStatus openRegistration(Long id) {
        TournamentResponse tournament = getById(id);
        if (tournament.getStatut().equals(TournamentStatus.BROUILLON.name())) {
            tournament.setStatut(TournamentStatus.INSCRIPTION.name());
            // Mise à jour en BDD
            Tournament t = tournamentRepo.findById(id).orElseThrow();
            t.setStatut(TournamentStatus.INSCRIPTION);
            tournamentRepo.save(t);
            return TournamentStatus.INSCRIPTION;
        }
        throw new InvalidOperationException("Impossible d'ouvrir les inscriptions: tournoi au statut " + tournament.getStatut());
    }

    @Override
    public String getStatusDisplay(TournamentStatus status) {
        return switch (status) {
            case BROUILLON -> "Brouillon";
            case INSCRIPTION -> "Inscriptions ouvertes";
            case PROGRAMME -> "Programmé";
            case EN_COURS -> "En cours";
            case TERMINE -> "Terminé";
            case ANNULE -> "Annulé";
        };
    }

    @Override
    public String getTypeDisplay(TournamentType type) {
        return switch (type) {
            case CHAMPIONNAT_SIMPLE -> "Championnat simple";
            case CHAMPIONNAT_ALLER_RETOUR -> "Championnat aller-retour";
            case ELIMINATION_DIRECTE -> "Élimination directe";
            default -> type.name();
        };
    }
}
