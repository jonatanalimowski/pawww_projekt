package com.example.projekt;

import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Kontroler, zarzadza adresami pod ktorymi jest logowanie/rejestracja oraz logika i laczeniem ze spring security
@Controller
public class AuthenticationController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error_user", "Taki użytkownik już istnieje!");
        }

        if (result.hasErrors()) {
            return "register"; // w razie bledow walidacji wyswietlamy formularz ponownie
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_FULL_USER");

        userRepository.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal User loggedInUser) {
        if (loggedInUser != null) {
            model.addAttribute("username", loggedInUser.getUsername());
        } else {
            return "redirect:/login";
        }
        return "home";
    }
}
