package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findByTournamentIdOrderByClassementAsc(Long tournamentId);

    Optional<Ranking> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    @Query("SELECT r FROM Ranking r WHERE r.tournament.id = :tournamentId ORDER BY r.points DESC, (r.butsMarques - r.butsEncaisses) DESC, r.butsMarques DESC")
    List<Ranking> findSortedRankings(@Param("tournamentId") Long tournamentId);

    void deleteByTournamentId(Long tournamentId);
}
