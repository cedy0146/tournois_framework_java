package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.TournamentMatch;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MatchMapper {

    public MatchResponse toResponse(TournamentMatch m) {
        return MatchResponse.builder()
                .id(m.getId())
                .tournamentId(m.getTournament().getId())
                .tournamentNom(m.getTournament().getNom())
                .equipeDomicileId(m.getEquipeDomicile() != null ? m.getEquipeDomicile().getId() : null)
                .equipeDomicileNom(m.getEquipeDomicile() != null ? m.getEquipeDomicile().getNom() : null)
                .equipeVisiteurId(m.getEquipeVisiteur() != null ? m.getEquipeVisiteur().getId() : null)
                .equipeVisiteurNom(m.getEquipeVisiteur() != null ? m.getEquipeVisiteur().getNom() : null)
                .date(m.getDate())
                .heure(m.getHeure())
                .terrain(m.getTerrain())
                .phase(m.getPhase().name())
                .statut(m.getStatut().name())
                .scoreDomicile(m.getScoreDomicile())
                .scoreVisiteur(m.getScoreVisiteur())
                .vainqueurId(m.getVainqueur() != null ? m.getVainqueur().getId() : null)
                .vainqueurNom(m.getVainqueur() != null ? m.getVainqueur().getNom() : null)
                .tour(m.getTour())
                .bracketPosition(m.getBracketPosition())
                .build();
    }

    public List<MatchResponse> toResponseList(List<TournamentMatch> matches) {
        return matches.stream().map(this::toResponse).toList();
    }
}
