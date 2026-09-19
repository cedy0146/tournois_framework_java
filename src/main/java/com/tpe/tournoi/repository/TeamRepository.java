package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.TeamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByNom(String nom);

    boolean existsByNom(String nom);

    Page<Team> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Team> findByStatut(TeamStatus statut, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE " +
           "(:nom IS NULL OR LOWER(t.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:statut IS NULL OR t.statut = :statut)")
    Page<Team> findByFilters(@Param("nom") String nom,
                             @Param("statut") TeamStatus statut,
                             Pageable pageable);
}
