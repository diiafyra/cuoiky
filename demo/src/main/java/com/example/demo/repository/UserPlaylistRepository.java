package com.example.demo.repository;

import com.example.demo.domain.UserPlaylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlaylistRepository extends JpaRepository<UserPlaylist, Long> {

    @EntityGraph(attributePaths = "playlist")
    Page<UserPlaylist> findByUidOrderByCreatedAtDesc(String uid, Pageable pageable);
  boolean existsByUidAndPlaylist_Id(String uid, Long playlistId);

}
