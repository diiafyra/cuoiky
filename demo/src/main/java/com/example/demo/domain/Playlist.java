// src/main/java/com/example/demo/domain/Playlist.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
  name = "playlists",
  uniqueConstraints = @UniqueConstraint(
    name = "uq_playlists_extid_source",
    columnNames = {"external_id","source"}
  )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Playlist {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name="external_id", nullable=false, length=64)
  private String externalId;

  @Column(nullable=false, length=255)
  private String title;

  @Column(columnDefinition="TEXT")
  private String description;

  @Column(name="external_url", nullable=false, columnDefinition="TEXT")
  private String externalUrl;

  @Column(name="cover_url", columnDefinition="TEXT")
  private String coverUrl;

  @Column(name="owner_name", length=255)
  private String ownerName;

  @Column(name="total_tracks")
  private Integer totalTracks;

  @Column(name="snapshot_id", length=128)
  private String snapshotId;

  @CreationTimestamp
  @Column(name="fetched_at", nullable=false)
  private OffsetDateTime fetchedAt;
}
