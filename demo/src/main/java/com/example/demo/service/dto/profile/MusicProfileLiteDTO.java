package com.example.demo.service.dto.profile;

import lombok.Data;
import java.util.List;

@Data
public class MusicProfileLiteDTO {
    private String vibe;                   // CHILL | HAPPY | FOCUS | PARTY
    private List<String> favoriteGenres;   // ["pop","rock"]
    private List<String> favoriteArtists;  // ["Taylor Swift","Drake"]
    private List<String> languages;        // ["en","vi"]
}
