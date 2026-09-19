package com.tpe.tournoi.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentResponse {
    private Long id;
    private String nom;
    private String sport;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private int nombreMaxEquipes;
    private String type;
    private String statut;
    private int nombreEquipesInscrites;
}
