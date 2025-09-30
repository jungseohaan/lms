package com.visang.aidt.lms.api.mq.controller;

import com.visang.aidt.lms.api.mq.service.MediaPlayedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NatsSendControllerTest {

    /*@Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaPlayedService mediaPlayedService;

    @Test
    void testPushNatsBulkMqMediaPlayed_Success() throws Exception {
        // 요청 데이터 설정
        String requestContent = """
                            {
                                "testStartTime": "2024-07-21 16:45:00",
                                "testEndTime": "2024-07-21 17:19:03"
                            }
                            """;

        // 요청을 수행하고 응답을 검증
        mockMvc.perform(post("/mq/pushMediaPlayedBulkMQ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.result").isNotEmpty());
    }

    @Test
    void testPushNatsBulkMqMediaPlayed_Failure() throws Exception {
        // 서비스 메소드가 예외를 던지도록 설정
        when(mediaPlayedService.createMediaPlayedMq(anyString(), anyString())).thenThrow(new RuntimeException("테스트 오류"));

        // 요청을 수행하고 응답을 검증
        mockMvc.perform(post("/mq/pushMediaPlayedBulkMQ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "testStartTime": "2024-07-21 16:45:00",
                                    "testEndTime": "2024-07-21 17:19:03"
                                }
                                """))
                .andExpect(status().is5xxServerError());
                *//*.andExpect(jsonPath("$.result").value("fail"))
                .andExpect(jsonPath("$.errorMsg").value("테스트 오류"));*//*

        // 서비스 메소드 호출 횟수 검증
        verify(mediaPlayedService, times(1)).createMediaPlayedMq(anyString(), anyString());
    }*/
}
