// src/main/java/com/example/demo/service/dto/search/SearchTracksResponse.java
package com.example.demo.service.dto.search;

import lombok.Data;
import java.util.List;

@Data
public class SearchTracksResponse {
    private Boolean success;
    private String keyword;
    private Integer total;
    private List<TrackDTO> tracks;
    private Object tokenMeta;
}
