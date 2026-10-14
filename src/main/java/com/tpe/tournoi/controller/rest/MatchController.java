package com.tpe.tournoi.controller.rest;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.request.UpdateMatchScheduleRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.entity.MatchPhase;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.util.FilterParams;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    private static final Set<String> CHAMPS_TRI = Set.of("date", "heure", "tour", "statut", "id");

    @GetMapping
    public ResponseEntity<Page<MatchResponse>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "date,asc") String[] sort,
            @RequestParam(required = false) String tournamentId,
            @RequestParam(required = false) String phase,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String equipeId) {

        String champ = FilterParams.champTri(sort.length > 0 ? sort[0] : null, "date", CHAMPS_TRI);
        Sort.Direction sens = sort.length > 1 ? FilterParams.sensTri(sort[1]) : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(FilterParams.page(page), FilterParams.size(size, 20),
                Sort.by(sens, champ));

        return ResponseEntity.ok(matchService.getMatches(
                pageable,
                FilterParams.longOrNull(tournamentId),
                FilterParams.enumOrNull(MatchPhase.class, phase),
                FilterParams.enumOrNull(MatchStatus.class, statut),
                FilterParams.dateOrNull(date),
                FilterParams.longOrNull(equipeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getById(id));
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER', 'ADMIN')")
    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResultRequest> submitResult(@PathVariable Long id,
                                                            @Valid @RequestBody MatchResultRequest request) {
        return ResponseEntity.ok(matchService.submitResult(id, request));
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER', 'ADMIN')")
    @PutMapping("/{id}/schedule")
    public ResponseEntity<MatchResponse> updateSchedule(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateMatchScheduleRequest request) {
        return ResponseEntity.ok(matchService.updateSchedule(id, request));
    }
}
