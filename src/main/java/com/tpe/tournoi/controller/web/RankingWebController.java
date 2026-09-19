package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.service.RankingService;
import com.tpe.tournoi.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournaments/{tournamentId}/ranking")
@RequiredArgsConstructor
public class RankingWebController {

    private final RankingService rankingService;
    private final TournamentService tournamentService;

    @GetMapping
    public String ranking(@PathVariable Long tournamentId, Model model) {
        model.addAttribute("tournament", tournamentService.getById(tournamentId));
        model.addAttribute("rankings", rankingService.getRanking(tournamentId));
        return "ranking/list";
    }
}
