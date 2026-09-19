package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private int classement;

    private int matchsJoues;

    private int victoires;

    private int nuls;

    private int defaites;

    private int points;

    private int butsMarques;

    private int butsEncaisses;

    public int getDiffButs() {
        return butsMarques - butsEncaisses;
    }
}
