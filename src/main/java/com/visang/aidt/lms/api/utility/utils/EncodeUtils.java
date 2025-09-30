package com.visang.aidt.lms.api.utility.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 인코딩 유틸리티
 */
@Slf4j
public class EncodeUtils {

    /**
     * UTF-8로 인코딩한 문자열을 반환한다.
     * 공백은 그대로 유지된다.
     * 
     * @param fileName
     * @return
     */
    public static String encodeUtf8(String fileName) {
        String encoded = null;

        try {
            // 공백을 임시로 다른 문자로 대체
            String tempFileName = fileName.replace(" ", "___SPACE___");
            encoded = URLEncoder.encode(tempFileName, "UTF-8");
            // 임시 문자를 다시 공백으로 변환
            encoded = encoded.replace("___SPACE___", " ");
        } catch (UnsupportedEncodingException ignore) {
            // should never happens
            log.error(ignore.getMessage(), ignore);
        }

        return encoded;
    }
    private static byte[] bszUser_key = {
            (byte)0x6B, (byte)0x69, (byte)0x6E, (byte)0x73,
            (byte)0x49, (byte)0x56, (byte)0x31, (byte)0x30,
            (byte)0x30, (byte)0x34, (byte)0x76, (byte)0x61,
            (byte)0x6C, (byte)0x63, (byte)0x68, (byte)0x6B
    };

    private static byte[] bszIV = {
            (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x34,
            (byte)0x72, (byte)0x68, (byte)0x6B, (byte)0x73,
            (byte)0x66, (byte)0x6C, (byte)0x6B, (byte)0x69,
            (byte)0x6E, (byte)0x73, (byte)0x4D, (byte)0x4B
    };


}
