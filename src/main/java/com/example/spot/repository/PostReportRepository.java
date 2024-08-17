package com.example.spot.repository;

import com.example.spot.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    Optional<PostReport> findByMemberIdAndPostId(Long memberId, Long postId);

    boolean existsByPostId(Long postId);
}
