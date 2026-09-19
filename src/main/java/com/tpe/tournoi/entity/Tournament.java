package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String sport;

    private String description;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private String lieu;

    @Column(nullable = false)
    private int nombreMaxEquipes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus statut;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentTeam> tournamentTeams = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentMatch> matches = new ArrayList<>();

    public void addTournamentTeam(TournamentTeam tt) {
        tournamentTeams.add(tt);
        tt.setTournament(this);
    }

    public void removeTournamentTeam(TournamentTeam tt) {
        tournamentTeams.remove(tt);
        tt.setTournament(null);
    }
}
