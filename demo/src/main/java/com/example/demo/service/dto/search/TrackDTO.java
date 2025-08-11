package com.example.demo.service.dto.search;

import lombok.Data;
import java.util.List;

@Data
public class TrackDTO {
    private String id;
    private String name;
    private List<ArtistDTO> artists;
    private String artist_names;
    private AlbumDTO album;
    private Integer duration_ms;
    private String duration_formatted;
    private String preview_url;
    private Integer popularity;
    private Boolean explicit;
    private String external_urls;
    private Boolean is_playable;
}
