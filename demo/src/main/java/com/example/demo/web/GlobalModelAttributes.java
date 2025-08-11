package com.example.demo.web;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("path")
    public String addRequestPath(HttpServletRequest request) {
        return request.getRequestURI(); // ví dụ: "/", "/search", "/me/playlists"
    }

    @ModelAttribute("contextPath")
    public String addContextPath(HttpServletRequest request) {
        String cp = request.getContextPath();
        return (cp != null) ? cp : "";
    }
    @ModelAttribute
    public void injectAuth(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuth = auth != null && auth.isAuthenticated()
                        && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuth);
    }
}
