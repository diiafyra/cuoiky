package com.example.demo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDto {
    private String id;
    private String name;
    private String description;
    @JsonProperty("external_urls") private ExternalUrls externalUrls;
    private String href;
    private List<ImageRef> images;
    private Owner owner;
    @JsonProperty("snapshot_id") private String snapshotId;
    private TracksMeta tracks;
    private String uri;
    private Boolean collaborative;
    @JsonProperty("public") private Boolean publicFlag;
    @JsonProperty("primary_color") private String primaryColor;
    private String type;
}