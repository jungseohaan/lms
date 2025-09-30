package com.visang.aidt.lms.api.utility.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
    1. 먼저 직렬화
     - Map을 JSON으로 직렬화하고 크기를 확인합니다.
     - 이미 MAX_LOG_SIZE 이하라면 그대로 String으로 반환합니다.

    2. REMOVE 단계
     - REMOVE_KEYS_PRIORITY에 있는 키들을 순서대로 제거하면서 다시 직렬화합니다.
     - 크기가 줄어서 제한 이하가 되면 그 상태로 String 반환합니다.

    3. TRUNCATE 단계
     - 아직도 크면 TRUNCATABLE_FIELDS에 있는 문자열 값들을 잘라내기 합니다.
     - 패스 단위(TRUNCATE_ATTEMPTS)로 반복하되, 각 패스에서 후보 필드를 모두 절단하기 때문에 무한루프가 발생하지 않습니다.
     - 줄일 수 있는 문자열이 없거나 더 줄일 여지가 없으면 루프가 종료됩니다.

    4. 최후 방어선
     - 그래도 1MB를 초과하면 하드 컷(truncateUtf8ByBytes)을 합니다.
 * 크기 제한(바이트) 내에서 JSON 로그를 생성하는 유틸.
 * - 단계: REMOVE(삭제) → TRUNCATE(순차 절단) → 필수키만 재구성 → 최후 하드 컷
 * - 필수 키는 보호(PROTECTED)하며, 직렬화 시 먼저 기록
 * - isLogEncode=true면 최종 문자열에서 \n→\t, \r 제거
 * - 메모리 최적화: ThreadLocal BAOS 재사용, 중간 String 최소화
 * - CSAP 대비: print/trace 없음, 예외는 세분화 코드로 래핑
 */
public final class LogSizerUtil {

    /** 전송 허용 최대 바이트 (예: 1MB) */
    private static final int MAX_LOG_SIZE = 1_048_576;

    /** 통째로 제거해도 되는 키 우선순위 (원하는 대로 수정) */
    private static final String[] REMOVE_KEYS_PRIORITY = {
            "resp", "message", "eTime", "sTime", "duration", "hash"
    };

    /** 값만 줄일(절단) 후보 키 — 순서대로 하나씩 줄임: req → errMsg */
    private static final List<String> TRUNCATABLE_FIELDS =
            Arrays.asList("req", "errMsg");

    /** 절대로 제거/절단하지 말아야 할 필수 키(개수 제한 없음, 필요 시 값 추가) */
    private static final Set<String> PROTECTED_KEYS =
            new LinkedHashSet<>(Arrays.asList("appName", "profile", "uuid", "uType", "cid", "errCd", "exception", "url"));
    // ↑ 필요 시 정적 초기화 블록이나 별도 setter로 더 추가할 수 있습니다.

    /** 순차 절단 시 한 번에 줄일 최소 바이트(너무 미세하게 줄이면 직렬화 반복이 많아짐) */
    private static final int MIN_TRUNCATE_CHUNK = 8 * 1024; // 8KB

    /** ThreadLocal BAOS로 큰 버퍼 재사용 (초기 16KB) */
    private static final ThreadLocal<ByteArrayOutputStream> LOCAL_BAOS =
            ThreadLocal.withInitial(() -> new ByteArrayOutputStream(16 * 1024));

    private LogSizerUtil() {}

    /**
     * 크기 제한을 만족하는 최종 JSON 문자열 생성.
     * @param logData       로그 맵(절단 과정에서 문자열 값이 바뀔 수 있음)
     * @param objectMapper  Jackson ObjectMapper(싱글턴 권장)
     * @param isLogEncode   true면 최종 문자열에서 \n→\t, \r 제거
     * @return 제한 크기 내의 최종 JSON 문자열
     * @throws LogBuildException 내부 예외를 원인코드와 함께 래핑해 던짐
     */
    public static String buildCappedJson(Map<String, Object> logData,
                                         ObjectMapper objectMapper,
                                         boolean isLogEncode) throws LogBuildException {
        if (logData == null) {
            logData = new LinkedHashMap<>();
        }

        final Set<String> excluded = new HashSet<>();
        final ByteArrayOutputStream baos = acquireBaos();

        // 1) 최초 직렬화
        int size = serialize(logData, excluded, objectMapper, baos);
        if (size > MAX_LOG_SIZE) {
            // 2) REMOVE 단계: 통째 삭제(필수키 제외)
            for (String k : REMOVE_KEYS_PRIORITY) {
                if (PROTECTED_KEYS.contains(k)) continue;
                if (!logData.containsKey(k)) continue;
                excluded.add(k);
                size = serialize(logData, excluded, objectMapper, baos);
                if (size <= MAX_LOG_SIZE) break;
            }
        }

        // 3) TRUNCATE 단계: req → errMsg → url 순서로, 필요한 만큼 반복 절단
        if (size > MAX_LOG_SIZE) {
            for (String key : TRUNCATABLE_FIELDS) {
                if (PROTECTED_KEYS.contains(key)) continue;
                if (excluded.contains(key)) continue;

                Object v = logData.get(key);
                if (!(v instanceof String s) || s.isEmpty()) continue;

                // 필요할 때까지 현재 키만 반복 절단
                while (size > MAX_LOG_SIZE && s.length() > 0) {
                    int over = size - MAX_LOG_SIZE;
                    int cut = Math.max(over, MIN_TRUNCATE_CHUNK); // over가 작아도 최소 8KB 단위로 줄임

                    byte[] vb = s.getBytes(StandardCharsets.UTF_8);
                    int newMax = Math.max(0, vb.length - cut);
                    String truncated = truncateUtf8ByBytes(s, newMax);
                    if (truncated.length() == s.length()) {
                        // 더 이상 줄일 수 없으면 탈출(무한루프 방지)
                        break;
                    }
                    s = truncated;
                    logData.put(key, s);

                    size = serialize(logData, excluded, objectMapper, baos);
                }

                if (size <= MAX_LOG_SIZE) break; // 충분히 줄였으면 다음 단계로
            }
        }

        // 4) 여전히 크면: 필수 키만으로 Map 재구성 후 직렬화 (하드컷 전에 JSON 유지 보장)
        if (size > MAX_LOG_SIZE) {
            Map<String, Object> onlyProtected = new LinkedHashMap<>();
            for (String key : PROTECTED_KEYS) {
                if (logData.containsKey(key)) {
                    onlyProtected.put(key, logData.get(key));
                }
            }
            size = serializeExact(onlyProtected, objectMapper, baos);
        }

        // 5) 최종 문자열 생성(여기서 딱 1번)
        String json = baos.toString(StandardCharsets.UTF_8);

        // 6) 정규화(선택): \n→\t, \r 제거 (길이는 같거나 줄어듦)
        if (isLogEncode) {
            json = json.replace("\n", "\t").replace("\r", "");
        }

        // 7) 혹시라도 초과 시 최후 하드 컷(UTF-8 경계 보존) — JSON 구조는 깨질 수 있음
        if (json.getBytes(StandardCharsets.UTF_8).length > MAX_LOG_SIZE) {
            json = truncateUtf8ByBytes(json, MAX_LOG_SIZE);
        }

        maybeShrinkBaos(baos);
        return json;
    }

    // ---------------- 내부 구현 ----------------

    /** 직렬화(제거/보호 키 로직 포함)만 수행하고 문자열 생성은 하지 않음. 반환값은 현재 바이트 길이 */
    private static int serialize(Map<String, Object> logData,
                                 Set<String> excludeKeys,
                                 ObjectMapper om,
                                 ByteArrayOutputStream baos) throws LogBuildException {
        baos.reset();
        try (JsonGenerator g = om.getFactory().createGenerator(baos, JsonEncoding.UTF8)) {
            g.writeStartObject();

            // a) 필수 키 먼저 (하드 컷 대비 생존 확률 ↑)
            for (String key : PROTECTED_KEYS) {
                if (excludeKeys.contains(key)) continue;
                if (!logData.containsKey(key)) continue;
                g.writeFieldName(key);
                om.writeValue(g, logData.get(key));
            }

            // b) 나머지 키
            for (Map.Entry<String, Object> e : logData.entrySet()) {
                String key = e.getKey();
                if (PROTECTED_KEYS.contains(key)) continue; // 이미 a)에서 처리
                if (excludeKeys.contains(key)) continue;
                g.writeFieldName(key);
                om.writeValue(g, e.getValue());
            }

            g.writeEndObject();
        } catch (JsonMappingException e) {
            throw new LogBuildException(CauseCode.JSON_MAPPING_ERROR,
                    "JSON 매핑 오류: " + safePath(e) + " - " + e.getOriginalMessage(), e);
        } catch (JsonProcessingException e) {
            throw new LogBuildException(CauseCode.JSON_PROCESSING_ERROR,
                    "JSON 처리 오류: " + e.getOriginalMessage(), e);
        } catch (IOException e) {
            throw new LogBuildException(CauseCode.IO_ERROR,
                    "직렬화 IO 오류: " + e.getMessage(), e);
        }
        return baos.size();
    }

    /** 주어진 Map을 그대로 직렬화(필수키 우선순서/제거 로직 없이) */
    private static int serializeExact(Map<String, Object> map,
                                      ObjectMapper om,
                                      ByteArrayOutputStream baos) throws LogBuildException {
        baos.reset();
        try (JsonGenerator g = om.getFactory().createGenerator(baos, JsonEncoding.UTF8)) {
            g.writeStartObject();
            for (Map.Entry<String, Object> e : map.entrySet()) {
                g.writeFieldName(e.getKey());
                om.writeValue(g, e.getValue());
            }
            g.writeEndObject();
        } catch (JsonMappingException e) {
            throw new LogBuildException(CauseCode.JSON_MAPPING_ERROR,
                    "JSON 매핑 오류: " + safePath(e) + " - " + e.getOriginalMessage(), e);
        } catch (JsonProcessingException e) {
            throw new LogBuildException(CauseCode.JSON_PROCESSING_ERROR,
                    "JSON 처리 오류: " + e.getOriginalMessage(), e);
        } catch (IOException e) {
            throw new LogBuildException(CauseCode.IO_ERROR,
                    "직렬화 IO 오류: " + e.getMessage(), e);
        }
        return baos.size();
    }

    /** UTF-8 바이트 길이 기준으로 안전하게 자르기(문자 경계 보존) */
    private static String truncateUtf8ByBytes(String s, int maxBytes) throws LogBuildException {
        if (maxBytes < 0) return "";
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) return s;

        int end = maxBytes;
        // 다중바이트 연속(10xxxxxx) 위에서 끝나지 않도록 후퇴
        while (end > 0 && (bytes[end] & 0b1100_0000) == 0b1000_0000) end--;

        if (end > 0) {
            int lead = bytes[end] & 0xFF;
            int need = (lead >> 5 == 0b110) ? 2 :
                       (lead >> 4 == 0b1110) ? 3 :
                       (lead >> 3 == 0b11110) ? 4 : 1;
            if (end + need > maxBytes) {
                int tmp = end - 1;
                while (tmp > 0 && (bytes[tmp] & 0b1100_0000) == 0b1000_0000) tmp--;
                end = Math.max(0, tmp);
            }
        }

        try {
            return new String(bytes, 0, Math.max(0, end), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new LogBuildException(CauseCode.UTF8_TRUNCATE_ERROR,
                    "UTF-8 절단 중 오류", e);
        }
    }

    private static String safePath(JsonMappingException e) {
        try {
            return e.getPathReference();
        } catch (Exception ignored) {
            return "(path unavailable)";
        }
    }

    private static ByteArrayOutputStream acquireBaos() {
        ByteArrayOutputStream baos = LOCAL_BAOS.get();
        baos.reset();
        return baos;
    }

    private static void maybeShrinkBaos(ByteArrayOutputStream baos) {
        // 너무 커졌다면(예: 2MB+) ThreadLocal 버퍼 반납하여 메모리 홀드 방지
        if (baos.size() > (2 * 1024 * 1024)) {
            LOCAL_BAOS.remove();
        }
    }

    // ---------------- 예외 타입 ----------------

    public static final class LogBuildException extends Exception {
        private final CauseCode causeCode;

        public LogBuildException(CauseCode causeCode, String message, Throwable cause) {
            super(message, cause);
            this.causeCode = causeCode;
        }

        public CauseCode getCauseCode() {
            return causeCode;
        }
    }

    public enum CauseCode {
        JSON_MAPPING_ERROR,     // Jackson 매핑 실패
        JSON_PROCESSING_ERROR,  // Jackson 처리/생성 실패
        IO_ERROR,               // IO 계층 문제
        UTF8_TRUNCATE_ERROR     // UTF-8 절단 로직 문제
    }
}
