// src/main/java/com/example/demo/service/dto/search/CreatePlaylistNoStoreResponse.java
package com.example.demo.service.dto.search;

import lombok.Data;

@Data
public class CreatePlaylistNoStoreResponse {
    @Data
    public static class CreatedPayload {
        private String id;
        private String name;
        private String description;
        private String imageUrl;
        private String trackHref;
        private Integer totalTracks;
        private Boolean isPublic;
        private String ownerId;
    }

    private Boolean success;
    private String message;
    private CreatedPayload playlist;
    private Object tokenMeta;
}

