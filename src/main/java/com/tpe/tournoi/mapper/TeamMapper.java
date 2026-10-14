package com.tpe.tournoi.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tpe.tournoi.dto.response.PlayerResponse;
import com.tpe.tournoi.dto.response.TeamResponse;
import com.tpe.tournoi.entity.Team;

@Component
public class TeamMapper {

    private final PlayerMapper playerMapper;

    public TeamMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    public TeamResponse toResponse(Team t) {
        List<PlayerResponse> joueurs = t.getJoueurs() == null
                ? List.of()
                : playerMapper.toResponseList(t.getJoueurs());

        return TeamResponse.builder()
                .id(t.getId())
                .nom(t.getNom())
                .logo(t.getLogo())
                .ville(t.getVille())
                .entraineur(t.getEntraineur())
                .statut(t.getStatut().name())
                .nombreJoueurs(joueurs.size())
                .joueurs(joueurs)
                .build();
    }

    public List<TeamResponse> toResponseList(List<Team> teams) {
        return teams.stream().map(this::toResponse).toList();
    }
}
