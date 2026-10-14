package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.CreateTeamRequest;
import com.tpe.tournoi.dto.request.UpdatePlayerRequest;
import com.tpe.tournoi.dto.request.UpdateTeamRequest;
import com.tpe.tournoi.dto.response.MatchResponse;
import com.tpe.tournoi.dto.response.PlayerResponse;
import com.tpe.tournoi.entity.PlayerPosition;
import com.tpe.tournoi.entity.TeamStatus;
import com.tpe.tournoi.util.FilterParams;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.service.MatchService;
import com.tpe.tournoi.service.PlayerService;
import com.tpe.tournoi.service.StatsService;
import com.tpe.tournoi.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/teams")
public class TeamWebController {

    private final TeamService teamService;
    private final PlayerService playerService;
    private final MatchService matchService;
    private final StatsService statsService;

    public TeamWebController(TeamService teamService, PlayerService playerService,
                             MatchService matchService, StatsService statsService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.matchService = matchService;
        this.statsService = statsService;
    }

    private static final Set<String> CHAMPS_TRI = Set.of("nom", "ville", "entraineur", "statut");

    @GetMapping
    public String list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) String nom,
                       @RequestParam(required = false) String statut,
                       @RequestParam(required = false) String tri,
                       @RequestParam(required = false) String ordre,
                       Model model) {
        TeamStatus statutCourant = FilterParams.enumOrNull(TeamStatus.class, statut);
        String champ = FilterParams.champTri(tri, "nom", CHAMPS_TRI);
        String sens = ordre == null || ordre.isBlank() ? "asc" : ordre;
        PageRequest pageable = PageRequest.of(FilterParams.page(page), FilterParams.size(size),
                Sort.by("desc".equalsIgnoreCase(sens) ? Sort.Direction.DESC : Sort.Direction.ASC, champ));

        model.addAttribute("teams",
                teamService.getAll(pageable, FilterParams.texte(nom),
                        statutCourant == null ? null : statutCourant.name()));
        model.addAttribute("filtreNom", FilterParams.texte(nom));
        model.addAttribute("filtreStatut", statutCourant == null ? null : statutCourant.name());
        model.addAttribute("taille", pageable.getPageSize());
        model.addAttribute("tri", champ);
        model.addAttribute("ordre", sens);
        model.addAttribute("statuts", TeamStatus.values());
        Map<String, String> champsTri = new LinkedHashMap<>();
        champsTri.put("nom", "Nom");
        champsTri.put("ville", "Ville");
        champsTri.put("entraineur", "Entraîneur");
        champsTri.put("statut", "Statut");
        model.addAttribute("champsTri", champsTri);
        return "team/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, HttpServletRequest httpRequest) {
        model.addAttribute("team", new CreateTeamRequest());
        return ModalViews.of("team/form", httpRequest);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("team") CreateTeamRequest request,
                         BindingResult result, RedirectAttributes ra, Model model,
                         HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            return ModalViews.of("team/form", httpRequest);
        }
        try {
            teamService.create(request);
            ra.addFlashAttribute("success", "Équipe créée avec succès");
            return "redirect:/teams";
        } catch (ConflictException e) {
            model.addAttribute("error", e.getMessage());
            return ModalViews.of("team/form", httpRequest);
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpServletRequest httpRequest) {
        model.addAttribute("team", teamService.getById(id));
        Page<MatchResponse> matchsEquipe = matchService.getMatches(
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "date", "heure")),
                null, null, null, null, id);
        model.addAttribute("matchsEquipe", matchsEquipe);
        model.addAttribute("statsEquipe", statsService.computeTeamStats(matchsEquipe.getContent(), id));
        return ModalViews.of("team/detail", httpRequest);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpServletRequest httpRequest) {
        model.addAttribute("team", teamService.getById(id));
        model.addAttribute("teamId", id);
        return ModalViews.of("team/edit", httpRequest);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("team") UpdateTeamRequest request,
                         BindingResult result, RedirectAttributes ra, Model model,
                         HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            model.addAttribute("teamId", id);
            return ModalViews.of("team/edit", httpRequest);
        }
        teamService.update(id, request);
        ra.addFlashAttribute("success", "Équipe mise à jour");
        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            teamService.delete(id);
            return "redirect:/teams";
        }
        try {
            teamService.delete(id);
            ra.addFlashAttribute("success", "Équipe supprimée");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teams";
    }

    @GetMapping("/{teamId}/players/{playerId}/edit")
    public String editPlayerForm(@PathVariable Long teamId, @PathVariable Long playerId, Model model) {
        PlayerResponse player = findPlayer(teamId, playerId);
        model.addAttribute("team", teamService.getById(teamId));
        model.addAttribute("player", new UpdatePlayerRequest(player.getNom(), player.getPoste(), player.getNumero()));
        model.addAttribute("playerId", playerId);
        model.addAttribute("positions", PlayerPosition.values());
        return "team/player-edit";
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public String updatePlayer(@PathVariable Long teamId, @PathVariable Long playerId,
                               @Valid @ModelAttribute("player") UpdatePlayerRequest request,
                               BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            populatePlayerEditModel(teamId, playerId, model);
            return "team/player-edit";
        }
        try {
            playerService.update(teamId, playerId, request);
            ra.addFlashAttribute("success", "Joueur mis à jour");
            return "redirect:/teams/" + teamId;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            populatePlayerEditModel(teamId, playerId, model);
            return "team/player-edit";
        }
    }

    private void populatePlayerEditModel(Long teamId, Long playerId, Model model) {
        model.addAttribute("team", teamService.getById(teamId));
        model.addAttribute("playerId", playerId);
        model.addAttribute("positions", PlayerPosition.values());
    }

    private PlayerResponse findPlayer(Long teamId, Long playerId) {
        List<PlayerResponse> players = playerService.getByEquipe(teamId);
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Joueur", playerId));
    }
}
