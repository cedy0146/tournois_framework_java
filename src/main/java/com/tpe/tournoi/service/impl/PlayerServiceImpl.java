package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.CreatePlayerRequest;
import com.tpe.tournoi.dto.request.UpdatePlayerRequest;
import com.tpe.tournoi.dto.response.PlayerResponse;
import com.tpe.tournoi.entity.Player;
import com.tpe.tournoi.entity.PlayerPosition;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.PlayerMapper;
import com.tpe.tournoi.repository.PlayerRepository;
import com.tpe.tournoi.repository.TeamRepository;
import com.tpe.tournoi.service.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepo;
    private final TeamRepository teamRepo;
    private final PlayerMapper mapper;

    public PlayerServiceImpl(PlayerRepository playerRepo, TeamRepository teamRepo, PlayerMapper mapper) {
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PlayerResponse create(Long equipeId, CreatePlayerRequest request) {
        Team team = teamRepo.findById(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe", equipeId));

        PlayerPosition position;
        try {
            position = PlayerPosition.valueOf(request.getPoste());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Poste invalide: " + request.getPoste());
        }

        Player player = Player.builder()
                .nom(request.getNom())
                .poste(position)
                .numero(request.getNumero())
                .equipe(team)
                .build();

        player = playerRepo.save(player);
        return mapper.toResponse(player);
    }

    @Override
    @Transactional
    public PlayerResponse update(Long equipeId, Long playerId, UpdatePlayerRequest request) {
        Team team = teamRepo.findById(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe", equipeId));
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Joueur", playerId));

        if (!team.getId().equals(player.getEquipe().getId())) {
            throw new ResourceNotFoundException("Joueur", playerId);
        }

        try {
            player.setPoste(PlayerPosition.valueOf(request.getPoste()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Poste invalide: " + request.getPoste());
        }
        player.setNom(request.getNom());
        player.setNumero(request.getNumero());

        return mapper.toResponse(playerRepo.save(player));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getByEquipe(Long equipeId) {
        return playerRepo.findByEquipeId(equipeId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!playerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Joueur", id);
        }
        playerRepo.deleteById(id);
    }
}
