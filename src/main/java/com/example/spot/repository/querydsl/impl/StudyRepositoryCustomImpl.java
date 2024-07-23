package com.example.spot.repository.querydsl.impl;

import com.example.spot.domain.Theme;
import com.example.spot.domain.enums.Gender;
import com.example.spot.domain.enums.Status;
import com.example.spot.domain.enums.StudySortBy;
import com.example.spot.domain.enums.StudyState;
import com.example.spot.domain.enums.ThemeType;
import com.example.spot.domain.mapping.RegionStudy;
import com.example.spot.domain.mapping.StudyTheme;
import com.example.spot.domain.study.QStudy;
import com.example.spot.domain.study.Study;
import com.example.spot.repository.querydsl.StudyRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import static com.example.spot.domain.study.QStudy.study;
@RequiredArgsConstructor
public class StudyRepositoryCustomImpl implements StudyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Study> findByStudyTheme(List<StudyTheme> studyThemes) {
        return queryFactory.selectFrom(study)
            .where(study.themes.any().in(studyThemes))
            .orderBy(study.createdAt.desc())
            .offset(0)
            .limit(3)
            .fetch();
    }

    @Override
    public List<Study> findStudyByGenderAndAgeAndIsOnlineAndHasFeeAndFeeAndThemeTypes(
        Map<String, Object> search, StudySortBy sortBy, Pageable pageable, List<StudyTheme> themes) {
        QStudy study = QStudy.study;

        BooleanBuilder builder = getBooleanBuilderByThemeTypes(search, study, themes);
        if (sortBy != null && sortBy.equals(StudySortBy.RECRUITING)){
            builder.and(study.studyState.eq((StudyState.RECRUITING)));
        }

        // 정렬 조건 설정
        JPAQuery<Study> query = queryFactory.selectFrom(study)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());


        switch (sortBy) {
            case HIT:
                query.orderBy(study.hitNum.desc());
                query.orderBy(study.createdAt.desc());
                break;
            case LIKED:
                query.orderBy(study.heartCount.desc());
                query.orderBy(study.createdAt.desc());
                break;
            case RECRUITING:
                query.orderBy(study.createdAt.desc());
                break;
            default:
                query.orderBy(study.createdAt.desc());
                break;
        }

        return query.fetch();
    }

    @Override
    public List<Study> findStudyByGenderAndAgeAndIsOnlineAndHasFeeAndFeeAndRegionStudies(
        Map<String, Object> search, StudySortBy sortBy, Pageable pageable,
        List<RegionStudy> regionStudies) {

        QStudy study = QStudy.study;

        BooleanBuilder builder = getBooleanBuilderByRegionStudies(search, study, regionStudies);

        if (sortBy != null && sortBy.equals(StudySortBy.RECRUITING))
            builder.and(study.studyState.eq((StudyState.RECRUITING)));

        // 정렬 조건 설정
        JPAQuery<Study> query = queryFactory.selectFrom(study)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        switch (sortBy) {
            case HIT:
                query.orderBy(study.hitNum.desc());
                query.orderBy(study.createdAt.desc());
                break;
            case LIKED:
                query.orderBy(study.heartCount.desc());
                query.orderBy(study.createdAt.desc());
                break;
            case RECRUITING:
                query.orderBy(study.createdAt.desc());
                break;
            default:
                query.orderBy(study.createdAt.desc());
                break;
        }

        return query.fetch();
    }

    @Override
    public long countStudyByGenderAndAgeAndIsOnlineAndHasFeeAndFeeAndThemeTypes(
        Map<String, Object> search, List<StudyTheme> themeTypes) {
        BooleanBuilder builder = getBooleanBuilderByThemeTypes(search, study, themeTypes);
        return queryFactory.selectFrom(study)
            .where(builder)
            .fetchCount();
    }

    @Override
    public long countStudyByGenderAndAgeAndIsOnlineAndHasFeeAndFeeAndRegionStudies(
        Map<String, Object> search, List<RegionStudy> regionStudies) {
        BooleanBuilder builder = getBooleanBuilderByRegionStudies(search, study, regionStudies);
        return queryFactory.selectFrom(study)
            .where(builder)
            .fetchCount();
    }

    private static BooleanBuilder getBooleanBuilderByRegionStudies(Map<String, Object> search, QStudy study,
        List<RegionStudy> RegionStudies) {
        BooleanBuilder builder = new BooleanBuilder();
        // 조건문 추가
        getConditions(search, study, builder);
        if (RegionStudies != null && !RegionStudies.isEmpty()) {
            builder.and(study.regions.any().in(RegionStudies));
        }
        return builder;
    }

    private static BooleanBuilder getBooleanBuilderByThemeTypes(Map<String, Object> search, QStudy study,
        List<StudyTheme> themeTypes) {
        BooleanBuilder builder = new BooleanBuilder();

        // 조건문 추가
        getConditions(search, study, builder);
        if (themeTypes != null && !themeTypes.isEmpty()) {
            builder.and(study.themes.any().in(themeTypes));
        }
        return builder;
    }
    private static void getConditions(Map<String, Object> search, QStudy study,
        BooleanBuilder builder) {
        if (search.get("isOnline") != null) {
            builder.and(study.isOnline.eq((Boolean) search.get("isOnline")));
        }
        if (search.get("gender") != null) {
            builder.and(study.gender.eq((Gender) search.get("gender")));
        }
        if (search.get("minAge") != null) {
            builder.and(study.minAge.goe((Integer) search.get("minAge")));
        }
        if (search.get("maxAge") != null) {
            builder.and(study.maxAge.loe((Integer) search.get("maxAge")));
        }
        if (search.get("hasFee") != null) {
            builder.and(study.hasFee.eq((Boolean) search.get("hasFee")));
        }
        if (search.get("fee") != null) {
            builder.and(study.fee.loe((Integer) search.get("fee")));
        }
    }


}