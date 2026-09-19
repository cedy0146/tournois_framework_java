package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.response.RankingResponse;
import java.util.List;

public interface RankingService {

    List<RankingResponse> getRanking(Long tournamentId);

    void recalculateRankings(Long tournamentId);
}
