package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.PlayerResponse;
import com.tpe.tournoi.entity.Player;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PlayerMapper {

    public PlayerResponse toResponse(Player p) {
        return PlayerResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .poste(p.getPoste().name())
                .numero(p.getNumero())
                .equipeId(p.getEquipe() != null ? p.getEquipe().getId() : null)
                .equipeNom(p.getEquipe() != null ? p.getEquipe().getNom() : null)
                .build();
    }

    public List<PlayerResponse> toResponseList(List<Player> players) {
        return players.stream().map(this::toResponse).toList();
    }
}
