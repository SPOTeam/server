package com.example.spot.service.memberstudy;

import com.example.spot.api.code.status.ErrorStatus;
import com.example.spot.api.exception.handler.StudyHandler;
import com.example.spot.domain.Quiz;
import com.example.spot.domain.enums.Period;
import com.example.spot.domain.mapping.MemberAttendance;
import com.example.spot.domain.study.Schedule;
import com.example.spot.domain.study.Study;
import com.example.spot.repository.*;
import com.example.spot.web.dto.memberstudy.response.StudyQuizResponseDTO;
import com.example.spot.web.dto.study.response.ScheduleResponseDTO;
import lombok.RequiredArgsConstructor;
import com.example.spot.api.exception.GeneralException;
import com.example.spot.domain.enums.ApplicationStatus;
import com.example.spot.domain.mapping.MemberStudy;
import com.example.spot.domain.study.StudyPost;
import com.example.spot.web.dto.study.response.StudyMemberResponseDTO;
import com.example.spot.web.dto.study.response.StudyMemberResponseDTO.StudyApplyMemberDTO;
import com.example.spot.web.dto.study.response.StudyMemberResponseDTO.StudyMemberDTO;
import com.example.spot.web.dto.study.response.StudyPostResponseDTO;
import com.example.spot.web.dto.study.response.StudyScheduleResponseDTO;
import com.example.spot.web.dto.study.response.StudyScheduleResponseDTO.StudyScheduleDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberStudyQueryServiceImpl implements MemberStudyQueryService {

    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberStudyRepository memberStudyRepository;
    private final MemberAttendanceRepository memberAttendanceRepository;
    private final QuizRepository quizRepository;


    @Override
    public StudyPostResponseDTO findStudyAnnouncementPost(Long studyId) {
        StudyPost studyPost = studyPostRepository.findByStudyIdAndIsAnnouncement(
            studyId, true).orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_POST_NOT_FOUND));

        return StudyPostResponseDTO.builder()
            .title(studyPost.getTitle())
            .content(studyPost.getContent()).build();
    }

    @Override
    public StudyScheduleResponseDTO findStudySchedule(Long studyId, Pageable pageable) {
        List<Schedule> schedules = scheduleRepository.findAllByStudyId(studyId, pageable);
        if (schedules.isEmpty())
            throw  new GeneralException(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND);

        List<StudyScheduleDTO> scheduleDTOS = schedules.stream().map(schedule -> StudyScheduleDTO.builder()
            .title(schedule.getTitle())
            .location(schedule.getLocation())
            .staredAt(schedule.getStartedAt())
            .build()).toList();

        return new StudyScheduleResponseDTO(new PageImpl<>(scheduleDTOS, pageable, schedules.size()), scheduleDTOS, schedules.size());
    }

    @Override
    public StudyMemberResponseDTO findStudyMembers(Long studyId) {
        List<MemberStudy> memberStudies = memberStudyRepository.findAllByStudyIdAndStatus(studyId, ApplicationStatus.APPROVED);
        if (memberStudies.isEmpty())
            throw new GeneralException(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        List<StudyMemberDTO> memberDTOS = memberStudies.stream().map(memberStudy -> StudyMemberDTO.builder()
            .memberId(memberStudy.getMember().getId())
            .nickname(memberStudy.getMember().getName())
            .profileImage(memberStudy.getMember().getProfileImage())
            .build()).toList();
        return new StudyMemberResponseDTO(memberDTOS);
    }

    @Override
    public StudyMemberResponseDTO findStudyApplicants(Long studyId) {
        List<MemberStudy> memberStudies = memberStudyRepository.findAllByStudyIdAndStatus(studyId, ApplicationStatus.APPLIED);
        if (memberStudies.isEmpty())
            throw new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND);
        List<StudyMemberDTO> memberDTOS = memberStudies.stream().map(memberStudy -> StudyMemberDTO.builder()
            .memberId(memberStudy.getMember().getId())
            .nickname(memberStudy.getMember().getName())
            .profileImage(memberStudy.getMember().getProfileImage())
            .build()).toList();
        return new StudyMemberResponseDTO(memberDTOS);
    }

    @Override
    public StudyApplyMemberDTO findStudyApplication(Long studyId, Long memberId) {
        MemberStudy memberStudy = memberStudyRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, ApplicationStatus.APPLIED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        if (memberStudy.getIsOwned())
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);

        return StudyApplyMemberDTO.builder()
            .memberId(memberStudy.getMember().getId())
            .studyId(memberStudy.getStudy().getId())
            .introduction(memberStudy.getIntroduction())
            .nickname(memberStudy.getMember().getName())
            .profileImage(memberStudy.getMember().getProfileImage())
            .build();
    }

    @Override
    public StudyQuizResponseDTO.AttendanceListDTO getAllAttendances(Long studyId, Long quizId) {

        //=== Exception ===//
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND));
        quizRepository.findByIdAndStudyId(quizId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND));

        //=== Feature ===//
        List<StudyQuizResponseDTO.StudyMemberDTO> studyMembers = memberStudyRepository.findByStudyId(studyId).stream()
                .map(memberStudy -> {
                    List<MemberAttendance> attendanceList = memberAttendanceRepository.findByQuizIdAndMemberId(quizId, memberStudy.getMember().getId());
                    for (MemberAttendance attendance : attendanceList) {
                        // MemberAttendance에 퀴즈에 대한 정답이 저장되어 있으면 금일 출석 성공
                        if (attendance.getIsCorrect())
                            return StudyQuizResponseDTO.StudyMemberDTO.toDTO(memberStudy, Boolean.TRUE);
                    }
                    // 퀴즈를 풀지 않았거나 MemberAttendance에 오답만 저장되어 있으면 금일 출석 실패
                    return StudyQuizResponseDTO.StudyMemberDTO.toDTO(memberStudy, Boolean.FALSE);
                })
                .toList();

        return StudyQuizResponseDTO.AttendanceListDTO.toDTO(quiz, studyMembers);

    }

    @Override
    public ScheduleResponseDTO.MonthlyScheduleListDTO getMonthlySchedules(Long studyId, int year, int month) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS = new ArrayList<>();

        study.getSchedules().forEach(schedule -> {
                    if (schedule.getPeriod().equals(Period.NONE)) {
                        addSchedule(schedule, year, month, monthlyScheduleDTOS);
                    } else {
                        addPeriodSchedules(schedule, year, month, monthlyScheduleDTOS);
                    }
                });

        return ScheduleResponseDTO.MonthlyScheduleListDTO.toDTO(study, monthlyScheduleDTOS);
    }

    @Override
    public ScheduleResponseDTO.MonthlyScheduleDTO getSchedule(Long studyId, Long scheduleId) {

        // Exception
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        List<Schedule> scheduleList = scheduleRepository.findByStudyId(studyId).stream()
                .filter(studySchedule -> studySchedule.getStudy().equals(study))
                .toList();
        if (scheduleList.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND);
        }

        return ScheduleResponseDTO.MonthlyScheduleDTO.toDTO(schedule);
    }

    private void addSchedule(Schedule schedule, int year, int month, List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS) {
        if (schedule.getStartedAt().getYear() == year && schedule.getStartedAt().getMonthValue() == month) {
            monthlyScheduleDTOS.add(ScheduleResponseDTO.MonthlyScheduleDTO.toDTO(schedule));
        }
    }

    private void addPeriodSchedules(Schedule schedule, int year, int month, List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS) {

        Duration duration = Duration.between(schedule.getStartedAt(), schedule.getFinishedAt()); // 일정 수행 시간
        DayOfWeek targetDayOfWeek = schedule.getStartedAt().getDayOfWeek(); // 일정을 반복할 요일
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1); // 탐색 연월의 첫째 날
        LocalDate newStartedAtDate = firstDayOfMonth.with(TemporalAdjusters.nextOrSame(targetDayOfWeek)); // 탐색할 첫 날짜

        // 일정 시작일이 탐색 연월 내에 있는 경우에만 반복
        if (schedule.getStartedAt().isBefore(firstDayOfMonth.plusMonths(1).atStartOfDay())) {
            while (newStartedAtDate.getMonthValue() == month) {
                LocalDateTime newStartedAt = newStartedAtDate.atStartOfDay().with(schedule.getStartedAt().toLocalTime());
                LocalDateTime newFinishedAt = newStartedAt.plus(duration);

                monthlyScheduleDTOS.add(ScheduleResponseDTO.MonthlyScheduleDTO.toDTOWithDate(schedule, newStartedAt, newFinishedAt));

                if (schedule.getPeriod().equals(Period.DAILY)) {
                    newStartedAtDate = newStartedAtDate.plusDays(1);
                } else if (schedule.getPeriod().equals(Period.WEEKLY)) {
                    newStartedAtDate = newStartedAtDate.plusWeeks(1);
                } else if (schedule.getPeriod().equals(Period.BIWEEKLY)) {
                    newStartedAtDate = newStartedAtDate.plusWeeks(2);
                } else if (schedule.getPeriod().equals(Period.MONTHLY)) {
                    newStartedAtDate = newStartedAtDate.plusMonths(1);
                }
            }
        }

    }

}
