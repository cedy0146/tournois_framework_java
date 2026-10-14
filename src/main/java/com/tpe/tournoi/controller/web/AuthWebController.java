package com.tpe.tournoi.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout) {
        if (error != null) return "redirect:/?error=true";
        if (logout != null) return "redirect:/?logout=true";
        return "redirect:/?login";
    }
}
