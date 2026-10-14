package com.tpe.tournoi;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.dto.response.RankingResponse;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.exception.InvalidOperationException;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.repository.TournamentRepository;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.RankingService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.util.RankingUtil;
import com.tpe.tournoi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RankingTest extends TestDataFactory {

    @Autowired
    private MatchService matchService;
    @Autowired
    private RankingService rankingService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentMatchRepository matchRepo;
    @Autowired
    private TournamentRepository tournamentRepo;

    private Tournament tournament;
    private Team a;
    private Team b;
    private Team c;
    private Team d;

    @BeforeEach
    void setUp() {
        List<Team> teams = createTeams(4);
        a = teams.get(0);
        b = teams.get(1);
        c = teams.get(2);
        d = teams.get(3);
        tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams,
                "POINTS,DIFFERENCE_BUTS,BUTS_MARQUES");
        tournamentService.generateMatches(tournament.getId());

        // A : 9 pts (+3)   B : 6 pts (+9)   C : 3 pts (-4)   D : 0 pts (-9)
        jouer(a, b, 1, 0);
        jouer(a, c, 1, 0);
        jouer(a, d, 1, 0);
        jouer(b, c, 5, 0);
        jouer(b, d, 5, 0);
        jouer(c, d, 2, 0);
    }

    @Test
    void classement_par_defaut_ordonne_par_points() {
        List<RankingResponse> classement = rankingService.getRanking(tournament.getId());

        assertThat(classement).extracting(RankingResponse::getTeamId)
                .containsExactly(a.getId(), b.getId(), c.getId(), d.getId());
        assertThat(classement).extracting(RankingResponse::getPoints)
                .containsExactly(9, 6, 3, 0);
        assertThat(classement).extracting(RankingResponse::getClassement)
                .containsExactly(1, 2, 3, 4);
    }

    @Test
    void le_critere_principal_configurable_change_l_ordre() {
        // B a la meilleure différence de buts (+9 contre +3)
        tournament.setCriteresClassement("DIFFERENCE_BUTS,POINTS,BUTS_MARQUES");
        tournamentRepo.save(tournament);

        List<RankingResponse> classement = rankingService.getRanking(tournament.getId());

        assertThat(classement).extracting(RankingResponse::getTeamId)
                .containsExactly(b.getId(), a.getId(), c.getId(), d.getId());
        assertThat(classement.get(0).getDiffButs()).isGreaterThan(classement.get(1).getDiffButs());
    }

    @Test
    void les_statistiques_sont_recalculees_a_chaque_resultat() {
        RankingResponse premier = rankingService.getRanking(tournament.getId()).get(0);
        assertThat(premier.getMatchsJoues()).isEqualTo(3);
        assertThat(premier.getVictoires()).isEqualTo(3);
        assertThat(premier.getButsMarques()).isEqualTo(3);
        assertThat(premier.getButsEncaisses()).isZero();
    }

    @Test
    void les_matchs_sont_termines_et_les_statuts_mis_a_jour() {
        assertThat(matchRepo.findByTournamentId(tournament.getId()))
                .allSatisfy(m -> assertThat(m.getStatut())
                        .isEqualTo(com.tpe.tournoi.entity.MatchStatus.TERMINE));
        assertThat(tournamentRepo.findById(tournament.getId()).orElseThrow().getStatut())
                .isEqualTo(com.tpe.tournoi.entity.TournamentStatus.TERMINE);
    }

    @Test
    void un_critere_inconnu_est_refuse_a_la_creation() {
        assertThatThrownBy(() -> RankingUtil.parse("POINTS,CANDIDAT_OR"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("inconnu");
    }

    @Test
    void un_critere_en_double_est_refuse() {
        assertThatThrownBy(() -> RankingUtil.parse("POINTS,POINTS,DIFFERENCE_BUTS"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("double");
    }

    @Test
    void un_tournoi_sans_critere_utilise_les_defauts() {
        assertThat(RankingUtil.parse(null)).isEqualTo(RankingUtil.defaultCriteres());
        assertThat(RankingUtil.parse("")).isEqualTo(RankingUtil.defaultCriteres());
        assertThat(RankingUtil.parse("POINTS,DIFFERENCE_BUTS,BUTS_MARQUES"))
                .isEqualTo(RankingUtil.defaultCriteres());
    }

    /** Saisit le résultat d'une rencontre quelle que soit son orientation. */
    private void jouer(Team domicile, Team visiteur, int scoreDomicile, int scoreVisiteur) {
        MatchResponse match = matchRepo.findByTournamentId(tournament.getId()).stream()
                .filter(m -> m.getEquipeDomicile() != null && m.getEquipeVisiteur() != null)
                .filter(m -> m.getEquipeDomicile().getId().equals(domicile.getId())
                        && m.getEquipeVisiteur().getId().equals(visiteur.getId()))
                .map(m -> matchService.getById(m.getId()))
                .findFirst()
                .orElseGet(() -> matchRepo.findByTournamentId(tournament.getId()).stream()
                        .filter(m -> m.getEquipeDomicile() != null && m.getEquipeVisiteur() != null)
                        .filter(m -> m.getEquipeDomicile().getId().equals(visiteur.getId())
                                && m.getEquipeVisiteur().getId().equals(domicile.getId()))
                        .map(m -> matchService.getById(m.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(
                                "Match introuvable entre " + domicile.getNom() + " et " + visiteur.getNom())));

        boolean domicileChezMoi = match.getEquipeDomicileId().equals(domicile.getId());
        matchService.submitResult(match.getId(), new MatchResultRequest(
                domicileChezMoi ? scoreDomicile : scoreVisiteur,
                domicileChezMoi ? scoreVisiteur : scoreDomicile));
    }
}
