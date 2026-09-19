package com.tpe.tournoi.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RankingResponse {
    private int classement;
    private Long teamId;
    private String teamNom;
    private int matchsJoues;
    private int victoires;
    private int nuls;
    private int defaites;
    private int points;
    private int butsMarques;
    private int butsEncaisses;
    private int diffButs;
}
