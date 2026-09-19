package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TournamentService tournamentService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("tournaments", tournamentService.getAllActive());
        return "dashboard";
    }
}
