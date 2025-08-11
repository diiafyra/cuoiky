package com.example.demo.controller;

import com.example.demo.service.AuthService;
import com.example.demo.service.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(RegisterRequest request, Model model) {
        try {
            authService.register(request);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) {
            model.addAttribute("msg", "Registered successfully. Please login.");
        }
        return "login";
    }
}
