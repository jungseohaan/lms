package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.materials.mapper
 * fileName : StntMdulQuestMapper
 * USER : hs84
 * date : 2024-01-23
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-23         hs84          최초 생성
 */
@Mapper
public interface StntMdulQuestMapper {
    // /stnt/mdul/quest/list
    List<Map> findStntMdulQuestList(Map<String, Object> paramData) throws Exception;

    // /stnt/mdul/quest
    int createStntMdulQeust(Map<String, Object> paramData) throws Exception;

    // /stnc/mdul/quest/comment
    int createStntMdulQeustComment(Map<String, Object> paramData) throws Exception;

    int modifyStntMdulQuestReadall(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQuestCall(Map<String, Object> paramData) throws Exception;

    int modifyTchMdulQuestReadall(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchMdulQuestCall(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findQestnInfoById(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findUserById(Map<String, Object> paramData) throws Exception;

}
