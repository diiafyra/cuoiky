package com.example.demo.service;

import com.example.demo.domain.MusicProfileLite;
import com.example.demo.repository.MusicProfileLiteRepository;
import com.example.demo.service.dto.profile.MusicProfileLiteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicProfileLiteService {

    private final MusicProfileLiteRepository repo;

    // -------------------- CSV helpers (internal) --------------------
    private static String toCsv(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.joining(","));
    }
    private static List<String> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    // ----------------------------------------------------------------
    // get(uid): Lấy profile cho trang Profile (form) 
    // Dùng ở: ProfileController.GET /profile (đổ dữ liệu lên form)
    // ----------------------------------------------------------------
    public MusicProfileLiteDTO get(String uid) {
        var e = repo.findById(uid).orElse(null);
        var dto = new MusicProfileLiteDTO();
        if (e == null) return dto;

        dto.setVibe(e.getVibe());
        dto.setFavoriteGenres(fromCsv(e.getFavoriteGenres()));
        dto.setFavoriteArtists(fromCsv(e.getFavoriteArtists()));
        dto.setLanguages(fromCsv(e.getLanguages()));
        return dto;
    }

    // ----------------------------------------------------------------
    // upsert(uid, dto): Lưu/Sửa profile từ trang Profile
    // Dùng ở: ProfileController.POST /api/profile (submit form)
    // ----------------------------------------------------------------
    @Transactional
    public MusicProfileLiteDTO upsert(String uid, MusicProfileLiteDTO dto) {
        var e = repo.findById(uid).orElseGet(() -> {
            var n = new MusicProfileLite();
            n.setUid(uid);
            return n;
        });
        e.setVibe(dto.getVibe());
        e.setFavoriteGenres(toCsv(dto.getFavoriteGenres()));
        e.setFavoriteArtists(toCsv(dto.getFavoriteArtists()));
        e.setLanguages(toCsv(dto.getLanguages()));
        repo.save(e);
        return get(uid);
    }

    // ----------------------------------------------------------------
    // loadMusicProfileAsMap(uid): Trả Map để gửi sang API recommend/explore
    // Dùng ở: HomeQueryService.fetchPersonal / fetchExplore
    //   -> musicProfileLiteService.loadMusicProfileAsMap(uid).ifPresent(mp -> body.put("musicProfile", mp));
    // Format trả về (tối giản, optional fields):
    //   { genres:[], artists:[], languages:[], vibe:"CHILL", vibes:["CHILL"] }
    // ----------------------------------------------------------------
    public Optional<Map<String, Object>> loadMusicProfileAsMap(String uid) {
        System.out.println("===== LOAD MUSIC PROFILE (lite) =====");
        System.out.println("[uid] " + uid);
        if (uid == null || uid.isBlank()) {
            System.out.println("[MP-LITE] uid is blank -> return empty");
            return Optional.empty();
        }

        return repo.findById(uid).map(e -> {
            Map<String, Object> out = new LinkedHashMap<>();

            var genres    = fromCsv(e.getFavoriteGenres());
            var artists   = fromCsv(e.getFavoriteArtists());
            var languages = fromCsv(e.getLanguages());
            var vibe      = e.getVibe();

            if (!genres.isEmpty())    out.put("genres", genres);
            if (!artists.isEmpty())   out.put("artists", artists);
            if (!languages.isEmpty()) out.put("languages", languages);
            if (vibe != null && !vibe.isBlank()) {
                out.put("vibe", vibe);
                out.put("vibes", List.of(vibe)); // giữ tương thích nếu phía API đọc 'vibes'
            }

            System.out.println("[MP-LITE] map => " + out);
            return out.isEmpty() ? null : out;
        }).filter(Objects::nonNull);
    }

    // ----------------------------------------------------------------
    // (Legacy OPTIONAL) toRecommendProfile: map theo cấu trúc cũ (min/max)
    // Chỉ giữ nếu còn nơi nào gọi API old-format; không dùng thì có thể XOÁ.
    // ----------------------------------------------------------------
    public Map<String,Object> toRecommendProfile(MusicProfileLiteDTO lite) {
        var map = new HashMap<String,Object>();

        double[] energy = {0.3, 0.7};
        double[] val    = {0.4, 0.8};
        switch ((lite.getVibe() == null ? "" : lite.getVibe().toUpperCase())) {
            case "CHILL" -> { energy = new double[]{0.2, 0.5}; val = new double[]{0.3, 0.6}; }
            case "HAPPY" -> { energy = new double[]{0.5, 0.9}; val = new double[]{0.6, 1.0}; }
            case "FOCUS" -> { energy = new double[]{0.2, 0.6}; val = new double[]{0.2, 0.6}; }
            case "PARTY" -> { energy = new double[]{0.7, 1.0}; val = new double[]{0.6, 1.0}; }
        }

        map.put("favoriteGenres",  lite.getFavoriteGenres());
        map.put("favoriteArtists", lite.getFavoriteArtists());
        map.put("languages",       lite.getLanguages());
        map.put("energyMin", energy[0]);
        map.put("energyMax", energy[1]);
        map.put("valenceMin", val[0]);
        map.put("valenceMax", val[1]);
        return map;
    }
}
