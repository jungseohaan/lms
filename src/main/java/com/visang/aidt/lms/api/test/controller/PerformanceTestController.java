package com.visang.aidt.lms.api.test.controller;

import com.visang.aidt.lms.api.test.service.PerformanceTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 성능 모니터링 테스트용 컨트롤러
 * - API 실행시간 테스트
 * - MyBatis 쿼리 실행시간 테스트
 */
@Tag(name = "PerformanceTest", description = "성능 모니터링 테스트 API")
@RestController
@RequestMapping("/test/perf")
@RequiredArgsConstructor
@Slf4j
public class PerformanceTestController {

    private final PerformanceTestService performanceTestService;

    @Operation(summary = "빠른 API 테스트", description = "1초 지연 후 응답하는 API (임계값 미만)")
    @GetMapping("/api/fast")
    public Map<String, Object> fastApi() {
        log.info("Fast API 호출됨");
        
        // 1초 지연
        performanceTestService.simulateDelay(1000);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Fast API 호출 완료");
        result.put("delay", "1000ms");
        result.put("expected", "DEBUG 레벨 로그 출력");
        return result;
    }

    @Operation(summary = "느린 API 테스트", description = "5초 지연 후 응답하는 API (임계값 초과)")
    @GetMapping("/api/slow")
    public Map<String, Object> slowApi() {
        log.info("Slow API 호출됨");
        
        // 5초 지연
        performanceTestService.simulateDelay(5000);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Slow API 호출 완료");
        result.put("delay", "5000ms");
        result.put("expected", "WARN 레벨 로그 출력 ([SLOW-API])");
        return result;
    }

    @Operation(summary = "사용자 정의 지연 API", description = "지정된 시간만큼 지연 후 응답")
    @GetMapping("/api/custom")
    public Map<String, Object> customDelayApi(@RequestParam(defaultValue = "2000") long delayMs) {
        log.info("Custom delay API 호출됨 - {}ms", delayMs);
        
        performanceTestService.simulateDelay(delayMs);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Custom delay API 호출 완료");
        result.put("delay", delayMs + "ms");
        result.put("expected", delayMs > 3000 ? "WARN 레벨 로그" : "DEBUG 레벨 로그");
        return result;
    }

    @Operation(summary = "빠른 쿼리 테스트", description = "빠른 쿼리 실행 테스트 (1초)")
    @GetMapping("/query/fast")
    public Map<String, Object> fastQuery() {
        log.info("Fast Query 테스트 호출됨");
        
        String result = performanceTestService.executeFastQuery();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Fast Query 실행 완료");
        response.put("result", result);
        response.put("expected", "DEBUG 레벨 로그 출력");
        return response;
    }

    @Operation(summary = "느린 쿼리 테스트", description = "느린 쿼리 실행 테스트 (4초)")
    @GetMapping("/query/slow")
    public Map<String, Object> slowQuery() {
        log.info("Slow Query 테스트 호출됨");
        
        String result = performanceTestService.executeSlowQuery();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Slow Query 실행 완료");
        response.put("result", result);
        response.put("expected", "WARN 레벨 로그 출력 ([SLOW-QUERY], [SLOW-SQL])");
        return response;
    }

    @Operation(summary = "복합 테스트", description = "API + 쿼리 조합 테스트")
    @GetMapping("/combined")
    public Map<String, Object> combinedTest(@RequestParam(defaultValue = "2000") long apiDelay,
                                           @RequestParam(defaultValue = "fast") String queryType) {
        log.info("Combined 테스트 호출됨 - API delay: {}ms, Query type: {}", apiDelay, queryType);
        
        // API 지연
        performanceTestService.simulateDelay(apiDelay);
        
        // 쿼리 실행
        String queryResult = "fast".equals(queryType) ? 
            performanceTestService.executeFastQuery() : 
            performanceTestService.executeSlowQuery();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Combined 테스트 완료");
        result.put("apiDelay", apiDelay + "ms");
        result.put("queryType", queryType);
        result.put("queryResult", queryResult);
        result.put("expected", "API와 쿼리 각각의 성능 로그 출력");
        return result;
    }

    @Operation(summary = "에러 테스트", description = "의도적 에러 발생으로 에러 로깅 테스트")
    @GetMapping("/error")
    public Map<String, Object> errorTest() {
        log.info("Error 테스트 호출됨");
        
        // 2초 지연 후 에러 발생
        performanceTestService.simulateDelay(2000);
        
        throw new RuntimeException("의도적 에러 발생 - 성능 모니터링 테스트용");
    }
}