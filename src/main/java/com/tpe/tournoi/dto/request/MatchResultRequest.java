package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchResultRequest {

    @NotNull(message = "Le score domicile est obligatoire")
    @Min(value = 0, message = "Le score domicile ne peut pas être négatif")
    private Integer scoreDomicile;

    @NotNull(message = "Le score visiteur est obligatoire")
    @Min(value = 0, message = "Le score visiteur ne peut pas être négatif")
    private Integer scoreVisiteur;
}
