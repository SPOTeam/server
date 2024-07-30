package com.example.spot.web.dto.member;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSignInDTO{
        private String accessToken;
        private String email;
    }

    public static class MemberUpdateDTO {
        private Long memberId;
        private LocalDateTime updatedAt;
    }
}

