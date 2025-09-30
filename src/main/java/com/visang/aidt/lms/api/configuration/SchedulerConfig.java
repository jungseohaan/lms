package com.visang.aidt.lms.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 스케줄러 전용 스레드 풀 설정 클래스
 *
 * 기존 단일 스레드 풀로 인한 병목 현상을 해결하기 위해
 * 작업 성격에 따라 스레드 풀을 분리하여 독립적인 실행을 보장합니다.
 */
@Configuration
@EnableAsync
public class SchedulerConfig {

    /**
     * 평가 상태 변경 작업 전용 스레드 풀 생성
     *
     * executeTasksInProgress, executeTasksToComplete 메서드에서 사용
     * 빠른 DB 업데이트 작업들로 구성되어 있어 상대적으로 처리 시간이 짧음
     *
     * @return TaskScheduler 상태 변경 작업용 스케줄러
     */
    @Bean(name = "statusChangeExecutor")
    public TaskScheduler statusChangeTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 스레드 풀 크기: 2개
        // - executeTasksInProgress (진행중 상태 변경)
        // - executeTasksToComplete (완료 상태 변경)
        // 두 작업이 동시에 실행되어도 서로 영향을 주지 않도록 설정
        scheduler.setPoolSize(2);

        // 스레드 이름 접두사 설정 (로그 추적 및 디버깅 용이)
        scheduler.setThreadNamePrefix("evl-status-change-");

        // 애플리케이션 종료 시 진행 중인 작업 완료 대기
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        // 종료 대기 시간: 30초
        // 상태 변경 작업은 비교적 빠르므로 짧은 대기 시간 설정
        scheduler.setAwaitTerminationSeconds(30);

        // 스케줄러 초기화
        scheduler.initialize();

        return scheduler;
    }

    /**
     * 처방 과제 출제 작업 전용 스레드 풀 생성
     *
     * executePrescriptionTasks 메서드에서 사용
     * AI 처방 과제 생성 등 복잡한 비즈니스 로직으로 인해 처리 시간이 오래 걸릴 수 있음
     * 상태 변경 작업과 분리하여 병목 현상 방지
     *
     * @return TaskScheduler 처방 과제 출제용 스케줄러
     */
    @Bean(name = "prescriptionExecutor")
    public TaskScheduler prescriptionTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 스레드 풀 크기: 1개
        // 처방 과제 출제는 순차적으로 처리하여 시스템 부하 조절
        // 대량의 데이터 처리 시 메모리 및 CPU 사용량 제어
        scheduler.setPoolSize(1);

        // 스레드 이름 접두사 설정 (로그 추적 및 디버깅 용이)
        scheduler.setThreadNamePrefix("prescription-");

        // 애플리케이션 종료 시 진행 중인 작업 완료 대기
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        // 종료 대기 시간: 600초
        // 처방 과제 출제는 시간이 오래 걸릴 수 있으므로 여유있는 대기 시간 설정
        scheduler.setAwaitTerminationSeconds(600);

        // 스케줄러 초기화
        scheduler.initialize();

        return scheduler;
    }
}