package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.TeamResponse;
import com.tpe.tournoi.entity.Team;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team t) {
        return TeamResponse.builder()
                .id(t.getId())
                .nom(t.getNom())
                .logo(t.getLogo())
                .ville(t.getVille())
                .entraineur(t.getEntraineur())
                .statut(t.getStatut().name())
                .nombreJoueurs(t.getJoueurs().size())
                .build();
    }

    public List<TeamResponse> toResponseList(List<Team> teams) {
        return teams.stream().map(this::toResponse).toList();
    }
}
