// src/main/java/com/example/demo/domain/RecommendLog.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "recommend_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendLog {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length=128)              private String uid;
  @Column(columnDefinition="TEXT") private String moodText;
  private Double valence;
  private Double arousal;

  // Ảnh canvas đã lưu file => URL public
  @Column(columnDefinition="TEXT") private String imageUrl;

  // JSON gốc trả về từ core (để xem lại)
  @Column(columnDefinition="TEXT") private String keywordsJson;     // ["k1","k2",...]
  @Column(columnDefinition="TEXT") private String playlistsJson;    // array object (thô)
  private List<String> keywords;

  // Danh sách playlist rút gọn cho UI (id, title, owner, cover, externalUrl, totalTracks)
  @Column(columnDefinition="TEXT") private String simplePlaylistsJson;

  private Integer totalPlaylists;

  @CreationTimestamp
  @Column(nullable=false) private OffsetDateTime createdAt;
}
