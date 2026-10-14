package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.entity.MatchPhase;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.util.FilterParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/matches")
public class GlobalMatchWebController {

    private final MatchService matchService;
    private final TournamentService tournamentService;

    public GlobalMatchWebController(MatchService matchService, TournamentService tournamentService) {
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) String tournoi,
                       @RequestParam(required = false) String phase,
                       @RequestParam(required = false) String statut,
                       @RequestParam(required = false) String date,
                       @RequestParam(required = false) String tri,
                       @RequestParam(required = false) String ordre,
                       Model model) {
        MatchPhase currentPhase = FilterParams.enumOrNull(MatchPhase.class, phase);
        MatchStatus currentStatut = FilterParams.enumOrNull(MatchStatus.class, statut);
        Long tournamentId = FilterParams.longOrNull(tournoi);

        String champ = FilterParams.champTri(tri, "date", java.util.Set.of("date", "heure", "tour", "statut"));
        String sens = ordre == null || ordre.isBlank() ? "asc" : ordre;
        model.addAttribute("matches", matchService.getMatches(
                PageRequest.of(FilterParams.page(page), FilterParams.size(size, 12),
                        Sort.by("desc".equalsIgnoreCase(sens) ? Sort.Direction.DESC : Sort.Direction.ASC, champ)),
                tournamentId, currentPhase, currentStatut, FilterParams.dateOrNull(date), null));
        model.addAttribute("tournois", tournamentService.getAll(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "nom")), null, null, null));
        model.addAttribute("phases", MatchPhase.values());
        model.addAttribute("statuts", MatchStatus.values());
        model.addAttribute("filtreTournoi", tournamentId);
        model.addAttribute("filtrePhase", currentPhase == null ? null : currentPhase.name());
        model.addAttribute("filtreStatut", currentStatut == null ? null : currentStatut.name());
        model.addAttribute("filtreDate", FilterParams.texte(date));
        model.addAttribute("taille", FilterParams.size(size, 12));
        model.addAttribute("tri", champ);
        model.addAttribute("ordre", sens);
        java.util.Map<String, String> champsTri = new java.util.LinkedHashMap<>();
        champsTri.put("date", "Date");
        champsTri.put("heure", "Heure");
        champsTri.put("tour", "Tour");
        champsTri.put("statut", "Statut");
        model.addAttribute("champsTri", champsTri);
        return "match/global-list";
    }
}
