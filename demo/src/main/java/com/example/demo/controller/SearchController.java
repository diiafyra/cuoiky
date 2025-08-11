// src/main/java/com/example/demo/controller/SearchController.java
package com.example.demo.controller;

import com.example.demo.security.AuthUtils;
import com.example.demo.service.CreatedPlaylistService;
import com.example.demo.service.ExternalApiClient;
import com.example.demo.service.dto.search.CreatePlaylistNoStoreResponse;
import com.example.demo.service.dto.search.SearchTracksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final ExternalApiClient externalApiClient;
    private final CreatedPlaylistService createdPlaylistService;

    // GET page
    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    // Proxy: POST /api/no-store/search-tracks
    @PostMapping("/api/no-store/search-tracks")
    @ResponseBody
    public Mono<SearchTracksResponse> searchTracks(@RequestBody Map<String, Object> body) {
        return externalApiClient.searchTracksNoStore(body);
    }

    // Proxy: POST /api/no-store/create-playlists  -> save DB khi success
// SearchController.java
@PostMapping("/api/no-store/create-playlists")
@ResponseBody
public Mono<ResponseEntity<CreatePlaylistNoStoreResponse>> createPlaylist(@RequestBody Map<String, Object> body) {
    final String uid = AuthUtils.currentUid(); // ✅ LẤY TRƯỚC KHI VÀO REACTIVE
    System.out.println("[CTRL] currentUid=" + uid);

    return externalApiClient.createPlaylistNoStore(body)
        .map(res -> {
            if (Boolean.TRUE.equals(res.getSuccess()) && res.getPlaylist() != null) {
                if (uid == null || uid.isBlank()) {
                    System.out.println("[CTRL] uid is null/blank -> skip saveCreated");
                    return ResponseEntity.status(401).body(res);
                }
                createdPlaylistService.saveCreated(uid, res.getPlaylist());
                return ResponseEntity.ok(res);
            }
            return ResponseEntity.badRequest().body(res);
        });
}

}
