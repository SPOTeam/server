package com.example.spot.service.post;

public interface LikedPostCommentQueryService {
    long countByPostCommentIdAndIsLikedTrue(Long postCommentId);
}