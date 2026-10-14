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

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM TournamentMatch m " +
           "WHERE m.equipeDomicile.id = :teamId OR m.equipeVisiteur.id = :teamId OR m.vainqueur.id = :teamId")
    boolean existsByTeam(@Param("teamId") Long teamId);

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

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId AND m.phase = :phase AND m.tour = :tour AND m.statut <> :statut")
    List<TournamentMatch> findByTournamentAndPhaseAndTourAndStatutNot(@Param("tournamentId") Long tournamentId,
                                                 @Param("phase") MatchPhase phase,
                                                 @Param("tour") int tour,
                                                 @Param("statut") MatchStatus statut);

    long countByTournamentId(Long tournamentId);

    long countByTournamentIdAndStatut(Long tournamentId, MatchStatus statut);

    long countByStatut(MatchStatus statut);

    long countByStatutAndDateGreaterThanEqual(MatchStatus statut, java.time.LocalDate date);

    // --- Détection de conflits de planning ---

    @Query("SELECT m FROM TournamentMatch m WHERE (:matchId IS NULL OR m.id <> :matchId) " +
           "AND m.date = :date AND m.heure = :heure " +
           "AND (m.equipeDomicile.id IN (:teamIds) OR m.equipeVisiteur.id IN (:teamIds))")
    List<TournamentMatch> findConflictsForTeams(@Param("matchId") Long matchId,
                                                @Param("date") LocalDate date,
                                                @Param("heure") LocalTime heure,
                                                @Param("teamIds") List<Long> teamIds);

    @Query("SELECT m FROM TournamentMatch m WHERE (:matchId IS NULL OR m.id <> :matchId) " +
           "AND m.date = :date AND m.heure = :heure AND m.terrain = :terrain")
    List<TournamentMatch> findConflictsForTerrain(@Param("matchId") Long matchId,
                                                  @Param("date") LocalDate date,
                                                  @Param("heure") LocalTime heure,
                                                  @Param("terrain") String terrain);

    // --- Bracket ---

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId " +
           "AND m.tour = :tour AND m.bracketPosition = :bracketPosition")
    List<TournamentMatch> findByTournamentIdAndTourAndBracketPosition(
            @Param("tournamentId") Long tournamentId,
            @Param("tour") int tour,
            @Param("bracketPosition") Integer bracketPosition);

    @Query("SELECT MAX(m.date) FROM TournamentMatch m WHERE m.tournament.id = :tournamentId")
    LocalDate findLastMatchDate(@Param("tournamentId") Long tournamentId);

    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId AND (m.equipeDomicile.id = :teamId OR m.equipeVisiteur.id = :teamId)")
    List<TournamentMatch> findByTournamentIdAndTeam(@Param("tournamentId") Long tournamentId, @Param("teamId") Long teamId);

    @Query("SELECT SUM(m.scoreDomicile + m.scoreVisiteur) FROM TournamentMatch m WHERE m.tournament.id = :tournamentId AND m.statut = :statut")
    long sumGoalsByTournament(@Param("tournamentId") Long tournamentId, @Param("statut") MatchStatus statut);

    @Query("SELECT COUNT(m) FROM TournamentMatch m WHERE m.tournament.id = :tournamentId AND m.statut = :statut AND m.scoreDomicile = m.scoreVisiteur")
    long countByTournamentIdAndStatutAndScoreDomicileEqualsScoreVisiteur(@Param("tournamentId") Long tournamentId,
                                                                           @Param("statut") MatchStatus statut);

    // --- Recherche paginée avec filtres ---

    @Query("SELECT m FROM TournamentMatch m WHERE " +
           "(:tournamentId IS NULL OR m.tournament.id = :tournamentId) AND " +
           "(:phase IS NULL OR m.phase = :phase) AND " +
           "(:statut IS NULL OR m.statut = :statut) AND " +
           "(:date IS NULL OR m.date = :date) AND " +
           "(:equipeId IS NULL OR m.equipeDomicile.id = :equipeId OR m.equipeVisiteur.id = :equipeId) " +
           "ORDER BY m.date ASC, m.heure ASC, m.id ASC")
    Page<TournamentMatch> findByFilters(@Param("tournamentId") Long tournamentId,
                                        @Param("phase") MatchPhase phase,
                                        @Param("statut") MatchStatus statut,
                                        @Param("date") LocalDate date,
                                        @Param("equipeId") Long equipeId,
                                        Pageable pageable);
}
