package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.MqUrlType;
import com.visang.aidt.lms.api.mq.dto.assignment.*;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.AssignmentFinishedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentFinishedService {
    private final AssignmentFinishedMapper assignmentFinishedMapper;

    public void insertBulkTaskMqTrnLogStntSubmit(Map<String, Object> submittedData) throws Exception {
        int taskId = (int) submittedData.get("taskId");
        String stntId = (String) submittedData.get("userId");

        AssignmentFinishedSaveDto saveDto = AssignmentFinishedSaveDto.builder()
                .taskId(taskId)
                .stntId(stntId)
                .taskGbCd(TaskGbCd.SUBMISSION.getCode())
                .build();

        assignmentFinishedMapper.insertAssignmentSubmissionInfo(saveDto);
    }

    @Transactional(readOnly = true)
    public List<AssignmentFinishedMqDto> createAssignmentFinishedMq(RealMqReqDto paramData) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        List<AssignmentFinishedMqDto> resultList = new ArrayList<>();

        List<AssignmentFinishedInfoDto> infoDataList = assignmentFinishedMapper.findAssignmentSubmissionInfo(paramData);

        // null 값을 처리하는 그룹화 로직
        Map<String, List<AssignmentFinishedInfoDto>> groupedInfoData = infoDataList.stream()
                .filter(info -> Objects.nonNull(info.getStntId()))
                .collect(Collectors.groupingBy(AssignmentFinishedInfoDto::getStntId));


        for (Map.Entry<String, List<AssignmentFinishedInfoDto>> entry : groupedInfoData.entrySet()) {
            String stntId = entry.getKey();
            List<AssignmentFinishedInfoDto> assignments = entry.getValue();
            List<AssignmentInfo> assignmentInfoList = new ArrayList<>();

            for (AssignmentFinishedInfoDto infoData : assignments) {
                List<String> standardIds = new ArrayList<>();

                // StandardIds가 null이거나 비어있는 경우 "-1" 추가
                for (String standardId : infoData.getStandardIds().split(",")) {
                    // 세트지 내 빈 표준체계가 있는 경우 -1로 세팅, 여러 표준체계가 존재한다면 -1 제거
                    if (infoData.getStandardIds().split(",").length != 1 && StringUtils.equals(standardId, "-1")) continue;

                    // #^| 기준으로 문자열 분리
                    String[] parts = standardId.split("#\\^\\|");
                    standardIds.addAll(Arrays.asList(parts));
                }

                // date format 변경(UTC)
                String submDt = "";
                String taskRegDt = "";

                DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                if (StringUtils.isNotBlank(infoData.getSubmDt())) {
                    LocalDateTime localDateTime = LocalDateTime.parse(infoData.getSubmDt(), sourceFormatter);

                    submDt = formatter.format(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                }

                if (StringUtils.isNotBlank(infoData.getTaskRegDt())) {
                    LocalDateTime localDateTime = LocalDateTime.parse(infoData.getTaskRegDt(), sourceFormatter);

                    taskRegDt = formatter.format(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                }

                if(infoData.getSubmDt() != null){
                    AssignmentInfo assignmentInfo = AssignmentInfo.builder()
                            .id(MqUrlType.ASSIGNMENT.getUrl() + infoData.getTaskId())
                            .timestamp(taskRegDt)
                            .submTimestamp(submDt)
                            .curriculumStandardId(standardIds)
                            .build();

                    assignmentInfoList.add(assignmentInfo);
                }
            }

            Map<String, String> ptdInfo = new LinkedHashMap<>();
            if(stntId != null){
                ptdInfo = assignmentFinishedMapper.getUserInfo(stntId);
            }


            AssignmentFinishedMqDto resultMqData = AssignmentFinishedMqDto.builder()
                    .partnerId(ptdInfo.get("ptnId"))
                    .userId(stntId)
                    .type(MessageConstants.Type.ASSIGNMENT)
                    .verb(MessageConstants.Verb.FINISHED)
                    .reqTime(currentTime)
                    .assignmentInfoList(assignmentInfoList)
                    .useTermsAgreeYn(ObjectUtils.defaultIfNull(ptdInfo.get("useTermsAgreeYn"), "N"))
                    .build();

            resultList.add(resultMqData);
        }

        return resultList;
    }

    public int updateAssignmentFinishedSendAt() throws Exception {
        return assignmentFinishedMapper.updateAssignmentFinishedSendAt();
    }


}
