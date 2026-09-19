package com.tpe.tournoi.controller.rest;

import com.tpe.tournoi.dto.request.*;
import com.tpe.tournoi.dto.response.*;
import com.tpe.tournoi.entity.MatchPhase;
import com.tpe.tournoi.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final RankingService rankingService;

    // --- Tournament CRUD ---

    @PostMapping("/tournaments")
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        TournamentResponse response = tournamentService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/tournaments")
    public ResponseEntity<Page<TournamentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String nom) {

        Sort sortOrder = sort.length == 2
                ? Sort.by(sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sort[0])
                : Sort.by(sort[0]);

        Page<TournamentResponse> result = tournamentService.getAll(
                PageRequest.of(page, size, sortOrder), statut, sport, nom);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tournaments/{id}")
    public ResponseEntity<TournamentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTournamentRequest request) {
        return ResponseEntity.ok(tournamentService.update(id, request));
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Team registration ---

    @PostMapping("/tournaments/{id}/teams")
    public ResponseEntity<Void> registerTeam(@PathVariable("id") Long tournamentId,
                                              @RequestParam Long teamId) {
        tournamentService.registerTeam(tournamentId, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/tournaments/{id}/teams/{teamId}")
    public ResponseEntity<Void> removeTeam(@PathVariable("id") Long tournamentId,
                                            @PathVariable Long teamId) {
        tournamentService.removeTeam(tournamentId, teamId);
        return ResponseEntity.noContent().build();
    }

    // --- Match generation ---

    @PostMapping("/tournaments/{id}/generate-matches")
    public ResponseEntity<Void> generateMatches(@PathVariable Long id) {
        tournamentService.generateMatches(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // --- Matches ---

    @GetMapping("/tournaments/{id}/matches")
    public ResponseEntity<List<MatchResponse>> getMatches(@PathVariable Long id,
                                                           @RequestParam(required = false) String phase) {
        if (phase != null && !phase.isBlank()) {
            MatchPhase matchPhase = MatchPhase.valueOf(phase);
            return ResponseEntity.ok(matchService.getByTournamentAndPhase(id, matchPhase));
        }
        return ResponseEntity.ok(matchService.getByTournament(id));
    }

    // --- Ranking ---

    @GetMapping("/tournaments/{id}/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking(@PathVariable Long id) {
        return ResponseEntity.ok(rankingService.getRanking(id));
    }
}
