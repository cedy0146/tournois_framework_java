package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTournamentRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le sport est obligatoire")
    private String sport;

    @Size(max = 500)
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être dans le futur ou présent")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @FutureOrPresent(message = "La date de fin doit être dans le futur ou présent")
    private LocalDate dateFin;

    private String lieu;

    @Positive(message = "Le nombre max d'équipes doit être positif")
    @Min(2)
    private int nombreMaxEquipes;

    @NotNull(message = "Le type de tournoi est obligatoire")
    private String type;
}
