package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.CreateTeamRequest;
import com.tpe.tournoi.dto.request.UpdateTeamRequest;
import com.tpe.tournoi.dto.response.TeamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TeamService {

    TeamResponse create(CreateTeamRequest request);

    TeamResponse getById(Long id);

    Page<TeamResponse> getAll(Pageable pageable, String nom, String statut);

    List<TeamResponse> getAvailableForTournament(Long tournamentId);

    TeamResponse update(Long id, UpdateTeamRequest request);

    void delete(Long id);

    List<Map<String, Object>> getTeamHistory(Long id);
}
