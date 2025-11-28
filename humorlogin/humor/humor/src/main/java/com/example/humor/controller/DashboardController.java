package com.example.humor.controller;

import com.example.humor.model.User;
import com.example.humor.model.Registro;
import com.example.humor.model.StatusHumor;
import com.example.humor.repository.RegistroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private RegistroRepository registroRepository;

    // Página principal (home)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login?not_logged=true";
        }
        return "dashboard"; // Exibe a página home
    }

    // Página de registro de humor (formulário)
    @GetMapping("/dashboard/registrar")
    public String paginaRegistrarHumor(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login?not_logged=true";
        }

        model.addAttribute("statusList", StatusHumor.values());

        model.addAttribute("tagsList", List.of(
                "Trabalho",
                "Família",
                "Amigos",
                "Ansiedade",
                "Saúde",
                "Relacionamentos",
                "Estudos",
                "Autoestima",
                "Sono"
        ));

        return "registrar";
    }

    @GetMapping("/dashboard/calendario")
    public String paginaCalendario(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login?not_logged=true";
        }
        return "calendario"; // Exibe o arquivo templates/calendario.html
    }

    // Recebe o formulário preenchido
    @PostMapping("/dashboard/registrar")
    public String registrarHumor(@RequestParam StatusHumor status,
                                 @RequestParam String texto,
                                 @RequestParam(required = false) List<String> tags,
                                 HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login?not_logged=true";

        Registro registro = new Registro();
        registro.setUsuario(user);
        registro.setStatus(status);
        registro.setTexto(texto);

        if (tags != null) {
            registro.setTags(tags);
        }

        registroRepository.save(registro);
        return "redirect:/dashboard?success=true";
    }

    @GetMapping("/dashboard/registros")
    public String verRegistros(@RequestParam(required = false) StatusHumor status,
                               HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?not_logged=true";
        }

        List<Registro> registros;

        // Se o usuário escolheu um status no filtro, busca apenas os que têm aquele status
        if (status != null) {
            registros = registroRepository.findByUsuarioAndStatus(user, status);
        } else {
            registros = registroRepository.findByUsuario(user);
        }

        // Garante que a lista de tags seja compatível com o Thymeleaf
        registros.forEach(r -> {
            if (r.getTags() != null) {
                r.setTags(new ArrayList<>(r.getTags()));
            }
        });

        // adiciona as variáveis pro template
        model.addAttribute("registros", registros);
        model.addAttribute("statusOptions", StatusHumor.values());
        model.addAttribute("statusSelecionado", status);

        return "registros";
    }
    @PostMapping("/dashboard/registros/deletar/{id}")
    public String deletarRegistro(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?not_logged=true";
        }

        Registro registro = registroRepository.findById(id).orElse(null);
        if (registro != null && registro.getUsuario().getId().equals(user.getId())) {
            registroRepository.delete(registro);
        }

        return "redirect:/dashboard/registros?deleted=true";
    }

    @GetMapping("/dashboard/registros/{data}")
    @ResponseBody
    public List<Registro> listarPorData(@PathVariable String data, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ArrayList<>();
        }

        LocalDate dia = LocalDate.parse(data);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.plusDays(1).atStartOfDay();

        return registroRepository.findByUsuarioAndDataHoraBetween(user, inicio, fim);
    }

    @GetMapping("/dashboard/registros/resumo")
    @ResponseBody
    public Map<LocalDate, StatusHumor> resumoPorDia(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new HashMap<>();
        }

        List<Registro> registros = registroRepository.findByUsuario(user);
        Map<LocalDate, StatusHumor> resumo = new HashMap<>();

        registros.stream()
                .collect(Collectors.groupingBy(r -> r.getDataHora().toLocalDate()))
                .forEach((data, lista) -> {
                    // Conta qual humor apareceu mais no dia
                    StatusHumor humorPredominante = lista.stream()
                            .collect(Collectors.groupingBy(Registro::getStatus, Collectors.counting()))
                            .entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .get().getKey();

                    resumo.put(data, humorPredominante);
                });

        return resumo;
    }

}
