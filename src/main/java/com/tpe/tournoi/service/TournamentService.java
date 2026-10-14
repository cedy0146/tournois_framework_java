package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.CreateTournamentRequest;
import com.tpe.tournoi.dto.request.UpdateTournamentRequest;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.Tournament;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.entity.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TournamentService {

    TournamentResponse create(CreateTournamentRequest request);

    TournamentResponse getById(Long id);

    Page<TournamentResponse> getAll(Pageable pageable, String statut, String sport, String nom);

    TournamentResponse update(Long id, UpdateTournamentRequest request);

    void delete(Long id);

    void registerTeam(Long tournamentId, Long teamId);

    void removeTeam(Long tournamentId, Long teamId);

    void generateMatches(Long tournamentId);

    List<TournamentResponse> getAllActive();

    void startTournament(Long id);

    void cancelTournament(Long id);

    List<TournamentResponse> getRegisteredTeams(Long tournamentId);

    TournamentStatus openRegistration(Long id);

    String getStatusDisplay(TournamentStatus status);

    String getTypeDisplay(TournamentType type);
}