package com.example.spot.service.studyschedule;

import com.example.spot.api.exception.handler.StudyHandler;
import com.example.spot.domain.Member;
import com.example.spot.domain.enums.ApplicationStatus;
import com.example.spot.domain.enums.Gender;
import com.example.spot.domain.enums.Period;
import com.example.spot.domain.mapping.MemberStudy;
import com.example.spot.domain.study.Schedule;
import com.example.spot.domain.study.Study;
import com.example.spot.repository.MemberRepository;
import com.example.spot.repository.MemberStudyRepository;
import com.example.spot.repository.ScheduleRepository;
import com.example.spot.repository.StudyRepository;
import com.example.spot.service.memberstudy.MemberStudyQueryServiceImpl;
import com.example.spot.web.dto.memberstudy.response.ScheduleResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudyScheduleQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private MemberStudyRepository memberStudyRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private MemberStudyQueryServiceImpl memberStudyQueryService;

    private static Study study;
    private static Member member1;
    private static Member member2;
    private static Member owner;
    private static MemberStudy member1Study;
    private static MemberStudy ownerStudy;
    private static Schedule schedule1;
    private static Schedule schedule2;

    @BeforeEach
    void setUp() {
        initMember();
        initStudy();
        initMemberStudy();
        initSchedule();

        when(memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

        when(memberStudyRepository.findById(member1Study.getId())).thenReturn(Optional.of(member1Study));
        when(memberStudyRepository.findById(ownerStudy.getId())).thenReturn(Optional.of(ownerStudy));
        when(memberStudyRepository.existsByMemberIdAndStudyIdAndStatus(member1.getId(), study.getId(), ApplicationStatus.APPROVED)).thenReturn(true);
        when(memberStudyRepository.existsByMemberIdAndStudyIdAndStatus(member2.getId(), study.getId(), ApplicationStatus.APPROVED)).thenReturn(false);
        when(memberStudyRepository.existsByMemberIdAndStudyIdAndStatus(owner.getId(), study.getId(), ApplicationStatus.APPROVED)).thenReturn(true);

    }

    @Test
    @DisplayName("스터디 월별 일정 조회 - (성공)")
    void getMonthlySchedules_Success() {

        // given
        Long memberId = 1L; // 로그인 회원
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when
        ScheduleResponseDTO.MonthlyScheduleListDTO result = memberStudyQueryService.getMonthlySchedules(studyId, 2024, 1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getScheduleList()).isNotEmpty();
        assertThat(result.getScheduleList()).size().isEqualTo(2);
        assertThat(result.getScheduleList().get(0).getTitle()).isEqualTo(schedule1.getTitle());
        assertThat(result.getScheduleList().get(1).getTitle()).isEqualTo(schedule2.getTitle());
    }

    @Test
    @DisplayName("스터디 월별 일정 조회 - 스터디 회원이 아닌 경우 (성공)")
    void getMonthlySchedules_NotStudyMember_Fail() {

        // given
        Long memberId = 2L; // 로그인 회원 (스터디 회원 X)
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when
        ScheduleResponseDTO.MonthlyScheduleListDTO result = memberStudyQueryService.getMonthlySchedules(studyId, 2024, 1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getScheduleList()).isNotEmpty();
        assertThat(result.getScheduleList()).size().isEqualTo(2);
        assertThat(result.getScheduleList().get(0).getLocation()).isEqualTo("공개되지 않습니다.");
        assertThat(result.getScheduleList().get(1).getLocation()).isEqualTo("공개되지 않습니다.");
    }

    @Test
    void getSchedule() {
    }

/*-------------------------------------------------------- Utils ------------------------------------------------------------------------*/

    private static void initMember() {
        member1 = Member.builder()
                .id(1L)
                .scheduleList(new ArrayList<>())
                .build();
        member2 = Member.builder()
                .id(2L)
                .scheduleList(new ArrayList<>())
                .build();
        owner = Member.builder()
                .id(3L)
                .scheduleList(new ArrayList<>())
                .build();
    }

    private static void initStudy() {
        study = Study.builder()
                .gender(Gender.MALE)
                .minAge(20)
                .maxAge(29)
                .fee(10000)
                .profileImage("a.jpg")
                .hasFee(true)
                .isOnline(true)
                .goal("SQLD")
                .introduction("SQLD 자격증 스터디")
                .title("SQLD Master")
                .maxPeople(10L)
                .build();
    }

    private static void initMemberStudy() {
        ownerStudy = MemberStudy.builder()
                .id(1L)
                .status(ApplicationStatus.APPROVED)
                .isOwned(true)
                .introduction("Hi")
                .member(owner)
                .study(study)
                .build();
        member1Study = MemberStudy.builder()
                .id(2L)
                .status(ApplicationStatus.APPROVED)
                .isOwned(false)
                .introduction("Hi")
                .member(member1)
                .study(study)
                .build();
    }

    private static void initSchedule() {

        LocalDateTime startedAt = LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.MIN);

        schedule1 = Schedule.builder()
                .id(1L)
                .study(study)
                .member(owner)
                .title("일정1")
                .startedAt(startedAt)
                .finishedAt(startedAt.plusHours(1))
                .period(Period.NONE)
                .build();
        study.addSchedule(schedule1);
        owner.addSchedule(schedule1);

        schedule2 = Schedule.builder()
                .id(2L)
                .study(study)
                .member(member1)
                .title("일정2")
                .startedAt(startedAt)
                .finishedAt(startedAt.plusHours(2))
                .period(Period.MONTHLY)
                .build();
        study.addSchedule(schedule2);
        member1.addSchedule(schedule2);
    }

    private static void getAuthentication(Long memberId) {
        String idString = String.valueOf(memberId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(idString, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}