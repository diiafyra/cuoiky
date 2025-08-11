// src/main/java/com/example/demo/controller/PlaylistsController.java
package com.example.demo.controller;

import com.example.demo.service.PlaylistsPageService;
import com.example.demo.service.dto.UserDTO;
import com.example.demo.web.vm.PlaylistCardVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PlaylistsController {

    private final PlaylistsPageService svc;

    private String uidOrNull(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDTO u)) return null;
        return u.getUid();
    }

    @GetMapping("/me/playlists")
    public String myPlaylists(Authentication auth,
                              Model model,
                              @RequestParam(defaultValue = "0") int pageLiked,
                              @RequestParam(defaultValue = "0") int pageOwned,
                              @RequestParam(defaultValue = "24") int size) {

        String uid = uidOrNull(auth);
        if (uid == null) {
            // chưa đăng nhập: trả trang trống (hoặc redirect /login tùy bạn)
            model.addAttribute("liked", Page.empty());
            model.addAttribute("owned", Page.empty());
            return "playlists"; // trỏ đúng tên template bạn đang dùng
        }

        Page<PlaylistCardVM> liked = svc.getLiked(uid, pageLiked, size);
        Page<PlaylistCardVM> owned = svc.getOwned(uid, pageOwned, size);

        model.addAttribute("liked", liked);
        model.addAttribute("owned", owned);
        return "playlists";
    }
}
