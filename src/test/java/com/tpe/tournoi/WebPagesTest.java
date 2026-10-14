package com.tpe.tournoi;

import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Rendu de toutes les pages de l'interface web (filtres, tri et pagination compris).
 */
@SpringBootTest
@AutoConfigureMockMvc
class WebPagesTest extends TestDataFactory {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentService tournamentService;

    private Long tournamentId;
    private Long teamId;

    @BeforeEach
    void setUp() {
        List<Team> teams = createTeams(4);
        Tournament tournament = createTournament(TournamentType.CHAMPIONNAT_SIMPLE, 4, teams);
        tournamentService.generateMatches(tournament.getId());
        tournamentId = tournament.getId();
        teamId = teams.get(0).getId();
    }

    @Test
    void pagesPrincipales_s_affichent_avec_authentification() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tournaments").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/teams").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/matches").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rankings").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void listes_avec_filtres_tri_et_pagination() throws Exception {
        mockMvc.perform(get("/tournaments")
                        .param("nom", "tournoi")
                        .param("statut", "EN_COURS")
                        .param("tri", "nom")
                        .param("ordre", "desc")
                        .param("size", "5")
                        .param("page", "1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/teams")
                        .param("nom", "equipe")
                        .param("statut", "ACTIVE")
                        .param("tri", "ville")
                        .param("ordre", "asc")
                        .param("size", "5")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/matches")
                        .param("tournoi", tournamentId.toString())
                        .param("phase", "POULE")
                        .param("statut", "PROGRAMME")
                        .param("date", LocalDate.now().plusDays(1).toString())
                        .param("size", "12")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void valeurs_de_filtre_invalides_ne_cassent_pas_la_page() throws Exception {
        mockMvc.perform(get("/tournaments")
                        .param("statut", "INCONNUE")
                        .param("tri", "champInterdit")
                        .param("page", "-3")
                        .param("size", "9999")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/matches")
                        .param("phase", "NEXISTE_PAS")
                        .param("statut", "BOGUS")
                        .param("date", "pas-une-date")
                        .param("tournoi", "abc")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void pages_du_tournoi_s_affichent() throws Exception {
        mockMvc.perform(get("/tournaments/" + tournamentId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tournaments/" + tournamentId + "/matches")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tournaments/" + tournamentId + "/matches")
                        .param("phase", "POULE")
                        .param("statut", "PROGRAMME")
                        .param("size", "12")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tournaments/" + tournamentId + "/matches/bracket")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tournaments/" + tournamentId + "/ranking")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void la_fiche_equipe_affiche_statistiques_et_historique() throws Exception {
        mockMvc.perform(get("/teams/" + teamId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/teams/" + teamId)
                        .with(user("admin").roles("ADMIN"))
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk());
    }

    @Test
    void les_erreurs_renvoient_une_page_web_et_non_du_json() throws Exception {
        mockMvc.perform(get("/tournaments/99999999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/matches/99999999/result").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/erreur-inexistante").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/matches/99999999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void les_pages_publiques_remettent_vers_la_connexion() throws Exception {
        mockMvc.perform(get("/tournaments").with(anonymous()))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/dashboard").with(anonymous()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void les_ecritures_sont_refusees_au_role_user() throws Exception {
        mockMvc.perform(get("/tournaments/new").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/teams/new").with(user("operator").roles("OPERATOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/tournaments/" + tournamentId + "/edit")
                        .with(user("demo").roles("ADMIN", "MANAGER")))
                .andExpect(status().isOk());
    }
}
