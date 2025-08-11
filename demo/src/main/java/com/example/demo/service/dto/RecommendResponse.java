package com.example.demo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendResponse {
    private List<String> keywords;
    private List<PlaylistWrapper> playlists;
}