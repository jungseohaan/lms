package com.visang.aidt.lms.api.common.interceptor;

import com.visang.aidt.lms.api.utility.utils.NatsLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * MyBatis 쿼리 성능 모니터링 Interceptor
 * - SQL 쿼리 실행 시간 측정
 * - 임계값 초과 시 상세 로그 출력
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
@Slf4j
public class SlowQueryInterceptor implements Interceptor {

    @Value("${performance.monitoring.sql.threshold:3000}")
    private long sqlThresholdMs;

    @Value("${performance.monitoring.enabled:true}")
    private boolean sqlMonitoringEnabled;

    private final NatsLogUtil natsLogUtil;

    public SlowQueryInterceptor(@Lazy NatsLogUtil natsLogUtil) {
        this.natsLogUtil = natsLogUtil;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!sqlMonitoringEnabled) {
            return invocation.proceed();
        }

        long startTime = System.currentTimeMillis();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        String sqlId = mappedStatement.getId();
        String sqlType = mappedStatement.getSqlCommandType().name();

        Object result = invocation.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > sqlThresholdMs) {
            String sql = getSqlStatement(mappedStatement, parameter);
            log.error("[ERRDLAY002] {} took {}ms\n - SQL ID: {}\n - Parameters: {}\n - Sql: {}",
                    sqlType,
                    executionTime,
                    sqlId,
                    getParameterString(parameter),
                    sql
            );
            
            // NATS 로그 전송 (실패해도 애플리케이션 동작에 영향 없도록 처리)
            try {
                natsLogUtil.sendSlowQueryLog(sqlId, sqlType, executionTime, parameter, sql);
            } catch (RuntimeException natsException) {
                log.debug("NATS slow query log transmission failed: {}", natsException.getMessage());
            } catch (Exception natsException) {
                log.debug("NATS slow query log transmission failed: {}", natsException.getMessage());
            }
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 추가 설정이 필요한 경우 사용
    }

    /**
     * SQL 문장 추출 (간략화)
     */
    private String getSqlStatement(MappedStatement mappedStatement, Object parameter) {
        try {
            String sql = mappedStatement.getBoundSql(parameter).getSql();
            // SQL을 한 줄로 정리하고 길면 자르기
            sql = sql.replaceAll("\\s+", " ").trim();
            if (sql.length() > 200) {
                sql = sql.substring(0, 200) + "...";
            }
            return sql;
        } catch (NullPointerException e) {
            log.debug("SQL extraction failed - null pointer: {}", e.getMessage());
            return "SQL extraction failed";
        } catch (IllegalArgumentException e) {
            log.debug("SQL extraction failed - invalid argument: {}", e.getMessage());
            return "SQL extraction failed";
        } catch (RuntimeException e) {
            log.debug("SQL extraction failed - runtime error: {}", e.getMessage());
            return "SQL extraction failed";
        } catch (Exception e) {
            log.debug("SQL extraction failed - unexpected error: {}", e.getMessage());
            return "SQL extraction failed";
        }
    }

    /**
     * 파라미터 문자열 생성
     */
    private String getParameterString(Object parameter) {
        if (parameter == null) {
            return "null";
        }

        String paramStr = parameter.toString();
        if (paramStr.length() > 500) {
            paramStr = paramStr.substring(0, 500) + "...";
        }
        return paramStr;
    }
}