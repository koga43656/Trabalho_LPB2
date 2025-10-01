package com.example.humor.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        // Verifica se o usuário está logado
        if (session.getAttribute("user") == null) {
            return "redirect:/login?not_logged=true"; // redireciona se não logado
        }

        return "dashboard"; // carrega templates/dashboard.html
    }
}


