package com.example.demo.service;

import com.example.demo.repository.PlaylistLikeRepository;
import com.example.demo.repository.UserPlaylistRepository;
import com.example.demo.web.vm.PlaylistCardVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistsPageService {

    private final PlaylistLikeRepository likeRepo;
    private final UserPlaylistRepository userPlaylistRepo;

    public Page<PlaylistCardVM> getLiked(String uid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageLikes = likeRepo.findByUidOrderByCreatedAtDesc(uid, pageable);
        List<PlaylistCardVM> items = pageLikes.getContent().stream()
                .map(l -> {
                    var p = l.getPlaylist();
                    return PlaylistCardVM.builder()
                            .externalId(p.getExternalId())
                            .title(p.getTitle())
                            .ownerName(p.getOwnerName())
                            .coverUrl(p.getCoverUrl())
                            .externalUrl(p.getExternalUrl())
                            .totalTracks(p.getTotalTracks())
                            .snapshotId(p.getSnapshotId())
                            .build();
                })
                .toList();
        return new PageImpl<>(items, pageable, pageLikes.getTotalElements());
    }

    public Page<PlaylistCardVM> getOwned(String uid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageOwned = userPlaylistRepo.findByUidOrderByCreatedAtDesc(uid, pageable);
        List<PlaylistCardVM> items = pageOwned.getContent().stream()
                .map(up -> {
                    var p = up.getPlaylist();
                    return PlaylistCardVM.builder()
                            .externalId(p.getExternalId())
                            .title(p.getTitle())
                            .ownerName(p.getOwnerName())
                            .coverUrl(p.getCoverUrl())
                            .externalUrl(p.getExternalUrl())
                            .totalTracks(p.getTotalTracks())
                            .snapshotId(p.getSnapshotId())
                            .build();
                })
                .toList();
        return new PageImpl<>(items, pageable, pageOwned.getTotalElements());
    }
}
