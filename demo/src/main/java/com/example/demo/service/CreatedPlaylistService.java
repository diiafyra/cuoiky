// src/main/java/com/example/demo/service/CreatedPlaylistService.java
package com.example.demo.service;

import com.example.demo.domain.Playlist;
import com.example.demo.domain.UserPlaylist;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.UserPlaylistRepository;
import com.example.demo.service.dto.search.CreatePlaylistNoStoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatedPlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserPlaylistRepository userPlaylistRepository;


    @Transactional
    public Playlist saveCreated(String uid, CreatePlaylistNoStoreResponse.CreatedPayload p) {
        final String extId = p.getId();

        // 1) Upsert playlist theo (externalId, source)
        Playlist pl = playlistRepository.findByExternalId(extId)
                .orElseGet(() -> Playlist.builder()
                        .externalId(extId)
                        .externalUrl("https://open.spotify.com/playlist/" + extId)
                        .title(p.getName())
                        .description(p.getDescription())
                        .coverUrl(p.getImageUrl())
                        .ownerName("You") // hoặc lấy từ API nếu có
                        .totalTracks(p.getTotalTracks())
                        .build());

        boolean dirty = pl.getId() == null;
        if (!p.getName().equals(pl.getTitle())) { pl.setTitle(p.getName()); dirty = true; }
        if (p.getDescription() != null && !p.getDescription().equals(pl.getDescription())) { pl.setDescription(p.getDescription()); dirty = true; }
        if (p.getImageUrl() != null && !p.getImageUrl().equals(pl.getCoverUrl())) { pl.setCoverUrl(p.getImageUrl()); dirty = true; }
        if (p.getTotalTracks() != null && !p.getTotalTracks().equals(pl.getTotalTracks())) { pl.setTotalTracks(p.getTotalTracks()); dirty = true; }

        if (dirty) pl = playlistRepository.save(pl);

        // 2) Ghi quan hệ sở hữu qua bảng user_playlists
        if (!userPlaylistRepository.existsByUidAndPlaylist_Id(uid, pl.getId())) {
            userPlaylistRepository.save(UserPlaylist.builder()
                    .uid(uid)
                    .playlist(pl)
                    .build());
        }

        return pl;
    }
}
