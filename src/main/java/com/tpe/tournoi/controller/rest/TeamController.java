package com.tpe.tournoi.controller.rest;

import com.tpe.tournoi.dto.request.*;
import com.tpe.tournoi.dto.response.*;
import com.tpe.tournoi.service.TeamService;
import com.tpe.tournoi.service.PlayerService;
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
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        TeamResponse response = teamService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TeamResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(teamService.getAll(
                PageRequest.of(page, size, Sort.by("nom")), nom, statut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(teamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<List<PlayerResponse>> getPlayers(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getByEquipe(id));
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<PlayerResponse> addPlayer(@PathVariable Long id,
                                                     @Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = playerService.create(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/" + response.getId()).build().toUri();
        return ResponseEntity.created(location).body(response);
    }
}
