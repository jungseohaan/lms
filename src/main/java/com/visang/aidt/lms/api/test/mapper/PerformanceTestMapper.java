package com.visang.aidt.lms.api.test.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 성능 모니터링 테스트용 매퍼
 */
@Mapper
public interface PerformanceTestMapper {

    /**
     * 빠른 쿼리 실행 (1초 지연)
     */
    String selectFastQuery();

    /**
     * 느린 쿼리 실행 (4초 지연)
     */
    String selectSlowQuery();
}