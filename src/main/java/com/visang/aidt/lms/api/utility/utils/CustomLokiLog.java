package com.visang.aidt.lms.api.utility.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomLokiLog {

    private final static String BASE_PACKAGE = "com.visang.aidt.lms.api";
    private static volatile boolean LOKI_LOG_ENABLED = true;

    @Component
    private static class LokiFlagLoader {
        private LokiFlagLoader (@Value("${loki.log.enabled}") boolean enabled) {
            LOKI_LOG_ENABLED = enabled;
        }
    }

    public static String errorLog(final Exception ex) {

        StringBuilder stackTraceString = new StringBuilder();
        StringBuilder key = new StringBuilder();
        String apmErrorPoint =  commonError(stackTraceString, key, ex);
        return logTemplate(ex.getMessage(), stackTraceString.toString(), CompressUtil.hash(key.toString()), apmErrorPoint);
    }

    public static String shortErrorLog(final Exception ex, int start, int end) {
        String message = errorLog(ex);
        if(message.length() < end) {
            return message;
        }
        return message.substring(start, end);
    }

    public static String parameterErrorLog(final MissingServletRequestParameterException ex) {

        StringBuilder stackTraceString = new StringBuilder();
        StringBuilder key = new StringBuilder();
        String apmErrorPoint =  commonError(stackTraceString, key, ex);
        String apmCheckError = String.format("%s - %s %s", ex.getMessage(), ex.getParameterType(), ex.getParameterName());

        return logTemplate(apmCheckError, stackTraceString.toString(), CompressUtil.hash(key.toString()), apmErrorPoint);
    }

    public static String typeMissMatchErrorLog(final MethodArgumentTypeMismatchException ex) {

        StringBuilder stackTraceString = new StringBuilder();
        StringBuilder key = new StringBuilder();
        String apmErrorPoint =  commonError(stackTraceString, key, ex);
        String apmCheckError = String.format("%s - %s %s", ex.getMessage(), ex.getParameter(), ex.getName());

        return logTemplate(apmCheckError, stackTraceString.toString(), CompressUtil.hash(key.toString()), apmErrorPoint);
    }

    private static String commonError(StringBuilder stackTraceString, StringBuilder key, Exception ex) {

        String apmErrorPoint = null;
        // 직접 개발한 소스에서 발생한 에러인지 체크
        boolean isDeveloperMistake = false;

        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            stackTraceString.append(stackTraceElement).append("\t");

            if (stackTraceElement.toString().startsWith(BASE_PACKAGE)) {

                isDeveloperMistake = true;

                if (apmErrorPoint == null) {
                    apmErrorPoint = stackTraceElement.toString();
                }

                key.append(stackTraceElement.getMethodName())
                        .append(stackTraceElement.getFileName())
                        .append(stackTraceElement.getLineNumber());
            }
        }

        // stacktrace 에서 검출되지 않았을 때
        if (!isDeveloperMistake) {
            key.append(stackTraceString);
        }
        return apmErrorPoint;
    }

    public static String sqlErrorLog(final Exception ex) {

        String apmErrorPoint = null;

        StringBuilder key = new StringBuilder();
        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            if (stackTraceElement.toString().startsWith(BASE_PACKAGE)) {

                if (apmErrorPoint == null) {
                    apmErrorPoint = stackTraceElement.toString();
                }

                key.append(stackTraceElement.getMethodName())
                        .append(stackTraceElement.getFileName())
                        .append(stackTraceElement.getLineNumber());
            }
        }

        // 개행으로 발생하는 이슈 해결
        String message = ex.getMessage();

        return logTemplate(ex.getCause().toString(), message, CompressUtil.hash(key.toString()), apmErrorPoint);
    }

    public static String dataRestLog(final Exception ex, HttpServletRequest httpServletRequest) {
        StringBuilder stackTraceString = new StringBuilder();

        Object object = httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String apmErrorPoint = null;
        String attribute = null;

        if (object != null) {

            @SuppressWarnings("unchecked")
            Map<String, String> uriTemplateAttribute = (Map<String, String>) object;

            String entity = uriTemplateAttribute.get("repository");
            String property = uriTemplateAttribute.get("property");
            String search = uriTemplateAttribute.get("search");

            // 첫 글자를 대문자로 변경하는 로직
            if (entity != null) {
                entity = String.valueOf(entity.charAt(0)).toUpperCase(Locale.ROOT) + entity.substring(1);
            }

            // repository, property, search 유형에 제외하고 {key : value, ...} json 형태의 format 생성
            attribute = uriTemplateAttribute.entrySet().stream()
                .filter(entry -> !(entry.getKey().equals("repository") || entry.getKey().equals("property") || entry.getKey().equals("search")))
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(", "));

            // 유형에 따른 exception point 제작
            if(search != null) {
                apmErrorPoint = entity + "Repository : " + search;
            } else if(property != null) {
                apmErrorPoint = entity + "Repository &" + entity + "Entity : " + property;
            } else {
                apmErrorPoint = entity + "Entity";
            }
        }

        // Exception 의 원인을 파고 들어 stackTrace 형태로 제작하는 로직
        Throwable cause = ex.getCause();
        while (cause != null) {
            stackTraceString.append(cause).append("\t");
            cause = cause.getCause();
        }

        // null 체크 로직 추가
        if (attribute != null) {
            stackTraceString.append("{").append(attribute).append("}");
        } else {
            stackTraceString.append("{null}");
        }

        // getCause() null 체크
        String causeMessage = (ex.getCause() != null) ? ex.getCause().toString() : "No cause available";

        // apmErrorPoint null 체크
        String errorPoint = (apmErrorPoint != null) ? apmErrorPoint : "unknown";

        return logTemplate(causeMessage, stackTraceString.toString(), CompressUtil.hash(stackTraceString.toString()), errorPoint);
    }

    /**
     * loki log enabled == false 이거나 isConnectionReset == true 의 경우 null String return
     * 최하위
     * 클라이언트가 갑자기 연결을 종료한 경우
     * 서버나 프록시가 세션을 강제로 종료한 경우
     * 로드밸런서나 방화벽이 세션을 정리할 때
     * 네트워크 장애나 패킷 손실이 발생한 경우
     * 등 여러 가지 이유로 "Connection reset by peer" 예외 발생 가능
     * @param apmCheckError String
     * @param apmStackTrace String
     * @param apmKey String
     * @param apmErrorPoint String
     * @return String
     */
    // 로그 포멧 Base64로 인코딩
    public static String logTemplate(String apmCheckError, String apmStackTrace, final String apmKey, String apmErrorPoint) {

        if(!LOKI_LOG_ENABLED) return "";

        //커넥션관련된 에러를 찍지 않음
        if(isConnectionReset(apmCheckError)) {
//            log.error("[apm_check_error]:{};[apm_error_point]:{};", replaceLineBreak(apmCheckError), replaceLineBreak(apmErrorPoint));
            return "";
        }

        byte[] bytes = String.format("[apm_check_error]:%s;[apm_stack_trace]:%s;[apm_key]:%s;[apm_error_point]:%s;", replaceLineBreak(apmCheckError), replaceLineBreak(apmStackTrace), apmKey, replaceLineBreak(apmErrorPoint))
                .getBytes(StandardCharsets.UTF_16);
        return String.format("[LOG_FOR_APM]:%s",  Base64.encodeBase64String(bytes));
    }

    private static boolean isConnectionReset(String msg) {
        return msg != null &&
                (msg.contains("Connection reset by peer") ||
                        msg.contains("Broken pipe") ||
                        msg.contains("An existing connection was forcibly closed"));
    }

    // ApiAuthCheckAspect 전용 로그 템플릿
    public static String logTemplateForApp(String apmCheckError, String apmStackTrace, final String apmKey, String apmErrorPoint) {
//        String str = String.format("[apm_check_name]:%s;[apm_check_trace]:%s;[apm_check_hash]:%s;[apm_check_point]:%s;", replaceLineBreak(apmCheckError), replaceLineBreak(apmStackTrace), apmKey, replaceLineBreak(apmErrorPoint));
//        return String.format("[LOG_FOR_APP]:%s", replaceLineBreak(apmCheckError));
        return replaceLineBreak(apmCheckError);
    }

    private static String replaceLineBreak(String str) {
        if(StringUtils.isNotEmpty(str)) {
            str = str.replaceAll("\n", "\t");
            str = str.replaceAll("\r", "");
        }
        return str;
    }
}