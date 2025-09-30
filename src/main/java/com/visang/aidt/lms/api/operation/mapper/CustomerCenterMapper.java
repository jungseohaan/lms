package com.visang.aidt.lms.api.operation.mapper;

import com.visang.aidt.lms.api.operation.dto.FaqInfoDto;
import com.visang.aidt.lms.api.operation.dto.NoticeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.mapper
 * fileName : PopUpMapper
 * USER : shinhc1
 * date : 2025-07-03
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-07-03      shinhc1          최초 생성
 */
@Mapper
public interface CustomerCenterMapper {

    List<NoticeInfoDto> getCustomerNotice(Map<String, Object> paramMap);

    NoticeInfoDto getCustomerNoticeDtl(Map<String, Object> paramMap);

    List<Map<String,Object>> getCustomerNoticeFile(Map<String, Object> paramMap);

    int getCustomerNoticeCnt (Map<String, Object> paramMap);

    List<FaqInfoDto> getCustomerFaq(Map<String, Object> paramMap);

    FaqInfoDto getCustomerFaqDtl(Map<String, Object> paramMap);

    List<Map<String,Object>> getCustomerFaqFile(Map<String, Object> paramMap);

    List<Map<String,Object>> getCustomerCodeList(Map<String, Object> paramMap);

    int getCustomerFaqCnt (Map<String, Object> paramMap);

}
