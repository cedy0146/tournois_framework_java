package com.tpe.tournoi.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamResponse {
    private Long id;
    private String nom;
    private String logo;
    private String ville;
    private String entraineur;
    private String statut;
    private int nombreJoueurs;
}
