package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateTournamentRequest {

    @Size(min = 2, max = 100)
    private String nom;

    private String sport;

    @Size(max = 500)
    private String description;

    @FutureOrPresent
    private LocalDate dateDebut;

    @FutureOrPresent
    private LocalDate dateFin;

    private String lieu;

    @Positive
    @Min(2)
    private int nombreMaxEquipes;

    private String statut;
}
