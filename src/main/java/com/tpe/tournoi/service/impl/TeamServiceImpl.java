package com.tpe.tournoi.service.impl;

import com.tpe.tournoi.dto.request.CreateTeamRequest;
import com.tpe.tournoi.dto.request.UpdateTeamRequest;
import com.tpe.tournoi.dto.response.TeamResponse;
import com.tpe.tournoi.entity.Team;
import com.tpe.tournoi.entity.TeamStatus;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.mapper.TeamMapper;
import com.tpe.tournoi.repository.TeamRepository;
import com.tpe.tournoi.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepo;
    private final TeamMapper mapper;

    @Override
    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        if (teamRepo.existsByNom(request.getNom())) {
            throw new ConflictException("Une équipe avec le nom '" + request.getNom() + "' existe déjà");
        }

        Team team = Team.builder()
                .nom(request.getNom())
                .logo(request.getLogo())
                .ville(request.getVille())
                .entraineur(request.getEntraineur())
                .statut(TeamStatus.ACTIVE)
                .build();

        team = teamRepo.save(team);
        return mapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getById(Long id) {
        Team team = findTeam(id);
        return mapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamResponse> getAll(Pageable pageable, String nom, String statut) {
        TeamStatus status = null;
        if (statut != null && !statut.isBlank()) {
            try {
                status = TeamStatus.valueOf(statut);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Statut invalide: " + statut);
            }
        }
        return teamRepo.findByFilters(nom, status, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public TeamResponse update(Long id, UpdateTeamRequest request) {
        Team team = findTeam(id);

        if (request.getNom() != null) team.setNom(request.getNom());
        if (request.getLogo() != null) team.setLogo(request.getLogo());
        if (request.getVille() != null) team.setVille(request.getVille());
        if (request.getEntraineur() != null) team.setEntraineur(request.getEntraineur());

        if (request.getStatut() != null) {
            try {
                team.setStatut(TeamStatus.valueOf(request.getStatut()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Statut invalide: " + request.getStatut());
            }
        }

        team = teamRepo.save(team);
        return mapper.toResponse(team);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findTeam(id);
        teamRepo.deleteById(id);
    }

    private Team findTeam(Long id) {
        return teamRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe", id));
    }
}
