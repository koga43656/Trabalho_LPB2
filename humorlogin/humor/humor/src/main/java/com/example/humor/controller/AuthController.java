package com.example.humor.controller;

import com.example.humor.model.User;
import com.example.humor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Página de login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // carrega templates/login.html
    }

    // Página de cadastro
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // carrega templates/register.html
    }

    // Cadastro (POST)
    @PostMapping("/auth/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password) {

        // validações:
        if (username == null || username.trim().isEmpty()) {
            return "redirect:/register?error=username";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=exists_username";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error=exists_email";
        }

        // valida senha (mínimo 6 caracteres)
        if (password.length() < 6) {
            return "redirect:/register?error=weak_password";
        }

        // valida email (tem que conter "@" e ".com")
        if (!email.contains("@") || !email.endsWith(".com")) {
            return "redirect:/register?error=invalid_email";
        }

        // Se passou em tudo → salva usuário
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);

        return "redirect:/login?success=true";
    }

    // Login (POST)
    @PostMapping("/auth/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {
                    session.setAttribute("user", user); // salva usuário na sessão
                    return "redirect:/dashboard";
                })
                .orElse("redirect:/login?error=true");
    }
}


