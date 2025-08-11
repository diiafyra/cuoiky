package com.example.demo.repository;

import com.example.demo.domain.MusicProfileLite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicProfileLiteRepository extends JpaRepository<MusicProfileLite, String> {
}
