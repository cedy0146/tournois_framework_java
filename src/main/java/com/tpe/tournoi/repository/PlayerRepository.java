package com.tpe.tournoi.repository;

import com.tpe.tournoi.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByEquipeId(Long equipeId);

    List<Player> findByNomContainingIgnoreCase(String nom);
}
