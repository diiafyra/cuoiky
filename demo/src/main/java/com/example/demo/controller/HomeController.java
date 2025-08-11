// src/main/java/com/example/demo/controller/HomeController.java
package com.example.demo.controller;

import com.example.demo.service.HomeQueryService;
import com.example.demo.service.dto.UserDTO;
import com.example.demo.viewmodel.PlaylistView;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final HomeQueryService homeQuery;

    public HomeController(HomeQueryService homeQuery) {
        this.homeQuery = homeQuery;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && (auth.getPrincipal() instanceof UserDTO);

        String uid = isAuthenticated ? ((UserDTO) auth.getPrincipal()).getUid() : null;

        List<String> keywords = List.of("Discover Weekly", "Discover Monthly", "Trending");
        List<PlaylistView> explore  = homeQuery.fetchExplore(uid, keywords, null, 12);
        List<PlaylistView> personal = isAuthenticated
                ? homeQuery.fetchPersonal(uid, null, null)
                : List.of();

        // KHÔNG add isAuthenticated / userAvatar ở đây nữa (đã có GlobalModelAttributes + client lo avatar)
        model.addAttribute("personal", personal);
        model.addAttribute("explore", explore);
        return "home";
    }
}
