package com.example.spot.web.service;

import com.example.spot.web.dto.PostCreateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    public void createPost(PostCreateRequest postCreateRequest) {
        // validate & save
    }

    public void deletePost(Long postId) {
        // validate & delete
    }

    public void getSinglePost(Long postId) {
        // validate & read
    }

    public void postTypePaging(String type, Pageable pageable) {
        // validate & read
    }
}