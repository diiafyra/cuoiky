package com.example.demo.controller;

import com.example.demo.domain.RecommendLog;
import com.example.demo.repository.RecommendLogRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RecommendationPageController {
  private final RecommendLogRepository repo;

@GetMapping("/recommend/{id}")
public String show(@PathVariable Long id, Model model) {
  var log = repo.findById(id).orElse(null);
  model.addAttribute("log", log);

  List<String> keywords = java.util.List.of();
  if (log != null && log.getKeywordsJson() != null) {
    try {
      keywords = java.util.Arrays.asList(
          new com.fasterxml.jackson.databind.ObjectMapper()
              .readValue(log.getKeywordsJson(), String[].class));
    } catch (Exception ignored) {}
  }
  model.addAttribute("keywords", keywords);
  return "recommend/show";
}

}
