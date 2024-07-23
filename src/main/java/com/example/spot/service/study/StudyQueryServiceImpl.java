package com.example.spot.service.study;

import com.example.spot.api.code.status.ErrorStatus;
import com.example.spot.api.exception.handler.MemberHandler;
import com.example.spot.config.EntitySpecification;
import com.example.spot.domain.Member;
import com.example.spot.domain.Theme;
import com.example.spot.domain.enums.ThemeType;
import com.example.spot.domain.mapping.MemberTheme;
import com.example.spot.domain.study.Study;
import com.example.spot.repository.MemberRepository;
import com.example.spot.repository.MemberStudyRepository;
import com.example.spot.repository.MemberThemeRepository;
import com.example.spot.repository.StudyRepository;
import com.example.spot.web.dto.search.SearchRequestDTO.SearchStudyDTO;
import com.example.spot.web.dto.search.SearchResponseDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyQueryServiceImpl implements StudyQueryService {

    private final StudyRepository studyRepository;
    private final MemberStudyRepository memberStudyRepository;
    private final MemberThemeRepository memberThemeRepository;
    private final MemberRepository memberRepository;

    @Override
    public Page<SearchStudyDTO> findRecommendStudies(Pageable pageable, Long memberId) {
        return null;
    }

    @Override
    public Page<SearchResponseDTO.SearchStudyDTO> findInterestStudiesByConditionsAll(Pageable pageable, Long memberId,
        SearchStudyDTO request) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(
            ErrorStatus._MEMBER_NOT_FOUND));
        List<Theme> themes = memberThemeRepository.findAllByMemberId(memberId).stream()
            .map(MemberTheme::getTheme)
            .toList();

        Map<String, Object> search = new HashMap<>();
        if (request.getGender() != null)
            search.put("gender", request.getGender());
        if (request.getMinAge() != null)
            search.put("minAge", request.getMinAge());
        if (request.getMaxAge() != null)
            search.put("maxAge", request.getMaxAge());
        if (request.getIsOnline() != null)
            search.put("isOnline", request.getIsOnline());
        if (request.getHasFee() != null)
            search.put("hasFee", request.getHasFee());
        if (request.getFee() != null)
            search.put("fee", request.getFee());

        Sort sort = Sort.by(Direction.DESC,"createdAt");

        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case ALL:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case RECRUITING:
                    search.put("status", "RECRUITING"); // 모집중인 상태만 필터링
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case HIT:
                    sort = Sort.by(Sort.Direction.DESC, "hitCount");
                    break;
                case LIKED:
                    sort = Sort.by(Sort.Direction.DESC, "likeCount");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
            }
        }
        // 테마 조건 추가
        if (!themes.isEmpty())
            search.put("themes", themes);

        Specification<Study> spec = EntitySpecification.searchStudy(search)
            .and((root, query, criteriaBuilder) -> {
                if (!themes.isEmpty()) {
                    return root.join("themes").in(themes);
                } else {
                    return criteriaBuilder.conjunction();
                }
            });

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Study> studyPage = studyRepository.findAll(spec, pageable);

        List<SearchResponseDTO.SearchStudyDTO> dtoList = studyPage.getContent().stream()
            .map(SearchResponseDTO.SearchStudyDTO::new) // Study 엔티티를 SearchStudyDTO로 변환하는 매퍼 메서드가 필요
            .toList();

        return new PageImpl<>(dtoList, pageable, studyPage.getTotalElements());
    }

    @Override
    public Page<SearchStudyDTO> findInterestStudiesByConditionsSpecific(Pageable pageable,
        Long memberId, SearchStudyDTO request, ThemeType theme) {
        return null;
    }

    @Override
    public Page<SearchStudyDTO> findInterestRegionStudiesByConditionsAll(Pageable pageable,
        Long memberId, SearchStudyDTO request) {
        return null;
    }

    @Override
    public Page<SearchStudyDTO> findInterestRegionStudiesByConditionsSpecific(Pageable pageable,
        Long memberId, SearchStudyDTO request, String regionCode) {
        return null;
    }

    @Override
    public Page<SearchStudyDTO> findRecruitingStudiesByConditions(Pageable pageable,
        SearchStudyDTO request) {
        return null;
    }

    @Override
    public Page<SearchStudyDTO> findLikedStudiesByConditions(Pageable pageable, Long memberId) {
        return null;
    }
}
