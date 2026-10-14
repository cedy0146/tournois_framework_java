package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.CreateTournamentRequest;
import com.tpe.tournoi.dto.request.UpdateTournamentRequest;
import com.tpe.tournoi.dto.response.TournamentResponse;
import com.tpe.tournoi.entity.RankingCriterion;
import com.tpe.tournoi.entity.TournamentStatus;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.util.FilterParams;

import com.tpe.tournoi.service.TeamService;
import com.tpe.tournoi.service.TournamentService;
import com.tpe.tournoi.util.RankingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/tournaments")
public class TournamentWebController {

    private final TournamentService tournamentService;
    private final TeamService teamService;

    public TournamentWebController(TournamentService tournamentService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    private static final Set<String> CHAMPS_TRI = Set.of(
            "nom", "dateDebut", "dateFin", "statut", "sport", "lieu");

    @GetMapping
    public String list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) String nom,
                       @RequestParam(required = false) String statut,
                       @RequestParam(required = false) String tri,
                       @RequestParam(required = false) String ordre,
                       Model model) {
        TournamentStatus statutCourant = FilterParams.enumOrNull(TournamentStatus.class, statut);
        String champ = FilterParams.champTri(tri, "dateDebut", CHAMPS_TRI);
        String sens = ordre == null || ordre.isBlank() ? "desc" : ordre;
        PageRequest pageable = PageRequest.of(FilterParams.page(page), FilterParams.size(size),
                Sort.by("desc".equalsIgnoreCase(sens) ? Sort.Direction.DESC : Sort.Direction.ASC, champ));

        model.addAttribute("tournaments", tournamentService.getAll(
                pageable, statutCourant == null ? null : statutCourant.name(), null, FilterParams.texte(nom)));
        model.addAttribute("filtreNom", FilterParams.texte(nom));
        model.addAttribute("filtreStatut", statutCourant == null ? null : statutCourant.name());
        model.addAttribute("taille", pageable.getPageSize());
        model.addAttribute("tri", champ);
        model.addAttribute("ordre", sens);
        model.addAttribute("statuts", TournamentStatus.values());
        Map<String, String> champsTri = new LinkedHashMap<>();
        champsTri.put("dateDebut", "Date de début");
        champsTri.put("dateFin", "Date de fin");
        champsTri.put("nom", "Nom");
        champsTri.put("statut", "Statut");
        champsTri.put("sport", "Sport");
        champsTri.put("lieu", "Lieu");
        model.addAttribute("champsTri", champsTri);
        return "tournament/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, HttpServletRequest httpRequest) {
        model.addAttribute("tournament", new CreateTournamentRequest());
        model.addAttribute("types", TournamentType.values());
        addCriteresModel(model, null);
        return ModalViews.of("tournament/form", httpRequest);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("tournament") CreateTournamentRequest request,
                         BindingResult result, RedirectAttributes ra, Model model,
                         HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            model.addAttribute("types", TournamentType.values());
            addCriteresModel(model, null);
            return ModalViews.of("tournament/form", httpRequest);
        }
        try {
            tournamentService.create(request);
            ra.addFlashAttribute("success", "Tournoi créé avec succès");
            return "redirect:/tournaments";
        } catch (BusinessException | ConflictException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("types", TournamentType.values());
            addCriteresModel(model, null);
            return ModalViews.of("tournament/form", httpRequest);
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpServletRequest httpRequest) {
        populateDetailModel(id, model);
        return ModalViews.of("tournament/detail", httpRequest);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpServletRequest httpRequest) {
        model.addAttribute("tournament", populateEditModel(id, model));
        return ModalViews.of("tournament/edit", httpRequest);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("tournament") UpdateTournamentRequest request,
                         BindingResult result, RedirectAttributes ra, Model model,
                         HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            populateEditModel(id, model);
            return ModalViews.of("tournament/edit", httpRequest);
        }
        try {
            tournamentService.update(id, request);
            ra.addFlashAttribute("success", "Tournoi mis à jour");
            return "redirect:/tournaments/" + id;
        } catch (BusinessException | InvalidOperationException e) {
            if (ModalViews.isModal(httpRequest)) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/tournaments/" + id + "/edit";
        }
    }

    private TournamentResponse populateEditModel(Long id, Model model) {
        TournamentResponse current = tournamentService.getById(id);
        model.addAttribute("tournamentId", id);
        model.addAttribute("currentType", current.getType());
        model.addAttribute("types", TournamentType.values());
        addCriteresModel(model, current.getCriteresClassement());
        return current;
    }

    /**
     * Expose les critères de classement disponibles et l'ordre courant
     * afin de pouvoir les choisir et les ordonner dans les formulaires.
     */
    private void addCriteresModel(Model model, String criteresCourants) {
        model.addAttribute("criteres", RankingCriterion.values());
        String[] parts = (criteresCourants == null || criteresCourants.isBlank())
                ? RankingUtil.DEFAULT_CRITERES.split(",")
                : criteresCourants.split(",");
        model.addAttribute("critere1Default", parts.length > 0 ? parts[0].trim() : "");
        model.addAttribute("critere2Default", parts.length > 1 ? parts[1].trim() : "");
        model.addAttribute("critere3Default", parts.length > 2 ? parts[2].trim() : "");
        model.addAttribute("critere4Default", parts.length > 3 ? parts[3].trim() : "");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            tournamentService.delete(id);
            return "redirect:/tournaments";
        }
        try {
            tournamentService.delete(id);
            ra.addFlashAttribute("success", "Tournoi supprimé");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments";
    }

    @PostMapping("/{id}/register-team")
    public String registerTeam(@PathVariable Long id, @RequestParam Long teamId, RedirectAttributes ra,
                               Model model, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            try {
                tournamentService.registerTeam(id, teamId);
                model.addAttribute("success", "Équipe inscrite");
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            } catch (BusinessException | ConflictException | InvalidOperationException e) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
        }
        try {
            tournamentService.registerTeam(id, teamId);
            ra.addFlashAttribute("success", "Équipe inscrite");
        } catch (BusinessException | ConflictException | InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/remove-team")
    public String removeTeam(@PathVariable Long id, @RequestParam Long teamId, RedirectAttributes ra,
                             Model model, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            try {
                tournamentService.removeTeam(id, teamId);
                model.addAttribute("success", "Équipe retirée");
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            } catch (InvalidOperationException e) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
        }
        try {
            tournamentService.removeTeam(id, teamId);
            ra.addFlashAttribute("success", "Équipe retirée");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/generate-matches")
    public String generateMatches(@PathVariable Long id, RedirectAttributes ra,
                                  Model model, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            tournamentService.generateMatches(id);
            model.addAttribute("success", "Matchs générés avec succès");
            populateDetailModel(id, model);
            return "tournament/detail :: modalContent";
        }
        try {
            tournamentService.generateMatches(id);
            ra.addFlashAttribute("success", "Matchs générés avec succès");
        } catch (BusinessException | ConflictException | InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/open-registration")
    public String openRegistration(@PathVariable Long id, RedirectAttributes ra,
                                   Model model, HttpServletRequest httpRequest) {
        TournamentResponse tournament = tournamentService.getById(id);
        if (ModalViews.isModal(httpRequest)) {
            try {
                tournamentService.openRegistration(id);
                model.addAttribute("success", "Inscriptions ouvertes");
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            } catch (BusinessException e) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
        }
        try {
            tournamentService.openRegistration(id);
            ra.addFlashAttribute("success", "Inscriptions ouvertes");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/start")
    public String startTournament(@PathVariable Long id, RedirectAttributes ra,
                                  Model model, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            try {
                tournamentService.startTournament(id);
                model.addAttribute("success", "Tournoi démarré");
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            } catch (InvalidOperationException e) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
        }
        try {
            tournamentService.startTournament(id);
            ra.addFlashAttribute("success", "Tournoi démarré");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelTournament(@PathVariable Long id, RedirectAttributes ra,
                                   Model model, HttpServletRequest httpRequest) {
        if (ModalViews.isModal(httpRequest)) {
            try {
                tournamentService.cancelTournament(id);
                model.addAttribute("success", "Tournoi annulé");
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            } catch (InvalidOperationException e) {
                model.addAttribute("error", e.getMessage());
                populateDetailModel(id, model);
                return "tournament/detail :: modalContent";
            }
        }
        try {
            tournamentService.cancelTournament(id);
            ra.addFlashAttribute("success", "Tournoi annulé");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    private void populateDetailModel(Long id, Model model) {
        TournamentResponse tournament = tournamentService.getById(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("availableTeams", teamService.getAvailableForTournament(id));
        // Ajoute la liste des équipes inscrites pour la vue
        model.addAttribute("registeredTeams", tournamentService.getRegisteredTeams(id));
    }
}