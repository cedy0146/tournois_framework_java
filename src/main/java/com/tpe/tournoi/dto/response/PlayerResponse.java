package com.tpe.tournoi.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerResponse {
    private Long id;
    private String nom;
    private String poste;
    private int numero;
    private Long equipeId;
    private String equipeNom;
}
