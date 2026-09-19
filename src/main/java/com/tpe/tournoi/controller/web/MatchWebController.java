package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tournaments/{tournamentId}/matches")
@RequiredArgsConstructor
public class MatchWebController {

    private final MatchService matchService;
    private final TournamentService tournamentService;

    @GetMapping
    public String list(@PathVariable Long tournamentId, Model model) {
        model.addAttribute("tournament", tournamentService.getById(tournamentId));
        model.addAttribute("matches", matchService.getByTournament(tournamentId));
        return "match/list";
    }

    @GetMapping("/{id}/result")
    public String resultForm(@PathVariable Long tournamentId, @PathVariable Long id, Model model) {
        model.addAttribute("match", matchService.getById(id));
        return "match/result";
    }

    @PostMapping("/{id}/result")
    public String submitResult(@PathVariable Long tournamentId,
                               @PathVariable Long id,
                               @ModelAttribute MatchResultRequest request,
                               RedirectAttributes ra) {
        try {
            matchService.submitResult(id, request);
            ra.addFlashAttribute("success", "Résultat enregistré");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + tournamentId + "/matches";
    }
}
