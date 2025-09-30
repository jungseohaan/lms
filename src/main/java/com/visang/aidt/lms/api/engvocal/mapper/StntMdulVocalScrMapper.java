package com.visang.aidt.lms.api.engvocal.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.engvocal.mapper
 * fileName : StntMdulVocalScrMapper
 * USER : kil803
 * date : 2024-06-19
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-06-19         kil803          최초 생성
 */
@Mapper
public interface StntMdulVocalScrMapper {
    /**
     * [영어] 발성평가 점수 정보 등록
     *
     * @param param
     * @return
     * @throws Exception
     */
    int createVocalEvlScrInfo(Map<String,Object> param) throws Exception;

    /**
     * [영어] 발성평가 점수 정보 삭제
     *
     * @param param
     * @return
     * @throws Exception
     */
    int removeVocalEvlScrInfo(Map<String,Object> param) throws Exception;

    int createVocalEvlScrDetail(Map<String,Object> param) throws Exception;
    int createVocalEvlScrColor(Map<String,Object> param) throws Exception;
    int createVocalEvlPhoneLevel(Map<String,Object> param) throws Exception;
}
