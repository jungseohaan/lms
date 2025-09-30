package com.visang.aidt.lms.api.operation.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.mapper
 * fileName : PopUpMapper
 * USER : leejh16
 * date : 2025-02-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-02-25      leejh16          최초 생성
 */
@Mapper
public interface PopUpMapper {

    List<Map<String, Object>> getPopUpSummary(Map<String, Object> paramMap);
}
