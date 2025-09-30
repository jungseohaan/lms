package com.visang.aidt.lms.api.test.service;

import com.visang.aidt.lms.api.test.mapper.PerformanceTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 성능 모니터링 테스트용 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceTestService {

    private final PerformanceTestMapper performanceTestMapper;

    /**
     * 지정된 시간만큼 지연
     */
    public void simulateDelay(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrupted during delay simulation", e);
        }
    }

    /**
     * 빠른 쿼리 실행 (1초)
     */
    public String executeFastQuery() {
        return performanceTestMapper.selectFastQuery();
    }

    /**
     * 느린 쿼리 실행 (4초)
     */
    public String executeSlowQuery() {
        return performanceTestMapper.selectSlowQuery();
    }
}