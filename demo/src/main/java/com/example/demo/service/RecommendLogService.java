// src/main/java/com/example/demo/service/RecommendLogService.java
package com.example.demo.service;

import com.example.demo.domain.RecommendLog;
import com.example.demo.repository.RecommendLogRepository;
import com.example.demo.viewmodel.PlaylistView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendLogService {
  private final RecommendLogRepository repo;
  private static final ObjectMapper M = new ObjectMapper();

  private static String toJson(Object o){
    try { return o==null? null : M.writeValueAsString(o); }
    catch (Exception e){ return null; }
  }

  public RecommendLog save(String uid, String moodText, Double valence, Double arousal,
                           List<String> keywords, Object rawPlaylists, List<PlaylistView> simple, String imageUrl) {
    var e = RecommendLog.builder()
      .uid(uid)
      .moodText(moodText)
      .valence(valence)
      .arousal(arousal)
      .imageUrl(imageUrl)
      .keywordsJson(toJson(keywords))
      .playlistsJson(toJson(rawPlaylists))
      .simplePlaylistsJson(toJson(simple))
      .totalPlaylists(simple != null ? simple.size() : 0)
      .build();
    return repo.save(e);
  }
public List<RecommendLog> findAllByUidOrderByCreatedAtDesc(String uid){
  var list = repo.findByUidOrderByCreatedAtDesc(uid);
  list.forEach(log -> {
    try {
      if (log.getKeywords() == null && log.getKeywordsJson() != null) {
        var arr = new com.fasterxml.jackson.databind.ObjectMapper()
            .readValue(log.getKeywordsJson(), String[].class);
        log.setKeywords(java.util.Arrays.asList(arr));
      }
    } catch (Exception ignored) {}
  });
  return list;
}

}
