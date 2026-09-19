package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.TournamentMatch;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.entity.MatchPhase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {

    List<TournamentMatch> findByTournamentId(Long tournamentId);

    List<TournamentMatch> findByTournamentIdAndPhase(Long tournamentId, MatchPhase phase);

    List<TournamentMatch> findByTournamentIdOrderByTourAsc(Long tournamentId);

    boolean existsByTournamentId(Long tournamentId);

    boolean existsByTournamentIdAndPhase(Long tournamentId, MatchPhase phase);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId AND m.statut = :statut")
    List<TournamentMatch> findByTournamentIdAndStatut(@Param("tournamentId") Long tournamentId,
                                                       @Param("statut") MatchStatus statut);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.date = :date AND m.heure = :heure " +
           "AND (m.equipeDomicile.id = :equipeId OR m.equipeVisiteur.id = :equipeId)")
    List<TournamentMatch> findConflictsForTeam(@Param("tournamentId") Long tournamentId,
                                               @Param("date") LocalDate date,
                                               @Param("heure") LocalTime heure,
                                               @Param("equipeId") Long equipeId);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.phase = :phase AND m.equipeDomicile.id = :equipeId")
    List<TournamentMatch> findByTournamentIdAndPhaseAndEquipeDomicile(@Param("tournamentId") Long tournamentId,
                                                                       @Param("phase") MatchPhase phase,
                                                                       @Param("equipeId") Long equipeId);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.phase = :phase AND m.equipeVisiteur.id = :equipeId")
    List<TournamentMatch> findByTournamentIdAndPhaseAndEquipeVisiteur(@Param("tournamentId") Long tournamentId,
                                                                       @Param("phase") MatchPhase phase,
                                                                       @Param("equipeId") Long equipeId);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.phase = :phase AND m.tour = :tour " +
           "AND (m.equipeDomicile.id = :equipeId OR m.equipeVisiteur.id = :equipeId)")
    List<TournamentMatch> findByTournamentAndPhaseAndTourAndEquipe(@Param("tournamentId") Long tournamentId,
                                                                     @Param("phase") MatchPhase phase,
                                                                     @Param("tour") int tour,
                                                                     @Param("equipeId") Long equipeId);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.phase = :phase AND m.tour = :tour AND m.bracketPosition = :bracketPosition")
    List<TournamentMatch> findByTournamentAndPhaseAndTourAndBracketPosition(
           @Param("tournamentId") Long tournamentId,
           @Param("phase") MatchPhase phase,
           @Param("tour") int tour,
           @Param("bracketPosition") Integer bracketPosition);
}
