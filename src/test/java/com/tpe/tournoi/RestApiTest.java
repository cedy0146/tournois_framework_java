package com.tpe.tournoi;

import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.repository.TournamentMatchRepository;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Points d'entrée de l'API REST : pagination, filtres, validations et codes HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RestApiTest extends TestDataFactory {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentMatchRepository matchRepo;

    private Tournament tournament;
    private Long matchId;

    @BeforeEach
    void setUp() {
        List<Team> teams = createTeams(4);
        tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        matchId = matchRepo.findAll().stream()
                .filter(m -> m.getTournament().getId().equals(tournament.getId()))
                .map(com.tpe.tournoi.entity.TournamentMatch::getId)
                .findFirst().orElseThrow();
    }

    @Test
    void liste_des_tournois_est_paginee() throws Exception {
        mockMvc.perform(get("/api/tournaments")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "nom,asc")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void liste_des_tournois_filtre_par_nom_et_statut() throws Exception {
        mockMvc.perform(get("/api/tournaments")
                        .param("nom", tournament.getNom())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nom").value(tournament.getNom()));

        mockMvc.perform(get("/api/tournaments")
                        .param("statut", "STATUT_QUI_NEXISTE_PAS")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void liste_des_matchs_est_paginee_et_filtree() throws Exception {
        mockMvc.perform(get("/api/matches")
                        .param("size", "3")
                        .param("tournamentId", tournament.getId().toString())
                        .param("phase", "POULE")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.totalElements").value(6));
    }

    @Test
    void les_codes_http_sont_corrects() throws Exception {
        mockMvc.perform(get("/api/tournaments/99999999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());

        mockMvc.perform(post("/api/tournaments")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"\",\"sport\":\"\",\"type\":\"CHAMPIONNAT_SIMPLE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        mockMvc.perform(get("/api/matches").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void la_saisie_d_un_resultat_met_a_jour_le_match() throws Exception {
        mockMvc.perform(put("/api/matches/" + matchId + "/result")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scoreDomicile\":2,\"scoreVisiteur\":1}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/matches/" + matchId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreDomicile").value(2))
                .andExpect(jsonPath("$.scoreVisiteur").value(1));
    }

    @Test
    void un_score_negatif_est_rejete() throws Exception {
        mockMvc.perform(put("/api/matches/" + matchId + "/result")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scoreDomicile\":-1,\"scoreVisiteur\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void la_replanification_d_un_match_est_acceptee() throws Exception {
        String date = tournament.getDateDebut().plusDays(10).toString();
        mockMvc.perform(put("/api/matches/" + matchId + "/schedule")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"" + date + "\",\"heure\":\"15:00:00\",\"terrain\":\"Terrain 9\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.terrain").value("Terrain 9"));
    }

    @Test
    void une_date_hors_tournoi_est_rejetee() throws Exception {
        mockMvc.perform(put("/api/matches/" + matchId + "/schedule")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"1999-01-01\",\"heure\":\"15:00:00\",\"terrain\":\"Terrain 9\"}"))
                .andExpect(status().isBadRequest());
    }
}
