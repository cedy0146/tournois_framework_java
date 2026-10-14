package com.tpe.tournoi;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.request.UpdateMatchScheduleRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentStatus;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.exception.ConflictException;
import com.tpe.tournoi.exception.InvalidOperationException;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MatchFlowTest extends TestDataFactory {

    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentMatchRepository matchRepo;
    @Autowired
    private TournamentRepository tournamentRepo;

    @Test
    void elimination_directe_qualifie_les_vainqueurs_jusqu_a_la_finale() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, 4, teams);
        tournamentService.generateMatches(tournament.getId());

        List<MatchResponse> premiersTours = matchService.getByTournament(tournament.getId()).stream()
                .filter(m -> m.getTour() == 1)
                .toList();
        assertThat(premiersTours).hasSize(2);
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.PROGRAMME);

        for (MatchResponse match : premiersTours) {
            matchService.submitResult(match.getId(), new MatchResultRequest(2, 0));
        }

        MatchResponse finale = matchService.getByTournament(tournament.getId()).stream()
                .filter(m -> m.getTour() == 2)
                .findFirst().orElseThrow();
        assertThat(finale.getEquipeDomicileId()).isNotNull();
        assertThat(finale.getEquipeVisiteurId()).isNotNull();
        assertThat(finale.getStatut()).isEqualTo(MatchStatus.PROGRAMME.name());
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.EN_COURS);

        matchService.submitResult(finale.getId(), new MatchResultRequest(1, 3));
        assertThat(matchService.getById(finale.getId()).getVainqueurId())
                .isEqualTo(finale.getEquipeVisiteurId());
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.TERMINE);
    }

    @Test
    void un_nul_est_interdit_en_elimination_directe() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        Long matchId = matchService.getByTournament(tournament.getId()).get(0).getId();

        assertThatThrownBy(() -> matchService.submitResult(matchId, new MatchResultRequest(1, 1)))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("nul");
    }

    @Test
    void un_resultat_de_tour_anterieur_ne_peut_plus_etre_change() {
        List<Team> teams = createTeams(8);
        Tournament tournament = createTournament(TournamentType.ELIMINATION_DIRECTE, 8, teams);
        tournamentService.generateMatches(tournament.getId());

        List<MatchResponse> tous = matchService.getByTournament(tournament.getId());
        List<MatchResponse> tour1 = tous.stream().filter(m -> m.getTour() == 1).toList();
        assertThat(tour1).hasSize(4);
        for (MatchResponse match : tour1) {
            matchService.submitResult(match.getId(), new MatchResultRequest(2, 0));
        }
        List<MatchResponse> tour2 = matchService.getByTournament(tournament.getId()).stream()
                .filter(m -> m.getTour() == 2).toList();
        assertThat(tour2).hasSize(2);
        for (MatchResponse match : tour2) {
            matchService.submitResult(match.getId(), new MatchResultRequest(2, 0));
        }

        MatchResponse premier = tour1.get(0);
        assertThatThrownBy(() -> matchService.submitResult(premier.getId(), new MatchResultRequest(0, 2)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("match suivant");
    }

    @Test
    void le_championnat_passe_en_cours_puis_termine() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());

        List<MatchResponse> matches = matchService.getByTournament(tournament.getId());
        matchService.submitResult(matches.get(0).getId(), new MatchResultRequest(1, 0));
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.EN_COURS);

        for (int i = 1; i < matches.size(); i++) {
            matchService.submitResult(matches.get(i).getId(), new MatchResultRequest(1, 0));
        }
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(TournamentStatus.TERMINE);
    }

    @Test
    void la_replanification_refuse_les_conflits_de_terrain() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        List<MatchResponse> matches = matchService.getByTournament(tournament.getId());

        MatchResponse cible = matches.get(0);
        MatchResponse concurrent = matches.get(1);

        UpdateMatchScheduleRequest conflit = new UpdateMatchScheduleRequest(
                concurrent.getDate(), concurrent.getHeure(), concurrent.getTerrain());

        assertThatThrownBy(() -> matchService.updateSchedule(cible.getId(), conflit))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void la_replanification_refuse_une_date_hors_tournoi() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        Long matchId = matchService.getByTournament(tournament.getId()).get(0).getId();

        assertThatThrownBy(() -> matchService.updateSchedule(matchId,
                new UpdateMatchScheduleRequest(tournament.getDateFin().plusDays(5),
                        LocalTime.of(10, 0), "Terrain 1")))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("comprise entre");
    }

    @Test
    void la_replanification_valide_change_date_terrain_et_heure() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        MatchResponse cible = matchService.getByTournament(tournament.getId()).get(0);
        MatchResponse autre = matchService.getByTournament(tournament.getId()).get(1);

        LocalDate nouvelleDate = autre.getDate();
        LocalTime nouvelleHeure = LocalTime.of(18, 0);
        String nouveauTerrain = "Terrain spécial";

        MatchResponse apres = matchService.updateSchedule(cible.getId(),
                new UpdateMatchScheduleRequest(nouvelleDate, nouvelleHeure, nouveauTerrain));

        assertThat(apres.getDate()).isEqualTo(nouvelleDate);
        assertThat(apres.getHeure()).isEqualTo(nouvelleHeure);
        assertThat(apres.getTerrain()).isEqualTo(nouveauTerrain);
    }
}
