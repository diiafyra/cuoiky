package com.example.demo.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.example.demo.domain.Playlist;
import com.example.demo.service.dto.PlaylistDto;


public class PlaylistMapper {
    public static Playlist toEntity(PlaylistDto dto) {
        if (dto == null) return null;
        String cover = (dto.getImages()!=null && !dto.getImages().isEmpty()) ? dto.getImages().get(0).getUrl() : null;
        return Playlist.builder()
                .externalId(dto.getId())
                .title(dto.getName())
                .description(StringUtils.defaultString(dto.getDescription()))
                .externalUrl(dto.getExternalUrls()!=null? dto.getExternalUrls().getSpotify(): null)
                .coverUrl(cover)
                .ownerName(dto.getOwner()!=null? dto.getOwner().getDisplayName(): null)
                .totalTracks(dto.getTracks()!=null? dto.getTracks().getTotal(): null)
                .snapshotId(dto.getSnapshotId())
                .build();
    }

}