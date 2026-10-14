package com.tpe.tournoi.service;

import com.tpe.tournoi.dto.request.CreatePlayerRequest;
import com.tpe.tournoi.dto.request.UpdatePlayerRequest;
import com.tpe.tournoi.dto.response.PlayerResponse;
import java.util.List;

public interface PlayerService {

    PlayerResponse create(Long equipeId, CreatePlayerRequest request);

    PlayerResponse update(Long equipeId, Long playerId, UpdatePlayerRequest request);

    List<PlayerResponse> getByEquipe(Long equipeId);

    void delete(Long id);
}
