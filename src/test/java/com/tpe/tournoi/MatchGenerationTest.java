package com.tpe.tournoi;

import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentMatch;
import com.tpe.tournoi.entity.TournamentStatus;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.exception.ConflictException;
import com.tpe.tournoi.exception.InvalidOperationException;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MatchGenerationTest extends TestDataFactory {

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentMatchRepository matchRepo;

    @Test
    void championnatSimple_genere_n_multiples_de_2_rencontres() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());
        assertThat(matches).hasSize(6); // n(n-1)/2
        assertPasDeDoublon(matches);
        assertUneEquipeParJournee(matches, 3);
        assertAucunConflitDePlanning(matches);
    }

    @Test
    void championnatAllerRetour_genere_n_fois_n_moins_1_rencontres() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_ALLER_RETOUR, 4, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());
        assertThat(matches).hasSize(12); // n(n-1)
        assertPasDeDoublon(matches);
        assertUneEquipeParJournee(matches, 6);
        assertAucunConflitDePlanning(matches);
    }

    @Test
    void championnat_avec_nombre_impair_de_equipes() {
        List<Team> teams = createTeams(5);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 5, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());
        assertThat(matches).hasSize(10); // 5*4/2
        assertPasDeDoublon(matches);
        assertAucunConflitDePlanning(matches);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 10, 12, 20, 32})
    void elimination_directe_gere_les_tous_nombres(int nbEquipes) {
        List<Team> teams = createTeams(nbEquipes);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, nbEquipes, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());

        // n équipes => n-1 matchs (chaque match élimine une équipe)
        assertThat(matches).hasSize(nbEquipes - 1);

        int bracketSize = 1;
        while (bracketSize < nbEquipes) {
            bracketSize *= 2;
        }
        int toursAttendus = (int) Math.round(Math.log(bracketSize) / Math.log(2));

        // Le nombre de tours est correct et la finale existe
        assertThat(matches.stream().mapToInt(TournamentMatch::getTour).max().orElse(0))
                .isEqualTo(toursAttendus);
        assertThat(matches.stream().filter(m -> m.getTour() == toursAttendus)).hasSize(1);

        // Toutes les équipes sont présentes dans l'arbre
        Set<Long> equipesVues = new HashSet<>();
        for (TournamentMatch m : matches) {
            if (m.getEquipeDomicile() != null) equipesVues.add(m.getEquipeDomicile().getId());
            if (m.getEquipeVisiteur() != null) equipesVues.add(m.getEquipeVisiteur().getId());
        }
        assertThat(equipesVues).hasSize(nbEquipes);

        assertAucunConflitDePlanning(matches);
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.PROGRAMME);
    }

    @Test
    void la_double_generation_est_refusee() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());

        assertThatThrownBy(() -> tournamentService.generateMatches(tournament.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("existe");
    }

    @Test
    void la_generation_echoue_si_le_planning_depasse_la_date_de_fin() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournament.setDateDebut(LocalDate.now());
        tournament.setDateFin(LocalDate.now().plusDays(1));
        tournamentRepo.save(tournament);

        assertThatThrownBy(() -> tournamentService.generateMatches(tournament.getId()))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    void chaque_match_a_un_terrain_renseigne() {
        List<Team> teams = createTeams(6);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, 6, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());
        assertThat(matches).allSatisfy(m -> {
            assertThat(m.getTerrain()).isNotBlank();
            assertThat(m.getDate()).isNotNull();
            assertThat(m.getHeure()).isNotNull();
            assertThat(m.getHeure().getHour()).isBetween(9, 19);
        });
    }

    @Test
    void les_placeholders_des_tours_suivants_sont_creés() {
        List<Team> teams = createTeams(8);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, 8, teams);

        tournamentService.generateMatches(tournament.getId());

        List<TournamentMatch> matches = matchRepo.findByTournamentId(tournament.getId());
        assertThat(matches).hasSize(7);
        // Tour 2 (demi-finales) et tour 3 (finale) créés d'avance
        assertThat(matches.stream().filter(m -> m.getTour() == 2)).hasSize(2);
        assertThat(matches.stream().filter(m -> m.getTour() == 3)).hasSize(1);
        assertThat(matches.stream().filter(m -> m.getTour() > 1).map(TournamentMatch::getStatut))
                .containsOnly(MatchStatus.EN_ATTENTE);
    }

    private void assertPasDeDoublon(List<TournamentMatch> matches) {
        Set<String> rencontres = new HashSet<>();
        for (TournamentMatch m : matches) {
            String rencontre = m.getEquipeDomicile().getId() + "-" + m.getEquipeVisiteur().getId();
            assertThat(rencontres.add(rencontre))
                    .as("Rencontre en double : " + rencontre)
                    .isTrue();
        }
    }

    private void assertUneEquipeParJournee(List<TournamentMatch> matches, int nbJournees) {
        Map<Integer, Map<Long, Integer>> parTour = new HashMap<>();
        for (TournamentMatch m : matches) {
            Map<Long, Integer> compte = parTour.computeIfAbsent(m.getTour(), k -> new HashMap<>());
            compte.merge(m.getEquipeDomicile().getId(), 1, Integer::sum);
            compte.merge(m.getEquipeVisiteur().getId(), 1, Integer::sum);
        }
        assertThat(parTour).hasSize(nbJournees);
        parTour.values().forEach(compte ->
                assertThat(compte.values()).allSatisfy(c -> assertThat(c).isEqualTo(1)));
    }

    private void assertAucunConflitDePlanning(List<TournamentMatch> matches) {
        for (int i = 0; i < matches.size(); i++) {
            TournamentMatch a = matches.get(i);
            for (int j = i + 1; j < matches.size(); j++) {
                TournamentMatch b = matches.get(j);
                if (!a.getDate().equals(b.getDate()) || !a.getHeure().equals(b.getHeure())) {
                    continue;
                }
                Set<Long> equipesA = new HashSet<>();
                if (a.getEquipeDomicile() != null) equipesA.add(a.getEquipeDomicile().getId());
                if (a.getEquipeVisiteur() != null) equipesA.add(a.getEquipeVisiteur().getId());
                Set<Long> equipesB = new HashSet<>();
                if (b.getEquipeDomicile() != null) equipesB.add(b.getEquipeDomicile().getId());
                if (b.getEquipeVisiteur() != null) equipesB.add(b.getEquipeVisiteur().getId());

                for (Long id : equipesA) {
                    assertThat(equipesB)
                            .as("Une équipe joue deux matchs simultanés (matchs %d et %d)"
                                    .formatted(a.getId(), b.getId()))
                            .doesNotContain(id);
                }
                assertThat(a.getTerrain()).isNotEqualTo(b.getTerrain())
                        .as("Un terrain est utilisé deux fois au même créneau");
            }
        }
    }
}
