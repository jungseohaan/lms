package com.visang.aidt.lms.api.textbook.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.lecture.mapper
 * fileName : TchCrcuMapper
 * USER : kil803
 * date : 2024-01-13
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-13         kil803          최초 생성
 */
@Mapper
public interface CrcuMapper {
    List<Map<String,Object>> findTextbookCrcuList(Map<String,Object> param) throws Exception;

    /**
     * 메타(aidt_lcms.meta) 테이블에 저장되어 있는 교과과정 커리큘럼 목록 조회
     *
     * @param param
     * @return
     */
    List<Map<String,Object>> findTextbookCrcuListByMeta(Map<String,Object> param) throws Exception;

    /**
     * 교과서의 브랜드 ID 조회
     *
     * @param param
     * @return
     * @throws Exception
     */
    int findTextbookBrandId(Map<String,Object> param) throws Exception;
}
