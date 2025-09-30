package com.visang.aidt.lms.api.mathexplore.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface MathExploreMapper {
    List<Map<String, Object>> selectClaRankList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectGameRankList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findByUserId(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findClaMyRank(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findGameMyRank(Map<String, Object> paramData) throws Exception;
    List<Map> findClaList() throws Exception;

    int insertScr(Map<String, Object> paramData) throws Exception;

    int updateBestScr(Map<String, Object> paramData) throws Exception;

    int deleteAll() throws Exception;

}
