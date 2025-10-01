package com.example.humor.controller;

import com.example.humor.model.User;
import com.example.humor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
                               @RequestParam String password) {
        // Verifica se já existe usuário com esse nome
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // ⚠️ recomendável usar BCrypt no futuro
        userRepository.save(user);

        // Após cadastrar, redireciona para a tela de login
        return "redirect:/login?success=true";
    }

    // Login (POST)
    @PostMapping("/auth/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> "redirect:/dashboard") // se login OK
                .orElse("redirect:/login?error=true"); // se falhar
    }
}
