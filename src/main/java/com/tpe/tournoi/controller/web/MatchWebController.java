package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.MatchResultRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.MatchPhase;
import com.tpe.tournoi.entity.MatchStatus;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.util.FilterParams;
import jakarta.validation.Valid;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.service.RankingService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tournaments/{tournamentId}/matches")
public class MatchWebController {

    private final MatchService matchService;
    private final TournamentService tournamentService;

    public MatchWebController(MatchService matchService, TournamentService tournamentService) {
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    @GetMapping("")
    public String list(@PathVariable Long tournamentId,
                       @RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) String phase,
                       @RequestParam(required = false) String statut,
                       @RequestParam(required = false) String tri,
                       @RequestParam(required = false) String ordre,
                       Model model) {
        TournamentResponse tournament = tournamentService.getById(tournamentId);
        MatchPhase currentPhase = FilterParams.enumOrNull(MatchPhase.class, phase);
        MatchStatus currentStatut = FilterParams.enumOrNull(MatchStatus.class, statut);

        String champ = FilterParams.champTri(tri, "date", Set.of("date", "heure", "tour", "statut"));
        String sens = ordre == null || ordre.isBlank() ? "asc" : ordre;
        PageRequest pageable = PageRequest.of(FilterParams.page(page), FilterParams.size(size, 12),
                Sort.by("desc".equalsIgnoreCase(sens) ? Sort.Direction.DESC : Sort.Direction.ASC, champ));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matches", matchService.getMatches(pageable, tournamentId, currentPhase,
                currentStatut, null, null));
        model.addAttribute("phases", MatchPhase.values());
        model.addAttribute("statuts", MatchStatus.values());
        model.addAttribute("filtrePhase", currentPhase == null ? null : currentPhase.name());
        model.addAttribute("filtreStatut", currentStatut == null ? null : currentStatut.name());
        model.addAttribute("taille", pageable.getPageSize());
        model.addAttribute("tri", champ);
        model.addAttribute("ordre", sens);
        Map<String, String> champsTri = new LinkedHashMap<>();
        champsTri.put("date", "Date");
        champsTri.put("heure", "Heure");
        champsTri.put("tour", "Tour");
        champsTri.put("statut", "Statut");
        model.addAttribute("champsTri", champsTri);
        return "match/list";
    }

    @GetMapping("/bracket")
    public String bracket(@PathVariable Long tournamentId, Model model) {
        TournamentResponse tournament = tournamentService.getById(tournamentId);
        model.addAttribute("tournament", tournament);
        if (!"ELIMINATION_DIRECTE".equals(tournament.getType())) {
            model.addAttribute("message", "Cet arbre s'affiche uniquement pour les tournois d'élimination directe");
            return "match/bracket";
        }
        List<MatchResponse> matches = matchService.getEliminationTree(tournamentId);
        // Grouper les matchs par tour
        Map<Integer, List<MatchResponse>> byTour = matches.stream()
                .collect(Collectors.groupingBy(MatchResponse::getTour));
        model.addAttribute("byTour", byTour);
        return "match/bracket";
    }

    @GetMapping("/{id}/result")
    public String resultForm(@PathVariable Long tournamentId, @PathVariable Long id, Model model) {
        model.addAttribute("match", matchService.getById(id));
        return "match/result";
    }

    @PostMapping("/{id}/result")
    public String submitResult(@PathVariable Long tournamentId,
                               @PathVariable Long id,
                               @Valid @ModelAttribute MatchResultRequest request,
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
