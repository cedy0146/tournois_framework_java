package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.request.UpdateMatchScheduleRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.MatchPhase;
import com.tpe.tournoi.entity.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MatchService {

    Page<MatchResponse> getMatches(Pageable pageable, Long tournamentId, MatchPhase phase,
                                   MatchStatus statut, LocalDate date, Long equipeId);

    List<MatchResponse> getByTournament(Long tournamentId);

    List<MatchResponse> getByTournamentAndPhase(Long tournamentId, MatchPhase phase);

    MatchResponse getById(Long id);

    MatchResultRequest submitResult(Long matchId, MatchResultRequest request);

    MatchResponse updateSchedule(Long matchId, UpdateMatchScheduleRequest request);

    List<MatchResponse> getEliminationTree(Long tournamentId);
}
