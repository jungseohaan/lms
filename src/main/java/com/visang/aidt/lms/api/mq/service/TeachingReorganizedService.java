package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.dto.teaching.ReorganizedInfo;
import com.visang.aidt.lms.api.mq.dto.teaching.TeachingReorganizedMqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.TeachingReorganizedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeachingReorganizedService {

    private final TeachingReorganizedMapper teachingReorganizedMapper;

    @Transactional(readOnly = true)
    public List<TeachingReorganizedMqDto> createTeachingReorganized(ReorganizedInfo reorganizedInfoSetting) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        Map<String, Set<String>> userStandardIds = new HashMap<>();
        List<TeachingReorganizedMqDto> teachingReorganizedMqList = new ArrayList<>();

        List<ReorganizedInfo> reorganizedInfoList = teachingReorganizedMapper.findReorganizedInfoList(reorganizedInfoSetting);

        for (ReorganizedInfo reorganizedDto : reorganizedInfoList) {
            String userId = reorganizedDto.getUserId();
            Set<String> standardIds = userStandardIds.computeIfAbsent(userId, k -> new HashSet<>());

            if (reorganizedDto.getStandardId() != null && !reorganizedDto.getStandardId().isEmpty()) {
                String[] parts = reorganizedDto.getStandardId().split("#\\^\\|");
                standardIds.addAll(Arrays.asList(parts));
            }
        }

        for (Map.Entry<String, Set<String>> entry : userStandardIds.entrySet()) {
            String userId = entry.getKey();
            List<String> uniqueStandardIds = new ArrayList<>(entry.getValue());

            ReorganizedInfo reorganizedInfo = ReorganizedInfo.builder()
                    .curriculumStandardId(uniqueStandardIds)
                    .userId(userId)
                    .build();

            List<ReorganizedInfo> reorganizedDtoList = new ArrayList<>();
            reorganizedDtoList.add(reorganizedInfo);

            Map<String, String> ptdInfo = new LinkedHashMap<>();
            if (userId != null) {
                ptdInfo = teachingReorganizedMapper.getUserInfo(userId);
            }

            TeachingReorganizedMqDto teachingReorganizedMqDto = TeachingReorganizedMqDto.builder()
                    .partnerId(ptdInfo.get("ptnId"))
                    .userId(userId)
                    .type(MessageConstants.Type.TEACHING)
                    .verb(MessageConstants.Verb.REORGANIZED)
                    .reqTime(currentTime)
                    .reorganizedInfoList(reorganizedDtoList)
                    .useTermsAgreeYn(ObjectUtils.defaultIfNull(ptdInfo.get("useTermsAgreeYn"), "N"))
                    .build();

            teachingReorganizedMqList.add(teachingReorganizedMqDto);
        }

        return teachingReorganizedMqList;
    }}
