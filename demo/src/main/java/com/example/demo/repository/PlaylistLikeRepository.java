package com.example.demo.repository;

import com.example.demo.domain.PlaylistLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {

    boolean existsByUidAndPlaylist_Id(String uid, Long playlistId);
    Optional<PlaylistLike> findByUidAndPlaylist_Id(String uid, Long playlistId);
    void deleteByUidAndPlaylist_Id(String uid, Long playlistId);

    @EntityGraph(attributePaths = "playlist")
    Page<PlaylistLike> findByUidOrderByCreatedAtDesc(String uid, Pageable pageable);
}
