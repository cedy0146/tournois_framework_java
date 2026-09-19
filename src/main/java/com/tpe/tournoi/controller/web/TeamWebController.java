package com.tpe.tournoi.controller.web;

import com.tpe.tournoi.dto.request.CreateTeamRequest;
import com.tpe.tournoi.dto.request.UpdateTeamRequest;
import com.tpe.tournoi.exception.*;
import com.tpe.tournoi.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamWebController {

    private final TeamService teamService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("teams",
                teamService.getAll(PageRequest.of(page, size), null, null));
        return "team/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("team", new CreateTeamRequest());
        return "team/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("team") CreateTeamRequest request,
                         BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            return "team/form";
        }
        try {
            teamService.create(request);
            ra.addFlashAttribute("success", "Équipe créée avec succès");
            return "redirect:/teams";
        } catch (ConflictException e) {
            model.addAttribute("error", e.getMessage());
            return "team/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.getById(id));
        return "team/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.getById(id));
        return "team/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("team") UpdateTeamRequest request,
                         RedirectAttributes ra) {
        teamService.update(id, request);
        ra.addFlashAttribute("success", "Équipe mise à jour");
        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        teamService.delete(id);
        ra.addFlashAttribute("success", "Équipe supprimée");
        return "redirect:/teams";
    }
}
