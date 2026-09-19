package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.CreateTeamRequest;
import com.tpe.tournoi.dto.request.UpdateTeamRequest;
import com.tpe.tournoi.dto.response.TeamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {

    TeamResponse create(CreateTeamRequest request);

    TeamResponse getById(Long id);

    Page<TeamResponse> getAll(Pageable pageable, String nom, String statut);

    TeamResponse update(Long id, UpdateTeamRequest request);

    void delete(Long id);
}
