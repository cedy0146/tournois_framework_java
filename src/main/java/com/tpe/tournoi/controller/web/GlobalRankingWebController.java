package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.response.RankingResponse;
import com.tpe.tournoi.service.RankingService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.util.FilterParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rankings")
public class GlobalRankingWebController {

    private final RankingService rankingService;
    private final TournamentService tournamentService;

    public GlobalRankingWebController(RankingService rankingService, TournamentService tournamentService) {
        this.rankingService = rankingService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String tournoi, Model model) {
        Long tournamentId = FilterParams.longOrNull(tournoi);

        List<RankingResponse> rankings = rankingService.getAllRankings();
        if (tournamentId != null) {
            rankings = rankings.stream()
                    .filter(r -> tournamentId.equals(r.getTournamentId()))
                    .toList();
        }

        Map<Long, List<RankingResponse>> rankingsByTournament = new LinkedHashMap<>();
        for (RankingResponse ranking : rankings) {
            rankingsByTournament
                    .computeIfAbsent(ranking.getTournamentId(), k -> new java.util.ArrayList<>())
                    .add(ranking);
        }

        model.addAttribute("rankings", rankings);
        model.addAttribute("rankingsByTournament", rankingsByTournament);
        model.addAttribute("filtreTournoi", tournamentId);
        model.addAttribute("tournois", tournamentService.getAll(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "nom")), null, null, null));
        return "ranking/global-list";
    }
}
