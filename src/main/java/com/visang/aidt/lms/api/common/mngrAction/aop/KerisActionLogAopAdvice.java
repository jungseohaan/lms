package com.visang.aidt.lms.api.common.mngrAction.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.visang.aidt.lms.api.common.mngrAction.constant.KerisLogUtils;
import com.visang.aidt.lms.api.common.mngrAction.dto.LokiLogContext;
import com.visang.aidt.lms.api.common.mngrAction.dto.MngrLogContext;
import com.visang.aidt.lms.api.common.mngrAction.service.KerisLoggerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class KerisActionLogAopAdvice {

    private final KerisLoggerService kerisLoggerService;

    private final KerisLogUtils kerisLogUtils;

    @Value("${service.name}")
    private String serviceName;


    @Around("@annotation(com.visang.aidt.lms.api.common.mngrAction.aop.KerisActionLog)")
    public Object authCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = "";
        String userSeCd = "";
        String params = "";
        String resp = "";
        String url = String.valueOf(request.getRequestURI());
        Object returnData = null;
        String sTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String summary = "";
        String typeCd = "";
        String claId = "";
        String lectureCode = "";



        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            returnData = joinPoint.proceed();

            //사용자 아이디
            if (StringUtils.isEmpty(userId) && StringUtils.isNotEmpty(request.getParameter("user_id"))) {
                userId = request.getParameter("user_id");
            }
            //사용자 구분
            if (StringUtils.isEmpty(userSeCd) && StringUtils.isNotEmpty(request.getParameter("user_type"))) {
                userSeCd = request.getParameter("user_type");
            }
            //클래스 ID
            if (StringUtils.isEmpty(claId) && StringUtils.isNotEmpty(request.getParameter("cla_id"))) {
                claId = request.getParameter("cla_id");
            }

            //Custom 데이터가 있을경우
            String ctxUserId = kerisLoggerService.getUserId();
            if (StringUtils.isNotEmpty(ctxUserId)) {
                userId = ctxUserId;
            }
            String ctxUserSeCd = kerisLoggerService.getUserSeCd();
            if (StringUtils.isNotEmpty(ctxUserSeCd)) {
                userSeCd = ctxUserSeCd;
            }
            String ctxLectureCode = kerisLoggerService.getLectureCode();
            if (StringUtils.isNotEmpty(ctxLectureCode)) {
                lectureCode = ctxLectureCode;
            }
            String ctxClaId = kerisLoggerService.getClaId();
            if (StringUtils.isNotEmpty(ctxClaId)) {
                claId = ctxClaId;
            }

            if (!kerisLogUtils.getParams(request).entrySet().isEmpty()) {
                params = kerisLogUtils.getParams(request).toString();
            }

            String controllerName = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Operation operation = method.getAnnotation(Operation.class);
            if (operation != null) {
                summary = operation.summary();
            }
            typeCd = controllerName + "|" + methodName + "|" +summary;

            try {
                String jsonString = kerisLogUtils.writeValueAsString(returnData);
                resp = kerisLogUtils.processReturnData(jsonString);
            } catch (JsonProcessingException e) {
                log.warn("JSON 직렬화 실패 - Jackson 오류: {}", e.getMessage());
                resp = returnData != null ? returnData.toString() : "null";
            } catch (OutOfMemoryError e) {
                log.error("메모리 부족으로 인한 JSON 처리 실패: {}", e.getMessage());
                resp = "Memory overflow - unable to serialize response";
            } catch (Exception e) {
                log.error("JSON 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                resp = returnData != null ? returnData.toString() : "Serialization failed";
            }
        } catch (IllegalStateException e) {
            log.error("Failed to retrieve the current request: {}", e.getMessage());
        } catch (NullPointerException e) {
            log.error("NullPointerException encountered: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
        } finally {
            //Mngr 로그전송
            MngrLogContext mngrLogContext = MngrLogContext.builder()
                    .typeCd(typeCd)
                    .summary(summary)
                    .userId(userId)
                    .userSeCd(StringUtils.isEmpty(userSeCd) ? "T": userSeCd)
                    .service(serviceName)
                    .url(String.valueOf(request.getRequestURL()))
                    .ip(kerisLogUtils.getUserIp(request))
                    .host(request.getRemoteHost())
                    .req(kerisLogUtils.safeParseJson(params.toString()))
                    .resp(kerisLogUtils.safeParseJson(resp))
                    .claId(claId)
                    .lectureCode(lectureCode)
                    .build();

            if (userId.length() >= 36) {
                kerisLoggerService.logSendMngr(mngrLogContext);
            }

            //로키 로그전송
            String eTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            String hash = (resp != null && !resp.isEmpty()) ? kerisLogUtils.sha256(resp) : "";
            LokiLogContext lokiLogContext = LokiLogContext.builder()
                    .uuid(userId)
                    .uType(userSeCd)
                    .appName("keris-access")
                    .profile(kerisLogUtils.getProfileBasedLogMessage())
                    .url(url)
                    .req(kerisLogUtils.safeParseJson(params.toString()))
                    .resp(kerisLogUtils.safeParseJson(resp))
                    .sTime(sTime)
                    .eTime(eTime)
                    .hash(hash)
                    .build();

            if (userId.length() >= 36) {
                kerisLoggerService.logSendLoki(lokiLogContext);
            }

            return returnData;
        }
    }

}
