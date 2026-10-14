package com.tpe.tournoi.support;

import com.tpe.tournoi.entity.RegistrationStatus;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.TeamStatus;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentTeam;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.repository.TeamRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.repository.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fabrique de données de test (tournois + équipes) avec noms uniques
 * afin de pouvoir exécuter plusieurs tests dans la même transaction.
 */
public abstract class TestDataFactory {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    protected TournamentRepository tournamentRepo;
    @Autowired
    protected TeamRepository teamRepo;
    @Autowired
    protected TournamentTeamRepository tournamentTeamRepo;

    protected List<Team> createTeams(int count) {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            teams.add(teamRepo.save(Team.builder()
                    .nom("Équipe-" + suffix() + "-" + i)
                    .ville("Ville")
                    .entraineur("Entraîneur")
                    .statut(TeamStatus.ACTIVE)
                    .build()));
        }
        return teams;
    }

    protected Tournament createTournament(TournamentType type, int nbTeams, List<Team> teams) {
        return createTournament(type, nbTeams, teams, "POINTS,DIFFERENCE_BUTS,BUTS_MARQUES");
    }

    protected Tournament createTournament(TournamentType type, int nbTeams, List<Team> teams,
                                          String criteres) {
        Tournament tournament = Tournament.builder()
                .nom("Tournoi-" + suffix())
                .sport("Football")
                .description("Tournoi de test")
                .dateDebut(LocalDate.now().plusDays(1))
                .dateFin(LocalDate.now().plusDays(365))
                .lieu("Stade de test")
                .nombreMaxEquipes(Math.max(2, nbTeams))
                .type(type)
                .statut(com.tpe.tournoi.entity.TournamentStatus.INSCRIPTION)
                .criteresClassement(criteres)
                .terrains("Terrain 1, Terrain 2")
                .build();
        tournament = tournamentRepo.save(tournament);

        int seed = 1;
        for (Team team : teams) {
            tournamentTeamRepo.save(TournamentTeam.builder()
                    .tournament(tournament)
                    .team(team)
                    .inscriptionStatut(RegistrationStatus.INSCRIT)
                    .seed(seed++)
                    .build());
        }
        return tournament;
    }

    protected static String suffix() {
        return Integer.toString(COUNTER.incrementAndGet());
    }
}
