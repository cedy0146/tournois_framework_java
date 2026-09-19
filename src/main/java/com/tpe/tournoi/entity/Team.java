package com.tpe.tournoi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String logo;

    private String ville;

    private String entraineur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus statut;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Player> joueurs = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentTeam> tournamentTeams = new ArrayList<>();

    public void addJoueur(Player player) {
        joueurs.add(player);
        player.setEquipe(this);
    }

    public void removeJoueur(Player player) {
        joueurs.remove(player);
        player.setEquipe(null);
    }
}
