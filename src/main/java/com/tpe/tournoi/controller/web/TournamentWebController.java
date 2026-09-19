package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.CreateTournamentRequest;
import com.tpe.tournoi.dto.request.UpdateTournamentRequest;
import com.tpe.tournoi.entity.TournamentType;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentWebController {

    private final TournamentService tournamentService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("tournaments",
                tournamentService.getAll(PageRequest.of(page, size, Sort.by("dateDebut", "desc")), null, null, null));
        return "tournament/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tournament", new CreateTournamentRequest());
        model.addAttribute("types", TournamentType.values());
        return "tournament/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("tournament") CreateTournamentRequest request,
                         BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", TournamentType.values());
            return "tournament/form";
        }
        try {
            tournamentService.create(request);
            ra.addFlashAttribute("success", "Tournoi créé avec succès");
            return "redirect:/tournaments";
        } catch (BusinessException | ConflictException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("types", TournamentType.values());
            return "tournament/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentService.getById(id));
        return "tournament/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentService.getById(id));
        model.addAttribute("types", TournamentType.values());
        return "tournament/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("tournament") UpdateTournamentRequest request,
                         RedirectAttributes ra) {
        try {
            tournamentService.update(id, request);
            ra.addFlashAttribute("success", "Tournoi mis à jour");
            return "redirect:/tournaments/" + id;
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/tournaments/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tournamentService.delete(id);
            ra.addFlashAttribute("success", "Tournoi supprimé");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments";
    }

    @PostMapping("/{id}/register-team")
    public String registerTeam(@PathVariable Long id, @RequestParam Long teamId, RedirectAttributes ra) {
        try {
            tournamentService.registerTeam(id, teamId);
            ra.addFlashAttribute("success", "Équipe inscrite");
        } catch (BusinessException | ConflictException | InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/remove-team")
    public String removeTeam(@PathVariable Long id, @RequestParam Long teamId, RedirectAttributes ra) {
        try {
            tournamentService.removeTeam(id, teamId);
            ra.addFlashAttribute("success", "Équipe retirée");
        } catch (InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/generate-matches")
    public String generateMatches(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tournamentService.generateMatches(id);
            ra.addFlashAttribute("success", "Matchs générés avec succès");
        } catch (BusinessException | ConflictException | InvalidOperationException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }
}
