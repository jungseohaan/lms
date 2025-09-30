package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.lecture.mapper
 * fileName : StntCrcuMapper
 * date : 2024-05-08
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 */
@Mapper
public interface StntCrcuMapper {
    Map<String,Object> selectStntCrcuLastposition(Map<String,Object> param) throws Exception;

    int createStntCrcuLastposition(Map<String, Object> paramData) throws Exception;
    int updateStntCrcuLastposition(Map<String, Object> paramData) throws Exception;

}
