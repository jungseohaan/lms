package com.visang.aidt.lms.api.operation.service;

import com.visang.aidt.lms.api.operation.mapper.PopUpMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.service
 * fileName : PopUpService
 * USER : leejh16
 * date : 2025-02-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-02-25      leejh16          최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PopUpService {

    @Value("${cloud.aws.s3.url}")
    private String cloudAwsS3Url;

    private final PopUpMapper popUpMapper;

    /**
     * 팝업 노출을 위해 팝업 관리의 목록을 조회하는 메서드
     * @param paramMap 검색할 팝업 관리의 조건이 정의된 파라미터 정보
            exposTrgtCd : 교사 학생 구분 값 (T|S)
            exposPstnCd : 노출위치
                파라미터를 보내주지 않으면 1을 기본 값으로 사용합니다.
                id: 1, value: 홈
                id: 2, value: 교과서
                id: 3, value: 과제
                id: 4, value: 평가
                id: 5, value: 수업자료실
                id: 6, value: 스스로학습
     */
    public List<Map<String, Object>> getPopUpSummary(Map<String, Object> paramMap) {
        paramMap.put("cloudAwsS3Url", cloudAwsS3Url);
        return popUpMapper.getPopUpSummary(paramMap);
    }
}
