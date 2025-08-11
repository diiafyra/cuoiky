package com.example.demo.web.vm;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaylistCardVM {
    private String externalId;
    private String title;
    private String ownerName;
    private String coverUrl;
    private String externalUrl;
    private Integer totalTracks;
    private String snapshotId;
}
