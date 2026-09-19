package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tournament_teams",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "team_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus inscriptionStatut;

    private int seed;

    public boolean isInscrit() {
        return RegistrationStatus.INSCRIT.equals(inscriptionStatut);
    }
}
