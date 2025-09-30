package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.MqUrlType;
import com.visang.aidt.lms.api.mq.dto.assignment.*;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.AssignmentGaveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentGaveService {

    private final AssignmentGaveMapper assignmentGaveMapper;

    /** 교사) 과제 자료 설정 정보 저장 (Map) */
    public void insertBulkTaskMqTrnLog(Map<String, Object> registeredTaskData) {
        int test = MapUtils.getIntValue(registeredTaskData, "taskId", 0);

        if (test != 0) {
            AssignmentGaveSaveDto saveDto = AssignmentGaveSaveDto.builder()
                    .taskId(test)
                    .taskGbCd(TaskGbCd.REGISTRATION.getCode())
                    .build();

            assignmentGaveMapper.insertBulkTaskMqTrnLog(saveDto);
        }
    }

    /** 교사) 과제 자료 설정 정보 저장 (List) */
    public void insertBulkTaskMqTrnLog(List<String> registeredTaskData) {
        registeredTaskData.stream()
                .distinct()
                .forEach(taskId ->
                        assignmentGaveMapper.insertBulkTaskMqTrnLog(
                                AssignmentGaveSaveDto.builder()
                                        .taskId(Integer.valueOf(taskId))
                                        .taskGbCd(TaskGbCd.REGISTRATION.getCode())
                                        .build()
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<AssignmentGaveMqDto> createAssignmentGaveMq(RealMqReqDto paramData) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        List<AssignmentGaveMqDto> resultList = new ArrayList<>();
        List<AssignmentRegistInfoDto> infoDataList = assignmentGaveMapper.findAssignmentRegistrationInfo(paramData);

        // null 값을 처리하는 그룹화 로직
        Map<String, List<AssignmentRegistInfoDto>> groupedInfoData = infoDataList.stream()
                .filter(info -> Objects.nonNull(info.getWrterId()))
                .collect(Collectors.groupingBy(AssignmentRegistInfoDto::getWrterId));
        for (Map.Entry<String, List<AssignmentRegistInfoDto>> entry : groupedInfoData.entrySet()) {
            String wrterId = entry.getKey();
            List<AssignmentRegistInfoDto> assignments = entry.getValue();
            List<AssignmentInfo> assignmentInfoList = new ArrayList<>();
            for (AssignmentRegistInfoDto infoData : assignments) {
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
                String taskRegDt ="";

                DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                if (StringUtils.isNotBlank(infoData.getTaskRegDt())) {
                    LocalDateTime localDateTime = LocalDateTime.parse(infoData.getTaskRegDt(), sourceFormatter);

                    taskRegDt = formatter.format(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                }

                AssignmentInfo assignmentInfo = AssignmentInfo.builder()
                        .id(MqUrlType.ASSIGNMENT.getUrl()+infoData.getTaskId())
                        .timestamp(taskRegDt)
                        .curriculumStandardId(standardIds)
                        .build();

                assignmentInfoList.add(assignmentInfo);
            }

            Map<String, String> ptdInfo = new LinkedHashMap<>();
            if(wrterId != null){
                ptdInfo = assignmentGaveMapper.getUserInfo(wrterId);
            }

            AssignmentGaveMqDto resultMqData = AssignmentGaveMqDto.builder()
                    .partnerId(ptdInfo.get("ptnId"))
                    .userId(wrterId)
                    .type(MessageConstants.Type.ASSIGNMENT)
                    .verb(MessageConstants.Verb.GAVE)
                    .reqTime(currentTime)
                    .assignmentInfoList(assignmentInfoList)
                    .build();

            resultList.add(resultMqData);
        }

        return resultList;
    }

    public int updateBulkTaskMqTrnLog()throws Exception {
        return assignmentGaveMapper.updateBulkTaskMqTrnLog();
    }




}
