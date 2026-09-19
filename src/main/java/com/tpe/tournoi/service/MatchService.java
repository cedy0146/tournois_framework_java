package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.MatchPhase;
import java.util.List;

public interface MatchService {

    List<MatchResponse> getByTournament(Long tournamentId);

    List<MatchResponse> getByTournamentAndPhase(Long tournamentId, MatchPhase phase);

    MatchResponse getById(Long id);

    MatchResultRequest submitResult(Long matchId, MatchResultRequest request);

    List<MatchResponse> getEliminationTree(Long tournamentId);
}
