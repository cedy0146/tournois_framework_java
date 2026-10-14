package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.TeamSummaryResponse;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentMapper {

    public TournamentResponse toResponse(Tournament t) {
        long inscrit = t.getTournamentTeams().stream()
                .filter(TournamentTeam::isInscrit)
                .count();
        TournamentResponse.Builder builder = TournamentResponse.builder()
                .id(t.getId())
                .nom(t.getNom())
                .sport(t.getSport())
                .description(t.getDescription())
                .dateDebut(t.getDateDebut())
                .dateFin(t.getDateFin())
                .lieu(t.getLieu())
                .nombreMaxEquipes(t.getNombreMaxEquipes())
                .type(t.getType().name())
                .statut(t.getStatut().name())
                .criteresClassement(t.getCriteresClassement())
                .pointsVictoire(t.getPointsVictoire())
                .pointsNul(t.getPointsNul())
                .pointsDefaite(t.getPointsDefaite())
                .terrains(t.getTerrains())
                .nombreEquipesInscrites((int) inscrit)
                .statusDisplay(getStatusDisplay(t.getStatut()))
                .typeDisplay(getTypeDisplay(t.getType()));
        List<TeamSummaryResponse> teamResponses = t.getTournamentTeams().stream()
                .filter(TournamentTeam::isInscrit)
                .map(tt -> {
                    TeamSummaryResponse team = new TeamSummaryResponse(
                            tt.getTeam().getId(),
                            tt.getTeam().getNom(),
                            tt.getTeam().getVille(),
                            tt.getTeam().getEntraineur());
                    team.setStatut(tt.getInscriptionStatut().name());
                    return team;
                })
                .collect(Collectors.toList());
        builder.registeredTeams(teamResponses);
        return builder.build();
    }

    private String getStatusDisplay(TournamentStatus status) {
        return switch (status) {
            case BROUILLON -> "Brouillon";
            case INSCRIPTION -> "Inscriptions ouvertes";
            case PROGRAMME -> "Programmé";
            case EN_COURS -> "En cours";
            case TERMINE -> "Terminé";
            case ANNULE -> "Annulé";
            default -> status.name();
        };
    }

    private String getTypeDisplay(TournamentType type) {
        return switch (type) {
            case CHAMPIONNAT_SIMPLE -> "Championnat simple";
            case CHAMPIONNAT_ALLER_RETOUR -> "Championnat aller-retour";
            case ELIMINATION_DIRECTE -> "Élimination directe";
            default -> type.name();
        };
    }

    public List<TournamentResponse> toResponseList(List<Tournament> tournaments) {
        return tournaments.stream().map(this::toResponse).toList();
    }
}
