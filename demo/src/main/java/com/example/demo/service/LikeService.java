package com.example.demo.service;

import com.example.demo.domain.Playlist;
import com.example.demo.domain.PlaylistLike;
import com.example.demo.repository.PlaylistLikeRepository;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.web.dto.LikeDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PlaylistRepository playlistRepo;
    private final PlaylistLikeRepository likeRepo;

    @Transactional
    public ToggleResponse toggle(String uid, ToggleRequest req) {
        Playlist playlist = upsertPlaylist(req);

        boolean exists = likeRepo.existsByUidAndPlaylist_Id(uid, playlist.getId());
        if (exists) {
            likeRepo.deleteByUidAndPlaylist_Id(uid, playlist.getId());
            return ToggleResponse.builder().liked(false).build();
        } else {
            PlaylistLike like = PlaylistLike.builder()
                    .uid(uid)
                    .playlist(playlist)
                    .build();
            likeRepo.save(like);
            return ToggleResponse.builder().liked(true).build();
        }
    }

    @Transactional(readOnly = true)
    public StatusResponse statuses(String uid, List<StatusItem> items) {
        List<StatusEntry> out = new ArrayList<>();
        for (StatusItem it : items) {
            Playlist playlist = playlistRepo
                    .findByExternalId(it.getExternalId())
                    .orElse(null);

            boolean liked = false;
            if (playlist != null) {
                liked = likeRepo.existsByUidAndPlaylist_Id(uid, playlist.getId());
            }
            out.add(StatusEntry.builder()
                    .externalId(it.getExternalId())
                    .liked(liked)
                    .build());
        }
        return StatusResponse.builder().results(out).build();
    }

    private Playlist upsertPlaylist(ToggleRequest req) {
        return playlistRepo.findByExternalId(req.getExternalId())
                .orElseGet(() -> {
                    Playlist p = Playlist.builder()
                            .externalId(req.getExternalId())
                            .title(req.getTitle() != null ? req.getTitle() : "")
                            .description(req.getDescription())
                            .externalUrl(req.getExternalUrl())
                            .coverUrl(req.getCoverUrl())
                            .ownerName(req.getOwnerName())
                            .totalTracks(req.getTotalTracks())
                            .snapshotId(req.getSnapshotId())
                            .build();
                    return playlistRepo.save(p);
                });
    }
}
