package com.example.spot.service.memberstudy;

import com.example.spot.web.dto.memberstudy.request.StudyQuizRequestDTO;
import com.example.spot.web.dto.memberstudy.response.StudyQuizResponseDTO;
import com.example.spot.web.dto.memberstudy.response.StudyTerminationResponseDTO;
import com.example.spot.web.dto.memberstudy.response.StudyWithdrawalResponseDTO;
import com.example.spot.web.dto.study.request.ScheduleRequestDTO;
import com.example.spot.web.dto.study.response.ScheduleResponseDTO;
import com.example.spot.web.dto.study.response.StudyApplyResponseDTO;

public interface MemberStudyCommandService {

    StudyWithdrawalResponseDTO.WithdrawalDTO withdrawFromStudy(Long memberId, Long studyId);

    StudyTerminationResponseDTO.TerminationDTO terminateStudy(Long studyId);

    // 스터디 신청 수락
    StudyApplyResponseDTO acceptAndRejectStudyApply(Long memberId, Long studyId, boolean isAccept);

    // 일정 생성
    ScheduleResponseDTO.ScheduleDTO addSchedule(Long studyId, ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO);

    // 일정 수정
    ScheduleResponseDTO.ScheduleDTO modSchedule(Long studyId, Long scheduleId, ScheduleRequestDTO.ScheduleDTO scheduleModDTO);

    // 스터디 퀴즈 생성
    StudyQuizResponseDTO.QuizDTO createAttendanceQuiz(Long studyId, StudyQuizRequestDTO.QuizDTO quizRequestDTO);

    // 스터디 출석
    StudyQuizResponseDTO.AttendanceDTO attendantStudy(Long studyId, Long quizId, StudyQuizRequestDTO.AttendanceDTO attendanceRequestDTO);

    // 스터디 퀴즈 삭제
    StudyQuizResponseDTO.QuizDTO deleteAttendanceQuiz(Long studyId, Long quizId);
}
