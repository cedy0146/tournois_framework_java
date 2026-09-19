package com.tpe.tournoi.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchResponse {
    private Long id;
    private Long tournamentId;
    private String tournamentNom;
    private Long equipeDomicileId;
    private String equipeDomicileNom;
    private Long equipeVisiteurId;
    private String equipeVisiteurNom;
    private LocalDate date;
    private LocalTime heure;
    private String terrain;
    private String phase;
    private String statut;
    private Integer scoreDomicile;
    private Integer scoreVisiteur;
    private Long vainqueurId;
    private String vainqueurNom;
    private int tour;
    private Integer bracketPosition;
}
