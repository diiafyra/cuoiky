package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "music_profiles_lite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MusicProfileLite {

    @Id
    @Column(name = "uid", length = 64, nullable = false)
    private String uid; // Firebase UID

    @Column(name = "vibe", length = 16) // CHILL | HAPPY | FOCUS | PARTY
    private String vibe;

    @Column(name = "favorite_genres", columnDefinition = "TEXT")
    private String favoriteGenres;   // "pop,rock,indie"

    @Column(name = "favorite_artists", columnDefinition = "TEXT")
    private String favoriteArtists;  // "Taylor Swift,Drake"

    @Column(name = "languages", columnDefinition = "TEXT")
    private String languages;        // "en,vi,ko"

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
