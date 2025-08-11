package com.example.demo.web.dto;

import lombok.*;

import java.util.List;

public class LikeDtos {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ToggleRequest {
        private String externalId;

        // Các field dưới đây dùng để "upsert" playlist nếu chưa có
        private String title;
        private String description;
        private String externalUrl;
        private String coverUrl;
        private String ownerName;
        private Integer totalTracks;
        private String snapshotId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ToggleResponse {
        private boolean liked;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatusItem {
        private String externalId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatusRequest {
        private List<StatusItem> items;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatusEntry {
        private String externalId;
        private boolean liked;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatusResponse {
        private List<StatusEntry> results;
    }
}
