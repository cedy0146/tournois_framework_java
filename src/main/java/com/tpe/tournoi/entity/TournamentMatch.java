package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tournament_matches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_domicile_id")
    private Team equipeDomicile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_visiteur_id")
    private Team equipeVisiteur;

    private LocalDate date;

    private LocalTime heure;

    private String terrain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchPhase phase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus statut;

    private Integer scoreDomicile;

    private Integer scoreVisiteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vainqueur_id")
    private Team vainqueur;

    private int tour;

    private Integer bracketPosition;

    public boolean hasResult() {
        return scoreDomicile != null && scoreVisiteur != null;
    }

    public Team getGagnant() {
        if (!hasResult()) return null;
        if (scoreDomicile > scoreVisiteur) return equipeDomicile;
        if (scoreVisiteur > scoreDomicile) return equipeVisiteur;
        return null;
    }

    public boolean isNul() {
        return hasResult() && scoreDomicile.equals(scoreVisiteur);
    }
}
