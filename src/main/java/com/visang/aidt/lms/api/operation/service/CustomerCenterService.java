package com.visang.aidt.lms.api.operation.service;

import com.visang.aidt.lms.api.operation.dto.NoticeInfoDto;
import com.visang.aidt.lms.api.operation.mapper.CustomerCenterMapper;
import com.visang.aidt.lms.api.operation.mapper.PopUpMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.service
 * fileName : CustomerCenterService
 * USER : shinhc1
 * date : 2025-02-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-07-03      shinhc1          최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCenterService {

    @Value("${cloud.aws.s3.url}")
    private String cloudAwsS3Url;

    private final CustomerCenterMapper customerCenterMapper;

    /**
     * 공지사항 관리의 목록을 조회하는 메서드
     * @param paramMap 검색할 관리의 조건이 정의된 파라미터 정보
    exposTrgtCd : 교사 학생 구분 값 (T|S)
    schoolLevelCd : 학교급
    100: 초등,
    200: 중등,
    300: 고등
     */
    public Map<String, Object> getCustomerNotice(Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();
        int page = Integer.parseInt(paramMap.get("page").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        int offset = pageSize * (page - 1);
        paramMap.put("offset",offset);
        paramMap.put("pageSize", pageSize);

        // 전체 개수 먼저 가져옴
        int totalCnt = customerCenterMapper.getCustomerNoticeCnt(paramMap);
        List<NoticeInfoDto> noticeList = customerCenterMapper.getCustomerNotice(paramMap);

        // rownum 부여
        for (int i = 0; i < noticeList.size(); i++) {
            int rownum = totalCnt - (offset + i);  // 전체 기준 역순 rownum 계산
            noticeList.get(i).setRownum(rownum);
        }

        result.put("noticeInfoList", noticeList);
        result.put("noticeTotalCnt", totalCnt);

//        result.put("noticeInfoList",customerCenterMapper.getCustomerNotice(paramMap));
//        result.put("noticeTotalCnt",customerCenterMapper.getCustomerNoticeCnt(paramMap));

        return result;
    }


    /**
     * 공지사항 관리의 목록을 조회하는 메서드
     * @param paramMap 검색할 관리의 조건이 정의된 파라미터 정보
    noticeId : 공지사항아이디
     */
    public Map<String, Object> getCustomerNoticeDtl(Map<String, Object> paramMap) {

        Map<String, Object> result = new HashMap<>();

        result.put("noticeInfo",customerCenterMapper.getCustomerNoticeDtl(paramMap));
        paramMap.put("cloudAwsS3Url", cloudAwsS3Url);
        result.put("noticeFile",customerCenterMapper.getCustomerNoticeFile(paramMap));
        return result;
    }

    /**
     * 자주하는질문 관리의 목록을 조회하는 메서드
     * @param paramMap 검색할 관리의 조건이 정의된 파라미터 정보
    exposTrgtCd : 교사 학생 구분 값 (T|S)
    schoolLevelCd : 학교급
    100: 초등,
    200: 중등,
    300: 고등
     */
    public Map<String, Object> getCustomerFaq(Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();
        int page = Integer.parseInt(paramMap.get("page").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        int offset = pageSize * (page - 1);
        paramMap.put("offset",offset);
        paramMap.put("pageSize", pageSize);
        result.put("faqInfoList",customerCenterMapper.getCustomerFaq(paramMap));
        result.put("faqTotalCnt",customerCenterMapper.getCustomerFaqCnt(paramMap));

        return result;
    }

    /**
     * 자주하는질문 관리의 목록을 조회하는 메서드
     * @param paramMap 검색할 관리의 조건이 정의된 파라미터 정보
    noticeId : 자주하는질문 아이디
     */
    public Map<String, Object> getCustomerFaqDtl(Map<String, Object> paramMap) {

        Map<String, Object> result = new HashMap<>();

        result.put("faqInfo",customerCenterMapper.getCustomerFaqDtl(paramMap));
        paramMap.put("cloudAwsS3Url", cloudAwsS3Url);
        result.put("faqFile",customerCenterMapper.getCustomerFaqFile(paramMap));
        return result;
    }

    /**
     * 자주하는질문 관리의 메뉴 목록을 조회하는 메서드
     * @param paramMap 검색할 관리의 조건이 정의된 파라미터 정보
    noticeId : 자주하는질문 아이디
     */
    public Map<String, Object> getCustomerCodeList(Map<String, Object> paramMap) {

        Map<String, Object> result = new HashMap<>();
        paramMap.put("codeGbCd","cs_faq_category");

        result.put("codeList",customerCenterMapper.getCustomerCodeList(paramMap));
        return result;
    }

}

