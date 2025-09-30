package com.visang.aidt.lms.api.common.excel.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
@Mapper
public interface ExcelDownloadMapper {
    List<Map<String, Object>> findUnits(Integer textbookId);
    List<Map<String, Object>> findTaskReviewsByClaIdAndMetaIds(Map<String, Object> paramData);
    List<Map<String, Object>> findEvlReviewsByClaIdAndMetaIds(Map<String, Object> paramData);
    List<Map<String, Object>> findMathReviewsByClaIdAndMetaIds(Map<String, Object> paramData);
    List<Map<String, Object>> findEnglishReviewsByClaIdAndMetaIds(Map<String, Object> paramData);
    List<Map<String, Object>> findEnglishAchievementByTextbkIdAndClaId(Map<String, Object> paramData);
    List<Map<String, Object>> findMathAchievementByTextbkIdAndClaId(Map<String, Object> paramData);
}
