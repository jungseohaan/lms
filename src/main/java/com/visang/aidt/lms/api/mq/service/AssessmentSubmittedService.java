package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.dto.assessment.*;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.AssessmentSubmittedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentSubmittedService {

    private final AssessmentSubmittedMapper assessmentSubmittedMapper;
    private final String evlUrlId = "https://govcon.aidtclass.com/assessment_";

    public void insertAssessmentInfo(Map<String, Object> paramData) {
        int evlId = (int) paramData.get("evlId");
        assessmentSubmittedMapper.insertEvlMqTrnLog(evlId);
    }

    public void modifyAssessmentScore(Map<String, Object> paramData) {
        int evlId = (int) paramData.get("evlId");
        assessmentSubmittedMapper.modifyAssessmentScore(evlId);
    }

    public List<AssessmentSubmittedMqDto> createAssessmentResultRequest(RealMqReqDto paramData) throws Exception {
        List<AssessmentSubmittedMqDto> resultList = new ArrayList<>();

        // 평가 공개 완료한 학생 목록 조회
        List<CompletedAssessments> students = assessmentSubmittedMapper.findCompletedAssessmentsStudents(paramData);
        for (CompletedAssessments student : students) {
            Map<String, String> studentInfo = Map.of(
                    "stntId", student.mamoymId(),
                    "userId", Optional.ofNullable(paramData.getUserId()).orElse("")
            );

            // 학생별로 진행한 평가지목록 조회
            List<AssessmentInfo> resultAssessmentInfoList = new ArrayList<>();
            List<AssessmentInfo> assessmentSheets = assessmentSubmittedMapper.findAssessmentSheets(studentInfo);
            for (AssessmentInfo assessmentInfo : assessmentSheets) {
                int evlId = assessmentInfo.getEvlId();
                int evlResultId = assessmentInfo.getEvlResultId();

                List<AssessmentDetail> rawAssessmentDetailList = assessmentSubmittedMapper.findStdtEvalResultsDetail(evlResultId,evlId);
                List<AssessmentDetail> assessmentDetailList = rawAssessmentDetailList.stream()
                        .map(this::convertCurriculumStandardId)
                        .collect(Collectors.toList());

                // date format 변경(UTC)
                String currentTime = "";

                DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .withZone(ZoneOffset.UTC);

                if (StringUtils.isNotBlank(assessmentInfo.getTimestamp())) {
                    LocalDateTime localDateTime = LocalDateTime.parse(assessmentInfo.getTimestamp(), sourceFormatter);

                    currentTime = formatter.format(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                }

                AssessmentInfo resultAssessmentInfo = AssessmentInfo.builder()
                        .setsId(evlUrlId+ assessmentInfo.getSetsId())
                        .type(assessmentInfo.getType())
                        .aitutorRecommended(assessmentInfo.getAitutorRecommended())
                        .score(assessmentInfo.getScore() > 100 ? 100 : assessmentInfo.getScore())
                        .timestamp(currentTime)
                        .assessmentDetailList(assessmentDetailList)
                        .userId(student.mamoymId())
                        .build();



                resultAssessmentInfoList.add(resultAssessmentInfo);
            }

            Map<String, String> ptdInfo = new LinkedHashMap<>();
            if(student.mamoymId() != null){
                ptdInfo = assessmentSubmittedMapper.getUserInfo(student.mamoymId());
            }
            AssessmentSubmittedMqDto resultData = AssessmentSubmittedMqDto.builder()
                    .partnerId(ptdInfo.get("ptnId"))
                    .type(MessageConstants.Type.ASSESSMENT)
                    .verb(MessageConstants.Verb.SUBMITTED)
                    .userId(student.mamoymId())
                    .reqTime(getCurrentTime())
                    .curriculumStandardList(getCurriculumScores(studentInfo.get("stntId"),student.evlId()))
                    .assessmentInfoList(resultAssessmentInfoList)
                    .build();

            resultAssessmentInfoList.stream()
                    .filter(info -> "F".equals(info.getType()))
                    .findFirst()
                    .ifPresentOrElse(assessmentInfo ->
                            resultData.setCurriculumStandardList(
                                    getCurriculumScores(studentInfo.get("stntId"),student.evlId())
                            )
                            , () -> resultData.setCurriculumStandardList(List.of()));

            resultList.add(resultData);
        }
        return resultList;
    }

    private AssessmentDetail convertCurriculumStandardId(AssessmentDetail rawDetail) {
        List<String> curriculumStandardIds = (rawDetail.getCurriculumStandardIdString() == null || rawDetail.getCurriculumStandardIdString().isEmpty())
                ? Collections.singletonList("-1")
                : Arrays.asList(rawDetail.getCurriculumStandardIdString().split("#\\^\\|"));

        int duration = rawDetail.getDuration();

        duration = Math.min(duration, 68400);

        return AssessmentDetail.builder()
                .id(evlUrlId+rawDetail.getSetsId()+"/"+rawDetail.getId())
                .type(rawDetail.getType())
                .difficulty(rawDetail.getDifficulty())
                .difficultyMin(rawDetail.getDifficultyMin())
                .difficultyMax(rawDetail.getDifficultyMax())
                .curriculumStandardId(curriculumStandardIds)
                .common(rawDetail.getCommon())
                .aitutorRecommended(rawDetail.getAitutorRecommended())
                .completion(rawDetail.getCompletion())
                .success(rawDetail.getSuccess())
                .duration(duration)
                .attempt(rawDetail.getAttempt())
                .build();
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        return formatter.format(Instant.now());
    }

    private List<CurriculumStandard> getCurriculumScores(String mamoymId, int evlId) {
        List<Map<String, Object>> results = assessmentSubmittedMapper.findCurriculumFormativeAssessment(mamoymId);
        Map<String, Double> curriculumScores = calculateScores(results);
        Map<String, Integer> curriculumCounts = calculateCounts(results);
        return classifyCurriculums(curriculumScores, curriculumCounts, evlId);
    }

    private Map<String, Double> calculateScores(List<Map<String, Object>> results) {
        Map<String, Double> curriculumScores = new HashMap<>();
        for (Map<String, Object> result : results) {
            String curriculumStr = (String) result.get("curriculumStr");
            Double percent = ((Number) result.get("percent")).doubleValue();
            String[] curriculums = curriculumStr.split("#\\^\\|");

            for (String curriculum : curriculums) {
                curriculumScores.put(curriculum, curriculumScores.getOrDefault(curriculum, 0.0) + percent);
            }
        }
        return curriculumScores;
    }

    private Map<String, Integer> calculateCounts(List<Map<String, Object>> results) {
        Map<String, Integer> curriculumCounts = new HashMap<>();
        for (Map<String, Object> result : results) {
            String curriculumStr = (String) result.get("curriculumStr");
            String[] curriculums = curriculumStr.split("#\\^\\|");

            for (String curriculum : curriculums) {
                curriculumCounts.put(curriculum, curriculumCounts.getOrDefault(curriculum, 0) + 1);
            }
        }
        return curriculumCounts;
    }

    private List<CurriculumStandard> classifyCurriculums(Map<String, Double> curriculumScores,
                                                         Map<String, Integer> curriculumCounts,
                                                         int evlId) {
        String curriSchool = assessmentSubmittedMapper.findcurriSchoolByEvlId(evlId);
        if (StringUtils.equals(curriSchool, "elementary")) {
            // 초등의 경우는 성취수준을 3단계로 분류
            List<CurriculumStandard> curriculumStandardList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : curriculumScores.entrySet()) {
                String curriculum = entry.getKey();
                Double totalScore = entry.getValue();
                int count = curriculumCounts.get(curriculum);
                int averageScore = (int) Math.round(totalScore / count);

                String classification;
                if (averageScore >= 80) {
                    classification = "A";
                } else if (averageScore >= 60) {
                    classification = "B";
                } else {
                    classification = "C";
                }

                curriculumStandardList.add(new CurriculumStandard(curriculum, classification));
            }
            return curriculumStandardList;
        } else {
            // 중, 고등의 경우는 성취수준을 5단계로 분류
            List<CurriculumStandard> curriculumStandardList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : curriculumScores.entrySet()) {
                String curriculum = entry.getKey();
                Double totalScore = entry.getValue();
                int count = curriculumCounts.get(curriculum);
                int averageScore = (int) Math.round(totalScore / count);

                String classification;
                if (averageScore >= 90) {
                    classification = "A";
                } else if (averageScore >= 80) {
                    classification = "B";
                } else if (averageScore >= 70) {
                    classification = "C";
                } else if (averageScore >= 60) {
                    classification = "D";
                } else {
                    classification = "E";
                }

                curriculumStandardList.add(new CurriculumStandard(curriculum, classification));
            }
            return curriculumStandardList;
        }
    }

    public int modifyEvlMqTrnAt() {
        return assessmentSubmittedMapper.updateEvlMqTrnAt();
    }


}
