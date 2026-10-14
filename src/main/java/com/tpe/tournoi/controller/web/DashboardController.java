package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.service.StatsService;
import com.tpe.tournoi.service.TournamentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    private final TournamentService tournamentService;
    private final StatsService statsService;

    public DashboardController(TournamentService tournamentService, StatsService statsService) {
        this.tournamentService = tournamentService;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("tournaments", tournamentService.getAllActive());
        return "landing";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<TournamentResponse> tournaments = tournamentService.getAllActive();
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("nextTournament", tournaments.stream()
                .filter(tournament -> "PROGRAMME".equals(tournament.getStatut())
                        || "INSCRIPTION".equals(tournament.getStatut()))
                .min(Comparator.comparing(TournamentResponse::getDateDebut))
                .orElse(null));
        model.addAttribute("stats", statsService.getDashboardStats());
        return "dashboard";
    }
}
