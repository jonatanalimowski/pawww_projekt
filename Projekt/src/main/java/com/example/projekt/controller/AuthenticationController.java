package com.example.projekt.controller;

import com.example.projekt.model.Role;
import com.example.projekt.model.User;
import com.example.projekt.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_LIMITED);

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
