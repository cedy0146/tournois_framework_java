package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentTeam;
import com.tpe.tournoi.entity.RegistrationStatus;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TournamentMapper {

    public TournamentResponse toResponse(Tournament t) {
        long inscrits = t.getTournamentTeams().stream()
                .filter(TournamentTeam::isInscrit)
                .count();
        return TournamentResponse.builder()
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
                .nombreEquipesInscrites((int) inscrits)
                .build();
    }

    public List<TournamentResponse> toResponseList(List<Tournament> tournaments) {
        return tournaments.stream().map(this::toResponse).toList();
    }
}
