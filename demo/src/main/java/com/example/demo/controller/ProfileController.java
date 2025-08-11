package com.example.demo.controller;

import com.example.demo.service.MusicProfileLiteService;
import com.example.demo.service.dto.UserDTO;
import com.example.demo.service.dto.profile.MusicProfileLiteDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final MusicProfileLiteService musicProfileLiteService;

    // Trang profile
    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        boolean isAuthenticated = auth != null && auth.getPrincipal() instanceof UserDTO;
        if (isAuthenticated) {
            UserDTO u = (UserDTO) auth.getPrincipal();
            model.addAttribute("userEmail", u.getEmail()); // hiển thị email
            model.addAttribute("displayName", u.getDisplayName()); // nếu có
        } else {
            model.addAttribute("userEmail", "");
            model.addAttribute("displayName", "");
        }
        // avatar sẽ lấy từ cookie 'avatarUrl' ở FE
        return "profile";
    }

    @GetMapping("/api/me/music-profile-lite")
    @ResponseBody
    public ResponseEntity<MusicProfileLiteDTO> getLite(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof com.example.demo.service.dto.UserDTO u)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(musicProfileLiteService.get(u.getUid()));
    }

    @PutMapping("/api/me/music-profile-lite")
    @ResponseBody
    public ResponseEntity<MusicProfileLiteDTO> upsertLite(Authentication auth,
            @RequestBody MusicProfileLiteDTO body) {
        if (auth == null || !(auth.getPrincipal() instanceof com.example.demo.service.dto.UserDTO u)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(musicProfileLiteService.upsert(u.getUid(), body));
    }
}
