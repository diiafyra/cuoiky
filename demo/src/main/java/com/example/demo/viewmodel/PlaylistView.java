package com.example.demo.viewmodel;

import com.example.demo.service.dto.PlaylistDto;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaylistView {
    String externalId;
    String title;
    String description;
    String externalUrl;
    String coverUrl;
    String ownerName;
    Integer totalTracks;
    String snapshotId;

    public static PlaylistView from(PlaylistDto dto) {
        String cover = (dto.getImages()!=null && !dto.getImages().isEmpty()) ? dto.getImages().get(0).getUrl() : null;
        String url = dto.getExternalUrls()!=null ? dto.getExternalUrls().getSpotify() : null;
        String owner = dto.getOwner()!=null ? dto.getOwner().getDisplayName() : null;
        Integer total = dto.getTracks()!=null ? dto.getTracks().getTotal() : null;
        return PlaylistView.builder()
                .externalId(dto.getId())
                .title(dto.getName())
                .description(dto.getDescription())
                .externalUrl(url)
                .coverUrl(cover)
                .ownerName(owner)
                .totalTracks(total)
                .snapshotId(dto.getSnapshotId())
                .build();
    }
}
