package com.example.spot.service.memberstudy;

import com.example.spot.api.exception.GeneralException;
import com.example.spot.domain.Member;
import com.example.spot.domain.enums.ApplicationStatus;
import com.example.spot.domain.enums.Theme;
import com.example.spot.domain.mapping.MemberStudy;
import com.example.spot.domain.study.Schedule;
import com.example.spot.domain.study.Study;
import com.example.spot.domain.study.StudyPost;
import com.example.spot.repository.MemberRepository;
import com.example.spot.repository.MemberStudyRepository;
import com.example.spot.repository.ScheduleRepository;
import com.example.spot.repository.StudyPostRepository;
import com.example.spot.security.utils.SecurityUtils;
import com.example.spot.web.dto.study.response.StudyPostResponseDTO;
import com.example.spot.web.dto.study.response.StudyScheduleResponseDTO;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MemberStudyQueryServiceTest {

    @InjectMocks
    private MemberStudyQueryServiceImpl memberStudyQueryService;

    @Mock
    private MemberStudyRepository memberStudyRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StudyPostRepository studyPostRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private SecurityUtils securityUtils;

    private static Member member;
    private static Study study;
    private static MemberStudy memberStudy;
    @BeforeEach
    void setup(){
        member = Member.builder()
                .id(1L)
                .build();

        study = Study.builder()
                .build();

        memberStudy = MemberStudy.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(ApplicationStatus.APPROVED).build();

        Long studyId = 1L;

        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(memberStudy));
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        // SecurityContext 생성 및 설정
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void test(){
        // given


        // when


        // then
    }

    /* ------------------------------------------------ 스터디 공지사항 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 공지사항 조회 - 성공")
    void 스터디_공지사항_조회_성공(){

        // given
        long studyId = 1L;
        String title = "공지";
        String content = "공지입니다.";
        StudyPost studyPost = new StudyPost(true, Theme.WELCOME, title, content);
        MemberStudy memberStudy = MemberStudy.builder()
                        .introduction(title).study(study).member(member).isOwned(true).status(ApplicationStatus.APPROVED).build();

        when(studyPostRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.of(studyPost));
        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(memberStudy));

        // when
        StudyPostResponseDTO responseDTO = memberStudyQueryService.findStudyAnnouncementPost(studyId);

        // then
        assertEquals(title,responseDTO.getTitle());
        assertEquals(content, responseDTO.getContent());
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 로그인 한 회원이 해당 스터디 회원이 아닌 경우")
    void 스터디_공지사항_조회_실패_1(){

        // given
        long studyId = 1L;
        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyAnnouncementPost(studyId));
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 스터디 공지 글이 없는 경우")
    void 스터디_공지사항_조회_실패_2(){

        // given
        long studyId = 1L;
        MemberStudy memberStudy = MemberStudy.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(ApplicationStatus.APPROVED).build();


        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(memberStudy));
        when(studyPostRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyAnnouncementPost(studyId));
    }

    /* ------------------------------------------------ 스터디 모임 목록 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 모임 목록 조회 - 성공")
    void 스터디_모임_목록_조회_성공(){
        // given
        Long studyId = 1L;
        String title = "title";

        Schedule schedule1 = Schedule.builder().id(1L).title(title).build();
        Schedule schedule2 = Schedule.builder().id(2L).title("title1").build();
        when(scheduleRepository.findAllByStudyId(studyId, Pageable.unpaged())).thenReturn(List.of(schedule1, schedule2));

        // when
        StudyScheduleResponseDTO responseDTO = memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged());

        // then
        assertEquals(2, responseDTO.getTotalElements());
        assertEquals(title, responseDTO.getSchedules().get(0).getTitle());
    }

    @Test
    @DisplayName("스터디 모임 목록 조회 - 로그인 한 회원이 해당 스터디 회원이 아닌 경우")
    void 스터디_모임_목록_조회_실패_1(){
        // given
        long studyId = 1L;
        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged()));
    }

    @Test
    @DisplayName("스터디 모임 목록 조회 - 스터디 모임 일정이 존재하지 않는 경우")
    void 스터디_모임_목록_조회_실패_2(){
        // given
        long studyId = 1L;
        when(memberStudyRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, ApplicationStatus.APPROVED)).thenReturn(
                Optional.of(memberStudy));
        when(scheduleRepository.findAllByStudyId(studyId, Pageable.unpaged())).thenReturn(Collections.emptyList());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged()));
    }

}
