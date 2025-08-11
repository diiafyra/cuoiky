package com.example.demo.service;

import com.example.demo.service.dto.ExploreResponse;
import com.example.demo.service.dto.PlaylistDto;
import com.example.demo.service.dto.PlaylistWrapper;
import com.example.demo.service.dto.RecommendResponse;
import com.example.demo.viewmodel.PlaylistView;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HomeQueryService {

    private final ExternalApiClient api;
    private final MusicProfileLiteService musicProfileLiteService;

    public HomeQueryService(ExternalApiClient api, MusicProfileLiteService musicProfileLiteService) {
        this.api = api;
        this.musicProfileLiteService = musicProfileLiteService;
    }

    public List<PlaylistView> fetchPersonal(String uid, String moodText, Map<String,Object> circumplex) {
        Map<String,Object> body = new HashMap<>();
        if (uid != null) body.put("uid", uid);
        if (moodText != null) body.put("moodText", moodText);
        if (circumplex != null && !circumplex.isEmpty()) body.put("circumplex", circumplex);

        musicProfileLiteService.loadMusicProfileAsMap(uid).ifPresent(mp -> body.put("musicProfile", mp));

        
        RecommendResponse res = api.getPersonalAsync(body).block(Duration.ofSeconds(5));
        if (res == null || res.getPlaylists() == null) return List.of();

        return res.getPlaylists().stream()
                .map(PlaylistWrapper::getPlaylist)
                .filter(Objects::nonNull)
                .map(dto -> PlaylistView.from(dto))
                .collect(Collectors.toList());
    }

    public List<PlaylistView> fetchExplore(String uid, List<String> keywords, Integer maxPerKeyword, Integer maxTotal) {
        Map<String,Object> body = new HashMap<>();
        if (keywords != null && !keywords.isEmpty()) body.put("keywords", keywords);
        if (maxPerKeyword != null) body.put("maxPerKeyword", maxPerKeyword);
        if (maxTotal != null) body.put("maxTotal", maxTotal);

        if (uid != null) body.put("uid", uid);
        musicProfileLiteService.loadMusicProfileAsMap(uid).ifPresent(mp -> body.put("musicProfile", mp));


        ExploreResponse res = api.getExploreAsync(body).block(Duration.ofSeconds(5));
        if (res == null || res.getPlaylists() == null) return List.of();

        return res.getPlaylists().stream()
                .map(PlaylistWrapper::getPlaylist)
                .filter(Objects::nonNull)
                .map(dto -> PlaylistView.from(dto))
                .collect(Collectors.toList());
    }
}
