package com.tpe.tournoi.controller.rest;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getById(id));
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResultRequest> submitResult(@PathVariable Long id,
                                                            @Valid @RequestBody MatchResultRequest request) {
        return ResponseEntity.ok(matchService.submitResult(id, request));
    }
}
