package com.example.demo.controller;

import com.example.demo.service.ExternalApiClient;
import com.example.demo.service.MusicProfileLiteService;
import com.example.demo.service.RecommendLogService;
import com.example.demo.service.dto.RecommendResponse;
import com.example.demo.viewmodel.PlaylistView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationApi {

  private final ExternalApiClient api;
  private final RecommendLogService logs;
  private final MusicProfileLiteService musicProfile;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @PostMapping(path = "/mood", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, Object> fromMood(
      @RequestPart("moodText") String moodText,
      @RequestPart("circumplex") String circumplexJson,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @RequestAttribute(value = "uid", required = false) String uid) {

    try {
      if (uid == null) uid = "anonymous";

      // Parse circumplex JSON -> Map<String,Object>
      Map<String, Object> circumplex = parseCircumplex(circumplexJson);
      double val = parseDouble(circumplex.get("valence"));
      double aro = parseDouble(circumplex.get("arousal"));

      // Build req map (text/json parts)
      Map<String, Object> req = new HashMap<>();
      req.put("uid", uid);
      req.put("moodText", moodText);
      req.put("circumplex", circumplex);
      musicProfile.loadMusicProfileAsMap(uid).ifPresent(mp -> req.put("musicProfile", mp));

      // Optional image file -> truyền bytes/metas để ExternalApiClient add file-part
      if (image != null && !image.isEmpty()) {
        req.put("imageBytes", image.getBytes());
        req.put("imageFilename", Optional.ofNullable(image.getOriginalFilename()).orElse("mood.png"));
        req.put("imageContentType",
            Optional.ofNullable(image.getContentType()).orElse(MediaType.IMAGE_PNG_VALUE));
      }

      // Call core
      RecommendResponse res = api.getPersonalAsync(req).block(Duration.ofSeconds(12));
      if (res == null) res = new RecommendResponse();

      // Map playlists (đơn giản hoá cho UI lưu vào log)
      List<PlaylistView> simple = Optional.ofNullable(res.getPlaylists())
          .orElse(Collections.emptyList())
          .stream()
          .map(w -> w.getPlaylist())
          .filter(Objects::nonNull)
          .map(PlaylistView::from)
          .collect(Collectors.toList());

      // Lưu log (keywords, raw playlists wrapper, simple, imageUrl từ core)
      var saved = logs.save(
          uid,
          moodText,
          val,
          aro,
          res.getKeywords(),
          res.getPlaylists(),
          simple,
          res.getImageUrl()
      );

      return Map.of("id", saved.getId(), "ok", true);

    } catch (Exception e) {
      return Map.of("ok", false, "error", e.getMessage());
    }
  }

  private Map<String, Object> parseCircumplex(String json) {
    try {
      if (json == null || json.isBlank()) return Map.of();
      return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      // Nếu parse lỗi thì trả rỗng để không vỡ flow
      return Map.of();
    }
  }

  private double parseDouble(Object o) {
    try {
      return (o == null) ? 0d : Double.parseDouble(String.valueOf(o));
    } catch (Exception e) {
      return 0d;
    }
  }
}
