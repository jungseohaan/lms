package com.visang.aidt.lms.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ReadableRequestWrapper extends HttpServletRequestWrapper {
    private final Charset encoding;
    private static final byte[] EMPTY = new byte[0];
    private byte[] rawData = EMPTY;
    private Map<String, String[]> params = new HashMap<>();

    public ReadableRequestWrapper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap()); // 원래의 파라미터를 저장

        String charEncoding = request.getCharacterEncoding(); // 인코딩 설정
        this.encoding = StringUtils.isBlank(charEncoding) ? StandardCharsets.UTF_8 : Charset.forName(charEncoding);

        try {
            // Wrapper가 new ByteArrayInputStream(buf) 에 null을 생성하여, 신규 로직 적용
            // 에러메세지: Cannot read the array length because "buf" is null
            byte[] body;
            try (InputStream is = request.getInputStream()) {
                body = (is != null) ? is.readAllBytes() : EMPTY;
            }
            this.rawData = body;
            // body 파싱 (rawData에서 직접 문자열화, io 레이어에서 가져오지 않고 메모리에서 가져오도록 변경함)
            String collect = new String(this.rawData, this.encoding);

            if (StringUtils.isEmpty(collect)) { // body 가 없을경우 로깅 제외
                return;
            }
            if (request.getContentType() != null && request.getContentType().contains(
                    ContentType.MULTIPART_FORM_DATA.getMimeType())) { // 파일 업로드시 로깅제외
                return;
            }

            // JSON Parsing using org.json
            if (collect.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(collect);
                setParameter("requestBody", jsonArray.toString());
            } else {
                JSONObject jsonObject = new JSONObject(collect);
                for (String key : jsonObject.keySet()) {
                    setParameter(key, jsonObject.get(key).toString().replace("\"", "\\\""));
                }
            }
        } catch (IOException e) {
            log.error("ReadableRequestWrapper - InputStream 읽기 오류: {}", e.getMessage());
        } catch (JSONException e) {
            log.warn("ReadableRequestWrapper - JSON 파싱 오류: {}", e.getMessage());
        } catch (OutOfMemoryError e) {
            log.error("ReadableRequestWrapper - 메모리 부족으로 인한 처리 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("ReadableRequestWrapper - 예상치 못한 초기화 오류: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);
        if (paramArray != null && paramArray.length > 0) {
            return paramArray[0];
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] dummyParamValue = params.get(name);

        if (dummyParamValue != null) {
            result = new String[dummyParamValue.length];
            System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
        }
        return result;
    }

    public void setParameter(String name, String value) {
        String[] param = {value};
        setParameter(name, param);
    }

    public void setParameter(String name, String[] values) {
        params.put(name, values);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData == null ? EMPTY : this.rawData);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }
}