package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_playlists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPlaylist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, length = 64)
    private String uid; // chủ sở hữu

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_userplaylist_playlist"))
    private Playlist playlist;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
