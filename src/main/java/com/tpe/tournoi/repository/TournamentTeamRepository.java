package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.TournamentTeam;
import com.tpe.tournoi.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Long> {

    boolean existsByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    Optional<TournamentTeam> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    List<TournamentTeam> findByTournamentId(Long tournamentId);

    List<TournamentTeam> findByTournamentIdAndInscriptionStatut(Long tournamentId, RegistrationStatus statut);

    long countByTournamentIdAndInscriptionStatut(Long tournamentId, RegistrationStatus statut);
}
