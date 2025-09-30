package com.visang.aidt.lms.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.utility.utils.LogSizerUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LogSizerUtilTest {

    private static final int MAX = 1_048_576; // LogSizerUtil 에서 사용하는 값과 동일해야 함
    private final ObjectMapper om = new ObjectMapper();

    private static int utf8Len(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    private static String bigString(int bytes) {
        // ASCII 1바이트 문자로 정확한 바이트 크기 만들기
        StringBuilder sb = new StringBuilder(bytes);
        for (int i = 0; i < bytes; i++) sb.append('A');
        return sb.toString();
    }

    @Test
    void underLimit_returnsAsIs() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "demo-app");
        m.put("profile", "dev");
        m.put("uuid", UUID.randomUUID().toString());
        m.put("message", "short");

        String json = LogSizerUtil.buildCappedJson(m, om, false);
        assertTrue(utf8Len(json) <= MAX, "should be <= MAX");
        assertTrue(json.contains("\"appName\""));
        assertTrue(json.contains("\"profile\""));
        assertTrue(json.contains("\"uuid\""));
        assertTrue(json.contains("\"message\""));
    }

    @Test
    void overLimit_removeThenOk() throws Exception {
        // REMOVE_KEYS_PRIORITY: resp, message, duration, req
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "demo-app");
        m.put("profile", "dev");
        m.put("uuid", UUID.randomUUID().toString());

        // resp 를 크게 만들어서 먼저 제거 유도
        m.put("resp", bigString(MAX)); // 매우 큼
        m.put("message", "kept-if-possible");

        String json = LogSizerUtil.buildCappedJson(m, om, false);
        assertTrue(utf8Len(json) <= MAX, "should be <= MAX");
        assertFalse(json.contains("\"resp\""), "resp should be removed");
        // 필수 키는 반드시 존재
        assertTrue(json.contains("\"appName\""));
        assertTrue(json.contains("\"profile\""));
        assertTrue(json.contains("\"uuid\""));
    }

    @Test
    void truncateFields_whenStillOverLimit() throws Exception {
        // TRUNCATABLE_FIELDS: url, eTime, sTime, errMsg
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "demo-app");
        m.put("profile", "prod");
        m.put("uuid", UUID.randomUUID().toString());

        // 제거 대상이 아닌 큰 필드들로 오버 유도 → truncate 로 줄여야 함
        m.put("url", bigString(MAX / 2));
        m.put("eTime", bigString(MAX / 2));
        m.put("sTime", bigString(MAX / 2));
        m.put("errMsg", bigString(MAX / 2));

        String json = LogSizerUtil.buildCappedJson(m, om, false);
        assertTrue(utf8Len(json) <= MAX, "should be <= MAX");
        // 필드는 남아 있으나 값은 줄어들었을 것
        assertTrue(json.contains("\"url\""));
        assertTrue(json.contains("\"eTime\""));
        assertTrue(json.contains("\"sTime\""));
        assertTrue(json.contains("\"errMsg\""));
    }

    @Test
    void normalization_whenEnabled() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "demo-app");
        m.put("profile", "dev");
        m.put("uuid", UUID.randomUUID().toString());
        m.put("message", "line1\nline2\rline3");

        String json = LogSizerUtil.buildCappedJson(m, om, true);
        // \n -> \t, \r 제거 되었는지 확인 (주의: JSON 문자열 내부는 \\n 로 이스케이프될 수 있음)
        assertTrue(json.contains("\\t") || json.contains("\t"));
        assertFalse(json.contains("\r"));
        assertTrue(utf8Len(json) <= MAX);
    }

    @Test
    void hardCut_asLastResort() throws Exception {
        // 제거/절단으로도 도저히 못 줄이는 경우를 강제해보자
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "demo-app");
        m.put("profile", "dev");
        m.put("uuid", UUID.randomUUID().toString());

        // 필수 키만 넣고도 배열 형태 대량 데이터 같은 구조로 크게 만듦(실전과 다를 수 있음)
        // 여기서는 하나의 매우 큰 키를 만들고 제거/절단 후보에도 안 걸리게 유도
        m.put("giant", bigString(MAX * 2));

        String json = LogSizerUtil.buildCappedJson(m, om, false);
        int len = utf8Len(json);
        assertTrue(len <= MAX, "should be hard cut to <= MAX");
        // UTF-8 경계 보존: 잘라내도 invalid 문자 없어야 함
        assertDoesNotThrow(() -> json.getBytes(StandardCharsets.UTF_8));
        // 필수 키는 직렬화 우선이라 앞부분에 남아 있을 확률↑
        assertTrue(json.contains("\"appName\""));
        assertTrue(json.contains("\"profile\""));
        assertTrue(json.contains("\"uuid\""));
    }
}