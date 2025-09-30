package com.visang.aidt.lms.api.utility.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import java.time.ZoneId;

/**
 * ApiAuthUtil (확장)
 *
 * - 기존 generateAuthToken(...) 등은 그대로 사용
 * - verifyAuthTokenByTimeWindow(...) : salt2를 클라이언트에서 받지 않고,
 *   서버 시간(now)부터 now - allowedSeconds 까지 모든 초에 대해 해시를 생성하여 비교.
 *
 * 주의:
 * - 시간 포맷은 "yyyy-MM-dd HH:mm:ss" (초 단위)
 * - ZoneId는 필요에 따라 지정. 기본은 서버 시스템존.
 */
public final class ApiAuthUtil {

    private ApiAuthUtil() {}

    public static final DateTimeFormatter SALT2_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault(); // 필요시 ZoneId.of("Asia/Seoul")

    // 기존 sha256Hex / bytesToHex / generateAuthToken / constantTimeEqualsHex 재사용
    public static String sha256Hex(String input) {
        Objects.requireNonNull(input, "input must not be null");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    public static String generateAuthToken(String salt1, String uuid, String apiUrl, String salt2) {
        if (salt1 == null) salt1 = "";
        if (uuid == null) uuid = "";
        if (apiUrl == null) apiUrl = "";
        if (salt2 == null) salt2 = "";
        String raw = salt1 + uuid + apiUrl + salt2;
        return sha256Hex(raw);
    }

    private static boolean constantTimeEqualsHex(String aHex, String bHex) {
        if (aHex == null || bHex == null) return false;
        byte[] a = aHex.getBytes(StandardCharsets.UTF_8);
        byte[] b = bHex.getBytes(StandardCharsets.UTF_8);
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) result |= a[i] ^ b[i];
        return result == 0;
    }

    public static String verifyAuthTokenByTimeWindow(String receivedHex,
                                                     String salt1,
                                                     String uuid,
                                                     String apiUrl) {
        return verifyAuthTokenByTimeWindow(receivedHex, salt1, uuid, apiUrl, 3L, ZoneId.of("Asia/Seoul"));
    }

    /**
     * 핵심 메서드:
     * - receivedHex : 클라이언트가 보낸 Bearer 토큰 (hex)
     * - salt1 : 서버에서 알고 있는 고정 키
     * - uuid, apiUrl : 페이로드/요청에서 얻은 값
     * - allowedSeconds : 예: 3  (서버에서 현재시각부터 과거 allowedSeconds 초까지 검사)
     * - zone : 시간대 (클라이언트가 특정 타임존을 사용하지 않는다면 서버 시스템 존 사용)
     *
     * 반환값:
     * - 일치(succeeded)하면 matchedSalt2(찾은 yyyy-MM-dd HH:mm:ss) 문자열을 반환
     * - 못 찾으면 null 반환
     *
     * (참고) 반환된 matchedSalt2를 이용해 로그 기록하거나 Redis에 (uuid, apiUrl, matchedSalt2) 형태로
     *       소비(used) 표시하면 재사용 방지에 활용할 수 있음.
     */
    public static String verifyAuthTokenByTimeWindow(String receivedHex,
                                                     String salt1,
                                                     String uuid,
                                                     String apiUrl,
                                                     long allowedSeconds,
                                                     ZoneId zone) {
        if (receivedHex == null || receivedHex.isBlank()) return null;
        if (uuid == null || uuid.isBlank()) return null;
        if (zone == null) zone = DEFAULT_ZONE;

        // 현재 시각(초 단위). LocalDateTime.now(zone)
        LocalDateTime now = LocalDateTime.now(zone);

        // 루프: now, now -1s, now -2s, ... up to allowedSeconds (inclusive)
        for (long offset = 0; offset <= allowedSeconds; offset++) {
            LocalDateTime t = now.minusSeconds(offset);
            String salt2 = t.format(SALT2_FORMATTER);
            String expected = generateAuthToken(salt1, uuid, apiUrl, salt2);
            if (constantTimeEqualsHex(expected, receivedHex)) {
                return salt2; // matched
            }
        }
        return null; // none matched
    }
}