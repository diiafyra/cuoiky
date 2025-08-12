package com.example.demo.repository;

import com.example.demo.domain.RecommendLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendLogRepository extends JpaRepository<RecommendLog, Long> {
  List<RecommendLog> findByUidOrderByCreatedAtDesc(String uid);
}
