package com.tpe.tournoi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Authentification à partir des comptes stockés en base (table APP_USER)
 * et contrôle des rôles sur l'API comme sur l'interface.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void connexion_reussie_avec_un_compte_de_la_base() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("admin123"))
                .andExpect(authenticated().withUsername("admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void connexion_echoue_avec_un_mauvais_mot_de_passe() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("mauvais"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void connexion_echoue_avec_un_utilisateur_inconnu() throws Exception {
        mockMvc.perform(formLogin().user("ghost").password("nimportequoi"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void les_pages_privees_redirigent_vers_la_connexion() throws Exception {
        mockMvc.perform(get("/dashboard").with(anonymous()))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/tournaments").with(anonymous()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void l_api_renvoie_des_donnees_aux_utilisateurs_connectes() throws Exception {
        mockMvc.perform(get("/api/tournaments").with(user("user").roles("USER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/teams").with(user("operator").roles("OPERATOR")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/matches").with(user("demo").roles("ADMIN", "MANAGER", "OPERATOR")))
                .andExpect(status().isOk());
    }

    @Test
    void les_ecritures_sont_reservees_aux_roles_habiles() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .with(user("user").roles("USER"))
                        .contentType("application/json")
                        .content("{\"nom\":\"Équipe test\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/teams")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType("application/json")
                        .content("{\"nom\":\"Équipe test\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/teams")
                        .with(user("manager").roles("MANAGER"))
                        .contentType("application/json")
                        .content("{\"nom\":\"Équipe test\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void la_saisie_de_resultat_est_reservee_aux_operateurs() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .with(user("user").roles("USER"))
                        .contentType("application/json")
                        .content("{\"nom\":\"Tournoi test\",\"sport\":\"Football\","
                                + "\"dateDebut\":\"2030-01-01\",\"dateFin\":\"2030-02-01\","
                                + "\"type\":\"CHAMPIONNAT_SIMPLE\",\"nombreMaxEquipes\":4}"))
                .andExpect(status().isForbidden());
    }
}
