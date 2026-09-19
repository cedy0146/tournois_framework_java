package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentStatus;
import com.tpe.tournoi.entity.TournamentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Page<Tournament> findByStatut(TournamentStatus statut, Pageable pageable);

    Page<Tournament> findBySport(String sport, Pageable pageable);

    Page<Tournament> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Tournament> findByStatutAndSport(TournamentStatus statut, String sport, Pageable pageable);

    Page<Tournament> findByType(TournamentType type, Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE " +
           "(:statut IS NULL OR t.statut = :statut) AND " +
           "(:sport IS NULL OR t.sport = :sport) AND " +
           "(:nom IS NULL OR LOWER(t.nom) LIKE LOWER(CONCAT('%', :nom, '%')))")
    Page<Tournament> findByFilters(@Param("statut") TournamentStatus statut,
                                   @Param("sport") String sport,
                                   @Param("nom") String nom,
                                   Pageable pageable);

    List<Tournament> findByStatutIn(List<TournamentStatus> statuts);
}
