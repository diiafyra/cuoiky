package com.example.demo.controller;

import com.example.demo.domain.RecommendLog;
import com.example.demo.service.RecommendLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HistoryController {

  private final RecommendLogService logs;

  @GetMapping("/me/history")
  public String index(Model model,
                      @RequestAttribute(value = "uid", required = false) String uid) {
    if (uid == null) uid = "anonymous";

    // Lấy tất cả log theo uid (mới nhất trước)
    List<RecommendLog> items = logs.findAllByUidOrderByCreatedAtDesc(uid);

    // Có thể muốn giới hạn thì dùng service khác: findRecent(uid, 100)...
    model.addAttribute("items", items);
    model.addAttribute("uid", uid);
    // Server timezone để format nếu cần
    model.addAttribute("now", ZonedDateTime.now(ZoneId.systemDefault()));
    return "history";
  }
}
