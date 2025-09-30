package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.dto.media.MediaDto;
import com.visang.aidt.lms.api.mq.dto.media.MediaLogResultDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.MediaPlayedMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaPlayedServiceTest {

    @Mock
    private MediaPlayedMapper mediaPlayedMapper;

    @InjectMocks
    private MediaPlayedService mediaPlayedService;

    @Test
    void testCreateMediaPlayedMq_Success() throws Exception {
        // 목 데이터 설정
        List<MediaDto> mockStudents = new ArrayList<>();
        mockStudents.add(MediaDto.builder()
                .partnerId("partner-123")
                .userId("user-456")
                .type("MEDIA")
                .verb("PLAYED")
                .reqTime(String.valueOf(new Timestamp(System.currentTimeMillis())))
                .mediaInfoList(new ArrayList<>())
                .build());

        when(mediaPlayedMapper.findStudentsFromMediaLog(anyString(), anyString(), "")).thenReturn(mockStudents);

        List<MediaLogResultDto> mockMediaLogs = new ArrayList<>();
        mockMediaLogs.add(MediaLogResultDto.builder()
                .stdtId("user-456")
                .articleId("article-123")
                .medId("med-789")
                .medTy("video")
                .length(120)
                .difficulty(3)
                .difficultyMin(1)
                .difficultyMax(5)
                .curriculumStandardId("standard1#^|standard2")
                .common(false)
                .aitutorRecommended(true)
                .duration(100)
                .completion(true)
                .attempt(3)
                .muteCnt(1)
                .skipCnt(0)
                .pauseCnt(2)
                .build());

        when(mediaPlayedMapper.findMediaUsageHistory(anyString(), anyString(), anyString())).thenReturn(mockMediaLogs);

        // 메소드 실행
        List<MediaDto> result = mediaPlayedService.createMediaPlayedMq("2024-07-10 09:23:16", "2024-07-24 17:19:03", "");

        // 결과가 null이 아닌지 검증
        assertNotNull(result);
        // findStudentsFromMediaLog 메소드가 한 번 호출되었는지 검증
        verify(mediaPlayedMapper, times(1)).findStudentsFromMediaLog(anyString(), anyString(), "");
        // findMediaUsageHistory 메소드가 한 번 호출되었는지 검증
        verify(mediaPlayedMapper, times(1)).findMediaUsageHistory(anyString(), anyString(), anyString());
    }

    @Test
    void testModifyMediaPlayedUpdate() {
        // 메소드 실행
        mediaPlayedService.modifyMediaPlayedUpdate("2024-07-10 09:23:16", "2024-07-24 17:19:03");

        // Mapper 메소드 호출 횟수 검증
        verify(mediaPlayedMapper, times(1)).modifyMediaPlayedUpdate(anyString(), anyString());
    }
}
