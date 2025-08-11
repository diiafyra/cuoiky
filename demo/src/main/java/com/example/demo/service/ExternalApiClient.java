package com.example.demo.service;

import com.example.demo.service.dto.ExploreResponse;
import com.example.demo.service.dto.RecommendResponse;
import com.example.demo.service.dto.search.CreatePlaylistNoStoreResponse;
import com.example.demo.service.dto.search.SearchTracksResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Service
public class ExternalApiClient {
    private final WebClient webClient;
    private final String recommendUrl;
    private final String exploreUrl;
    private final String searchTracksUrl;
    private final String createPlaylistUrl;

    // Dùng chung cho việc serialize JSON vào từng part
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ExternalApiClient(
            WebClient webClient,
            @Value("${moodsic.api.recommend-url}") String recommendUrl,
            @Value("${moodsic.api.explore-url}") String exploreUrl,
            @Value("${moodsic.api.search-tracks-url}") String searchTracksUrl,
            @Value("${moodsic.api.create-playlists-url}") String createPlaylistsUrl) {
        this.webClient = webClient;
        this.recommendUrl = recommendUrl;
        this.exploreUrl = exploreUrl;
        this.searchTracksUrl = searchTracksUrl;
        this.createPlaylistUrl = createPlaylistsUrl;

    }

    private static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize error", e);
        }
    }

    /**
     * PERSONAL: API yêu cầu multipart/form-data
     * - Text fields: uid, moodText, accessToken, refreshToken -> text/plain
     * - Object/Map: circumplex, musicProfile -> serialize JSON và đặt Content-Type:
     * application/json cho từng part
     */
    public Mono<RecommendResponse> getPersonalAsync(Map<String, Object> req) {
        System.out.println("===== CALL API PERSONAL (multipart/form-data) =====");
        System.out.println("[URL] " + recommendUrl);
        System.out.println("[Method] POST");
        System.out.println("[Body keys] " + (req != null ? req.keySet() : "null"));

        MultipartBodyBuilder mb = new MultipartBodyBuilder();

        // helper add text
        java.util.function.BiConsumer<String, Object> addText = (k, v) -> {
            if (v != null)
                mb.part(k, String.valueOf(v));
        };

        if (req != null) {
            // text parts
            addText.accept("uid", req.get("uid"));
            addText.accept("moodText", req.get("moodText"));
            addText.accept("accessToken", req.get("accessToken"));
            addText.accept("refreshToken", req.get("refreshToken"));

            // json parts
            if (req.get("circumplex") != null) {
                mb.part("circumplex", toJson(req.get("circumplex")))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            }
            if (req.get("musicProfile") != null) {
                mb.part("musicProfile", toJson(req.get("musicProfile")))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            }
        }

        return webClient.post()
                .uri(recommendUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(mb.build())
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> {
                                    System.out.println("[PERSONAL ERROR BODY] " + body);
                                    return new RuntimeException("Recommend API error: " + body);
                                }))
                .bodyToMono(RecommendResponse.class)
                .doOnNext(res -> System.out.println("[PERSONAL RESPONSE] " + res))
                .timeout(java.time.Duration.ofSeconds(5))
                .onErrorResume(ex -> {
                    System.out.println("[PERSONAL ERROR] " + ex.getMessage());
                    return Mono.just(new RecommendResponse());
                });
    }

    /**
     * EXPLORE: API nhận application/json như trước
     */
    public Mono<ExploreResponse> getExploreAsync(Map<String, Object> req) {
        System.out.println("===== CALL API EXPLORE =====");
        System.out.println("[URL] " + exploreUrl);
        System.out.println("[Method] POST");
        System.out.println("[Headers] Content-Type: application/json");
        System.out.println("[Body] " + req);

        return webClient.post()
                .uri(exploreUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Objects.requireNonNullElseGet(req, java.util.HashMap::new))
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> {
                                    System.out.println("[EXPLORE ERROR BODY] " + body);
                                    return new RuntimeException("Explore API error: " + body);
                                }))
                .bodyToMono(ExploreResponse.class)
                .doOnNext(res -> System.out.println("[EXPLORE RESPONSE] " + res))
                .timeout(java.time.Duration.ofSeconds(5))
                .onErrorResume(ex -> {
                    System.out.println("[EXPLORE ERROR] " + ex.getMessage());
                    return Mono.just(new ExploreResponse());
                });
    }

    // ========== NEW: SEARCH TRACKS (no-store) ==========
    public Mono<SearchTracksResponse> searchTracksNoStore(Map<String, Object> req) {
        System.out.println("===== CALL API SEARCH TRACKS (no-store) =====");
        System.out.println("[URL] " + searchTracksUrl);
        System.out.println("[Method] POST");
        System.out.println("[Headers] Content-Type: application/json");
        System.out.println("[Body] " + req);

        return webClient.post()
                .uri(searchTracksUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Objects.requireNonNullElseGet(req, java.util.HashMap::new))
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> {
                                    System.out.println("[SEARCH TRACKS ERROR BODY] " + body);
                                    return new RuntimeException("SearchTracks API error: " + body);
                                }))
                .bodyToMono(SearchTracksResponse.class)
                .doOnNext(res -> System.out.println("[SEARCH TRACKS RESPONSE] " + res))
                .timeout(java.time.Duration.ofSeconds(6))
                .onErrorResume(ex -> {
                    System.out.println("[SEARCH TRACKS ERROR] " + ex.getMessage());
                    return Mono.just(new SearchTracksResponse());
                });
    }

    // ========== NEW: CREATE PLAYLIST (no-store) ==========
    public Mono<CreatePlaylistNoStoreResponse> createPlaylistNoStore(Map<String, Object> req) {
        System.out.println("===== CALL API CREATE PLAYLIST (no-store) =====");
        System.out.println("[URL] " + createPlaylistUrl);
        System.out.println("[Method] POST");
        System.out.println("[Headers] Content-Type: application/json");
        System.out.println("[Body before default accessToken] " + req);

        final String TEST_ACCESS = "BQCt1da5EU0PM_x0-HC9nnxAWMWu1dgNR-CHJ7wKw71BAus8NUcqeLFQJOfXTO4mQ7-_mSePcDogbWBXbLC3DuGGJYxiAvt5ZIAkzOaQmGq-K7iLfv7iqDLltfIPs1SxJpPhBa8V-Oc20uSV3f_CdwlS8dPKJruom8gTpoDn9qVlPaG24MjxXwwp1zey8SlURqtgih4tyGfUvLwGljgk6QJGvS2yfODfNphNkjLY-JUKr5uLtRH3utP3QSIyyXvIwH9PpN29wOA-QtQLebmGNww3dje41hu7sAkF_r_YRh4w7OcTvS2VbV22D6cctRGcWGE";

        var body = new java.util.HashMap<String, Object>();
        if (req != null)
            body.putAll(req);
        body.putIfAbsent("accessToken", TEST_ACCESS);

        System.out.println("[Body after default accessToken] " + body);

        return webClient.post()
                .uri(createPlaylistUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(errorBody -> {
                                    System.out.println("[CREATE PLAYLIST ERROR BODY] " + errorBody);
                                    return new RuntimeException("CreatePlaylist API error: " + errorBody);
                                }))
                .bodyToMono(CreatePlaylistNoStoreResponse.class)
                .doOnNext(res -> System.out.println("[CREATE PLAYLIST RESPONSE] " + res))
                .timeout(java.time.Duration.ofSeconds(8))
                .onErrorResume(ex -> {
                    System.out.println("[CREATE PLAYLIST ERROR] " + ex.getMessage());
                    return Mono.just(new CreatePlaylistNoStoreResponse());
                });
    }
}
