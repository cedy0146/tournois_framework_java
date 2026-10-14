package com.tpe.tournoi.mapper;

import com.tpe.tournoi.dto.response.RankingResponse;
import com.tpe.tournoi.entity.Ranking;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RankingMapper {

    public RankingResponse toResponse(Ranking r) {
        return RankingResponse.builder()
                .classement(r.getClassement())
                .tournamentId(r.getTournament() != null ? r.getTournament().getId() : null)
                .tournamentNom(r.getTournament() != null ? r.getTournament().getNom() : null)
                .teamId(r.getTeam().getId())
                .teamNom(r.getTeam().getNom())
                .teamInitiales(initiales(r.getTeam().getNom()))
                .matchsJoues(r.getMatchsJoues())
                .victoires(r.getVictoires())
                .nuls(r.getNuls())
                .defaites(r.getDefaites())
                .points(r.getPoints())
                .butsMarques(r.getButsMarques())
                .butsEncaisses(r.getButsEncaisses())
                .diffButs(r.getDiffButs())
                .build();
    }

    public List<RankingResponse> toResponseList(List<Ranking> rankings) {
        return rankings.stream().map(this::toResponse).toList();
    }

    private String initiales(String nom) {
        if (nom == null || nom.isBlank()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String mot : nom.trim().split("\\s+")) {
            if (!mot.isEmpty()) {
                sb.append(Character.toUpperCase(mot.charAt(0)));
            }
            if (sb.length() >= 3) {
                break;
            }
        }
        return sb.toString();
    }
}
