package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTeamRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    private String logo;

    @Size(max = 100)
    private String ville;

    @Size(max = 100)
    private String entraineur;
}
