package com.visang.aidt.lms.api.configuration;

import lombok.Getter;

/**
 * 캐시 타입 정의 ENUM 클래스
 * @see CacheConfig
 */
@Getter
public enum CacheType {

    // TODO : 캐시 시간 관련 정의 필요
    AIDT_LMS_CACHE_INTERNAL("aidt_lms_cache", 60 * 60 * 24 * 7, 1);

    private final String cacheName;
    private final int secsToExpireAfterWrite;
    private final int entryMaxSize;

    CacheType(String cacheName, int secsToExpireAfterWrite, int entryMaxSize) {
        this.cacheName = cacheName;
        this.secsToExpireAfterWrite = secsToExpireAfterWrite;
        this.entryMaxSize = entryMaxSize;
    }

}
