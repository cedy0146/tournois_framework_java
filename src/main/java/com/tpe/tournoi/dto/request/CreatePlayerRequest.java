package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePlayerRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Le poste est obligatoire")
    private String poste;

    @Positive(message = "Le numéro doit être positif")
    private int numero;
}
