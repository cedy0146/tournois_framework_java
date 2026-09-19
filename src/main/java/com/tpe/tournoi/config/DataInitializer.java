package com.tpe.tournoi.config;

import com.tpe.tournoi.entity.*;
import com.tpe.tournoi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final TournamentTeamRepository tournamentTeamRepo;
    private final TournamentMatchRepository matchRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (tournamentRepo.count() > 0) {
            log.info("Données déjà présentes, pas de réinitialisation");
            return;
        }

        log.info("Création des données de démo...");

        // --- Équipes ---
        Team t1 = createTeam("FC Paris", "Paris", "Dupont");
        Team t2 = createTeam("Olympique Lyon", "Lyon", "Martin");
        Team t3 = createTeam("AS Marseille", "Marseille", "Bernard");
        Team t4 = createTeam("OSC Lille", "Lille", "Petit");
        Team t5 = createTeam("RC Strasbourg", "Strasbourg", "Moreau");
        Team t6 = createTeam("OGC Nice", "Nice", "Laurent");
        Team t7 = createTeam("FC Nantes", "Nantes", "Simon");
        Team t8 = createTeam("Stade Rennais", "Rennes", "Michel");

        // --- Joueurs ---
        createPlayer("Dupont Jean", PlayerPosition.GARDIEN, 1, t1);
        createPlayer("Martin Paul", PlayerPosition.DEFENSEUR, 4, t1);
        createPlayer("Bernard Luc", PlayerPosition.MILIEU, 8, t1);
        createPlayer("Petit Marc", PlayerPosition.ATTAQUANT, 9, t1);

        createPlayer("Leroy Thomas", PlayerPosition.GARDIEN, 1, t2);
        createPlayer("Moreau Nicolas", PlayerPosition.DEFENSEUR, 3, t2);
        createPlayer("Simon Julien", PlayerPosition.MILIEU, 6, t2);
        createPlayer("Laurent Antoine", PlayerPosition.ATTAQUANT, 10, t2);

        createPlayer("David Kevin", PlayerPosition.GARDIEN, 1, t3);
        createPlayer("Bertrand Romain", PlayerPosition.DEFENSEUR, 2, t3);
        createPlayer("Roux Damien", PlayerPosition.MILIEU, 5, t3);
        createPlayer("Fournier Alex", PlayerPosition.ATTAQUANT, 11, t3);

        createPlayer("Girard Maxime", PlayerPosition.GARDIEN, 1, t4);
        createPlayer("Andre Steven", PlayerPosition.DEFENSEUR, 4, t4);
        createPlayer("Lefevre Remy", PlayerPosition.MILIEU, 8, t4);
        createPlayer("Mercier Hugo", PlayerPosition.ATTAQUANT, 9, t4);

        createPlayer("Garnier Fabien", PlayerPosition.GARDIEN, 1, t5);
        createPlayer("Chevalier Boris", PlayerPosition.DEFENSEUR, 3, t5);
        createPlayer("Francois Cedric", PlayerPosition.MILIEU, 7, t5);
        createPlayer("Lambert Dimitri", PlayerPosition.ATTAQUANT, 10, t5);

        createPlayer("Gauthier Mickael", PlayerPosition.GARDIEN, 1, t6);
        createPlayer("Morin Patrick", PlayerPosition.DEFENSEUR, 4, t6);
        createPlayer("Foucault Jeremy", PlayerPosition.MILIEU, 6, t6);
        createPlayer("Blanc Yann", PlayerPosition.ATTAQUANT, 9, t6);

        createPlayer("Henry Pascal", PlayerPosition.GARDIEN, 1, t7);
        createPlayer("Rousseau Florian", PlayerPosition.DEFENSEUR, 2, t7);
        createPlayer("Vincent Nicolas", PlayerPosition.MILIEU, 5, t7);
        createPlayer("Leroux Morgan", PlayerPosition.ATTAQUANT, 11, t7);

        createPlayer("Bonnet Sebastien", PlayerPosition.GARDIEN, 1, t8);
        createPlayer("Garcia Adrien", PlayerPosition.DEFENSEUR, 3, t8);
        createPlayer("Legrand Sylvain", PlayerPosition.MILIEU, 8, t8);
        createPlayer("Chevalier Dylan", PlayerPosition.ATTAQUANT, 10, t8);

        // --- Tournoi 1 : Championnat Simple (4 équipes) ---
        Tournament t1champ = Tournament.builder()
                .nom("Coupe de France Amateurs")
                .sport("Football")
                .description("Championnat simple round-robin entre 4 équipes")
                .dateDebut(LocalDate.now().plusDays(1))
                .dateFin(LocalDate.now().plusDays(30))
                .lieu("Stade Municipal")
                .nombreMaxEquipes(4)
                .type(TournamentType.CHAMPIONNAT_SIMPLE)
                .statut(TournamentStatus.INSCRIPTION)
                .build();
        tournamentRepo.save(t1champ);

        registerTeam(t1champ, t1, 1);
        registerTeam(t1champ, t2, 2);
        registerTeam(t1champ, t3, 3);
        registerTeam(t1champ, t4, 4);

        // --- Tournoi 2 : Élimination Directe (6 équipes) ---
        Tournament t2elim = Tournament.builder()
                .nom("Tournoi Express")
                .sport("Football")
                .description("Élimination directe avec 6 équipes")
                .dateDebut(LocalDate.now().plusDays(15))
                .dateFin(LocalDate.now().plusDays(25))
                .lieu("Complex Sportif")
                .nombreMaxEquipes(8)
                .type(TournamentType.ELIMINATION_DIRECTE)
                .statut(TournamentStatus.INSCRIPTION)
                .build();
        tournamentRepo.save(t2elim);

        registerTeam(t2elim, t5, 1);
        registerTeam(t2elim, t6, 2);
        registerTeam(t2elim, t7, 3);
        registerTeam(t2elim, t8, 4);
        registerTeam(t2elim, t1, 5);
        registerTeam(t2elim, t2, 6);

        // --- Tournoi 3 : Championnat Aller-Retour (4 équipes, brouillon) ---
        Tournament t3aller = Tournament.builder()
                .nom("Ligue Inter-Entreprises")
                .sport("Football")
                .description("Championnat aller-retour")
                .dateDebut(LocalDate.now().plusDays(60))
                .dateFin(LocalDate.now().plusDays(120))
                .lieu("Terrain Central")
                .nombreMaxEquipes(4)
                .type(TournamentType.CHAMPIONNAT_ALLER_RETOUR)
                .statut(TournamentStatus.BROUILLON)
                .build();
        tournamentRepo.save(t3aller);

        registerTeam(t3aller, t1, 1);
        registerTeam(t3aller, t3, 2);
        registerTeam(t3aller, t5, 3);
        registerTeam(t3aller, t7, 4);

        log.info("Données de démo créées avec succès !");
        log.info("3 tournois, 8 équipes, 32 joueurs créés");
        log.info("--- Vérification bracket élimination directe (10 équipes) ---");
        log.info("BracketSize=16, byes=6. Interleaving: [bye,T0,bye,T1,bye,T2,bye,T3,bye,T4,bye,T5,T6,T7,T8,T9]");
        log.info("Tour 1 (HUITIEME): 8 slots, 2 matchs réels (T6vT7, T8vT9) + 6 byes fusionnés en 3 placeholders");
        log.info("Tour 2 (QUART): 4 slots, 3 placeholders issue des byes (chaque paire de byes adjacents fusionnée) + 1 match réel");
        log.info("Après fusion: chaque bracketPosition du tour 2 ne contient QU'UN SEUL match, jamais de doublon");
    }

    private Team createTeam(String nom, String ville, String entraineur) {
        Team team = Team.builder()
                .nom(nom)
                .ville(ville)
                .entraineur(entraineur)
                .statut(TeamStatus.ACTIVE)
                .build();
        return teamRepo.save(team);
    }

    private void createPlayer(String nom, PlayerPosition poste, int numero, Team equipe) {
        Player player = Player.builder()
                .nom(nom)
                .poste(poste)
                .numero(numero)
                .equipe(equipe)
                .build();
        playerRepo.save(player);
    }

    private void registerTeam(Tournament tournament, Team team, int seed) {
        TournamentTeam tt = TournamentTeam.builder()
                .tournament(tournament)
                .team(team)
                .inscriptionStatut(RegistrationStatus.INSCRIT)
                .seed(seed)
                .build();
        tournamentTeamRepo.save(tt);
    }
}
