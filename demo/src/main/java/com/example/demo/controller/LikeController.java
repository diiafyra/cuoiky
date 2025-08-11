package com.example.demo.controller;

import com.example.demo.service.LikeService;
import com.example.demo.service.dto.UserDTO;
import com.example.demo.web.dto.LikeDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    private String requireUid(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDTO u)) {
            throw new RuntimeException("Unauthorized");
        }
        return u.getUid();
    }

    @PostMapping("/toggle")
    public ResponseEntity<ToggleResponse> toggle(@RequestBody ToggleRequest req, Authentication auth) {
        String uid = requireUid(auth);
        return ResponseEntity.ok(likeService.toggle(uid, req));
    }

    @PostMapping("/status")
    public ResponseEntity<StatusResponse> status(@RequestBody StatusRequest req, Authentication auth) {
        String uid = requireUid(auth);
        return ResponseEntity.ok(likeService.statuses(uid, req.getItems()));
    }
}
