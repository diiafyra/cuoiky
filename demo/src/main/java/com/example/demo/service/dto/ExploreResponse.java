package com.example.demo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ExploreResponse {
    private boolean success;
    private List<String> keywords;
    private Integer total;
    private List<PlaylistWrapper> playlists;
    private TokenMeta tokenMeta;
}