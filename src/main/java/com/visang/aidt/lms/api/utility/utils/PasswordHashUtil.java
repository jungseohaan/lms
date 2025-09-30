package com.visang.aidt.lms.api.utility.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordHashUtil {

    @Value("${key.salt.main}")
    private String keySaltMain; // 공통 시스템 키(salt)

    /**
     * 사용자별 salt(Base64) 생성
     */
    public String generateUserSaltBase64() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * CSAP 권장 방식: keySaltMain, userSalt, rawPassword를 각각 update 해서 SHA-256 해시 계산 후 hex 반환
     */
    public String computeSha256Hex(String userSaltBase64, String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(keySaltMain.getBytes(StandardCharsets.UTF_8)); // 1) 시스템 키
            digest.update(userSaltBase64.getBytes(StandardCharsets.UTF_8)); // 2) 사용자별 salt
            digest.update(rawPassword.getBytes(StandardCharsets.UTF_8));    // 3) 입력 패스워드
            byte[] hash = digest.digest();

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * 저장용 문자열 생성: "userSaltBase64:hexHash"
     */
    public String buildEncoded(String userSaltBase64, String hexHash) {
        return userSaltBase64 + ":" + hexHash;
    }

    /**
     * 저장 문자열에서 salt/해시 분리
     */
    public String[] splitEncoded(String encodedSaltAndHash) {
        return encodedSaltAndHash.split(":", 2); // [0]=salt, [1]=hex
    }

    /**
     * 검증: 저장된 "salt:hex"와 rawPassword가 일치하는지
     */
    public boolean verify(String encodedSaltAndHash, String rawPassword) {
        String[] parts = splitEncoded(encodedSaltAndHash);
        if (parts.length != 2) return false;
        String salt = parts[0];
        String storedHex = parts[1];
        String computedHex = computeSha256Hex(salt, rawPassword);
        return storedHex.equals(computedHex);
    }

    public LocalDateTime parseOrDefaultEndOfYear(String accountExpireDt) {
        return parseOrDefaultEndOfYear(accountExpireDt, null);
    }

    private LocalDateTime parseOrDefaultEndOfYear(String accountExpireDt, ZoneId zone) {

        if (zone == null) {
            zone = ZoneId.of("Asia/Seoul");
        }

        if (StringUtils.isEmpty(accountExpireDt)) {
            LocalDate last = LocalDate.now(zone).with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
            return last.atTime(23, 59, 59);
        }
        // 허용 포맷: ISO-8601 / "yyyy-MM-dd HH:mm:ss" / "yyyy-MM-dd" / "yyyyMMddHHmmss" / "yyyyMMdd"
        List<DateTimeFormatter> fmts = Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        for (DateTimeFormatter f : fmts) {
            try {
                if (f == DateTimeFormatter.ISO_LOCAL_DATE) {
                    return LocalDate.parse(accountExpireDt, f).atTime(23, 59, 59);
                }
                LocalDateTime ldt = LocalDateTime.parse(accountExpireDt.replace("T", " "), f);
                return ldt;
            }
             catch (DateTimeParseException ex) {
                log.error("{} parse error - {}", accountExpireDt, ex.getMessage());
            } catch (RuntimeException ex) {
                log.error("{} runtime error - {}", accountExpireDt, ex.getMessage());
            }
            catch (Exception ignore) {
                log.error("{} ignore error - {}", accountExpireDt, ignore.getMessage());
            }
        }
        // 파싱 실패 시 올해 말 23:59:59
        LocalDate last = LocalDate.now(zone).with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
        return last.atTime(23, 59, 59);
    }
}