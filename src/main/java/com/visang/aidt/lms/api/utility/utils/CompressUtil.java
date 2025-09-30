package com.visang.aidt.lms.api.utility.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@Slf4j
class CompressUtil {
    private static String SECRET_KEY;

    protected static void setSecretKey(String key) {
        SECRET_KEY = key;
    }

    protected static String hash(String input) {

        String result = null;

        if (input == null) {
            return null;
        }

        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);

            byte[] hashBytes = hmacSha256.doFinal(input.getBytes(StandardCharsets.UTF_8));
            result = toHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            log.error("CompressUtil HMAC SHA256 hashing error {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("CompressUtil HMAC SHA256 hashing error {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("CompressUtil HMAC SHA256 hashing error {}", e.getMessage());
        } catch (Exception e) {
            log.error("CompressUtil HMAC SHA256 hashing error {}", e.getMessage());
        }

        return result;
    }

    private static String toHex(final byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (final byte hashByte : hashBytes) {
            hexString.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hexString.toString();
    }
}
