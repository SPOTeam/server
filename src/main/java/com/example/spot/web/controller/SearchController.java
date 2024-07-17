package com.example.spot.web.controller;

import com.example.spot.web.dto.search.SearchRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Search", description = "Search API")
@RestController
@RequestMapping("/spot")
public class SearchController {

    @GetMapping("/search/studies/interest-themes/main/users/{userId}")
    @Operation(summary = "[메인 화면 - 개발중] 회원 별 관심 분야 스터디 3개 조회",
        description = """
            ## [메인 화면] 접속한 회원의 관심 스터디 3개를 조회 합니다.
            조회된 스터디 3개의 정보가 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken"))
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void interestStudiesForMain(@PathVariable long userId) {}

    @GetMapping("/search/studies/recommend/main/users/{userId}")
    @Operation(summary = "[메인 화면 - 개발중] 회원 별 추천 스터디 3개 조회",
        description = """
            ## [메인 화면] 접속한 회원의 추천 스터디 3개를 조회 합니다.
            조회된 스터디 3개의 정보가 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken"))
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void recommendStudiesForMain(@PathVariable long userId) {}


    /* ----------------------------- 내 관심 분야 별 스터디 조회  ------------------------------------- */


    @GetMapping("/search/studies/interest-themes/all/users/{userId}")
    @Operation(
        summary = "[내 관심사 스터디 조회 - 개발중] 내 '전체' 관심사 스터디 조회",
        description = """
            ## [내 관심사 스터디 조회] 입력한 조건에 맞는 회원의 전체 관심 분야의 스터디를 조회 합니다.
            
            조건에 맞게 검색된 스터디 목록이 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken")
    )
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void interestStudiesByConditionsAll(
        @PathVariable long userId,
        @ModelAttribute SearchRequestDTO.SearchStudyDTO searchStudyDTO
    ) {
        // 메소드 구현
    }

    @GetMapping("/search/studies/interest-themes/specific/users/{userId}/")
    @Operation(
        summary = "[내 관심사 스터디 조회 - 개발중] 내 '특정' 관심사 스터디 조회",
        description = """
            ## [내 관심사 스터디 조회] 입력한 조건에 맞는 회원의 특정 관심 분야의 스터디를 조회 합니다.
            
            조건에 맞게 검색된 스터디 목록이 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken")
    )
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void interestStudiesByConditionsSpecific(
        @PathVariable long userId,
        @ModelAttribute SearchRequestDTO.SearchStudyDTO searchStudyDTO
    ) {
        // 메소드 구현
    }


    /* ----------------------------- 내 관심 지역 별 스터디 조회  ------------------------------------- */


    @GetMapping("/search/studies/preferred-region/all/users/{userId}")
    @Operation(
        summary = "[내 관심 지역 스터디 조회 - 개발중] 내 '전체' 관심 지역 스터디 조회",
        description = """
            ## [내 관심 지역 스터디 조회] 입력한 조건에 맞는 회원의 전체 관심 지역의 스터디를 조회 합니다.
            
            조건에 맞게 검색된 스터디 목록이 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken")
    )
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void interestRegionStudiesByConditionsAll(
        @PathVariable long userId,
        @ModelAttribute SearchRequestDTO.SearchStudyDTO searchStudyDTO

    ) {
        // 메소드 구현
    }

    @GetMapping("/search/studies/preferred-region/specific/users/{userId}")
    @Operation(
        summary = "[내 관심 지역 스터디 조회 - 개발중] 내 '특정' 관심 지역 스터디 조회",
        description = """
            ## [내 관심 지역 스터디 조회] 입력한 조건에 맞는 회원의 특정 관심 지역의 스터디를 조회 합니다.
            
            조건에 맞게 검색된 스터디 목록이 반환 됩니다.""",
        security = @SecurityRequirement(name = "accessToken")
    )
    @Parameter(name = "userId", description = "조회할 유저의 ID를 입력 받습니다.", required = true)
    public void interestRegionStudiesByConditionsSpecific(
        @PathVariable long userId,
        @ModelAttribute SearchRequestDTO.SearchStudyDTO searchStudyDTO
    ) {
        // 메소드 구현
    }

    /* ----------------------------- 모집 중 스터디 조회  ------------------------------------- */


    @GetMapping("/search/studies/recruiting")
    @Operation(
        summary = "[모집 중 스터디 조회 - 개발중] 모집 중인 스터디 조회",
        description = """
            ## [모집 중 스터디 조회] 입력한 조건에 맞는 모집 중인  스터디 전체를 조회 합니다.
            
            조건에 맞게 검색된 스터디 목록이 반환 됩니다."""
    )
    public void recruitingStudiesByConditions(@ModelAttribute SearchRequestDTO.SearchStudyDTO searchStudyDTO) {
        // 메소드 구현
    }

}
