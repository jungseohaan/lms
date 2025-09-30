package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.MqUrlType;
import com.visang.aidt.lms.api.mq.dto.media.MediaDto;
import com.visang.aidt.lms.api.mq.dto.media.MediaInfo;
import com.visang.aidt.lms.api.mq.dto.media.MediaInfoDetail;
import com.visang.aidt.lms.api.mq.dto.media.MediaLogResultDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.MediaPlayedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaPlayedService {

    private final MediaPlayedMapper mediaPlayedMapper;


    public List<MediaDto> createMediaPlayedMq(String startTime, String endTime, String userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        List<MediaDto> mediaMqList = new ArrayList<>();

        List<MediaDto> students = mediaPlayedMapper.findStudentsFromMediaLog(startTime, endTime, userId);
        if(students == null || students.isEmpty()) {
            return mediaMqList;
        }

        for (MediaDto studentInfo : students) {
            List<MediaInfo> mediaInfoList = mediaPlayedMapper.findMediaUsageHistory(startTime, endTime, studentInfo.getUserId())
                    .stream()
                    .map(this::createMediaInfo)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

            MediaDto mediaMq = MediaDto.builder()
                    .partnerId(Optional.ofNullable(studentInfo.getPartnerId()).orElse(""))
                    .userId(studentInfo.getUserId())
                    .type(MessageConstants.Type.MEDIA)
                    .verb(MessageConstants.Verb.PLAYED)
                    .reqTime(currentTime)
                    .mediaInfoList(mediaInfoList)
                    .useTermsAgreeYn(ObjectUtils.defaultIfNull(studentInfo.getUseTermsAgreeYn(), "N"))
                    .build();

            mediaMqList.add(mediaMq);
        }

        return mediaMqList;
    }


    private MediaInfo createMediaInfo(MediaLogResultDto media) {
        MediaInfoDetail mediaInfoDetail = MediaInfoDetail.builder()
                .aitutorRecommended(media.aitutorRecommended())
                .duration(media.duration())
                .completion(media.completion())
                .attempt(media.attempt())
                .muteCnt(media.muteCnt())
                .skipCnt(media.skipCnt())
                .pauseCnt(media.pauseCnt())
                .build();

        List<String> standardIds = Optional.ofNullable(media.curriculumStandardId())
                .map(ids -> Arrays.asList(ids.split("#\\^\\|")))
                .orElse(Collections.singletonList("-1"));

        return MediaInfo.builder()
                .id(MqUrlType.MEDIA.getUrl() + media.articleId())
                .mediaType(media.medTy())
                .length(media.length())
                .difficulty(media.difficulty())
                .difficultyMin(media.difficultyMin())
                .difficultyMax(media.difficultyMax())
                .curriculumStandardId(standardIds)
                .common(media.common())
                .userId(media.stdtId())
                .mediaDetail(mediaInfoDetail)
                .build();
    }



    public Integer modifyMediaPlayedUpdate(String startTime, String endTime) {
        return mediaPlayedMapper.modifyMediaPlayedUpdate(startTime, endTime);

    }

}
