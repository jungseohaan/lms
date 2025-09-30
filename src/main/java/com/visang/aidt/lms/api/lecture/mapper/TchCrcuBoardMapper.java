package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchCrcuBoardMapper {
    Map<String, Object> findTcOpnnBrd(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTcWhtBrd(Map<String, Object> paramData) throws Exception;

    int modifyTchToolBoardSave(Map<String, Object> paramData) throws Exception;
    int createTchToolBoardSave(Map<String, Object> paramData) throws Exception;

    int modifyTchToolWhiteboardSave(Map<String, Object> paramData) throws Exception;
    int createTchToolWhiteboardSave(Map<String, Object> paramData) throws Exception;

    List<Map> findTchToolWhiteboardList(Map<String, Object> paramData) throws Exception;

    int modifyTchToolWhiteboardModify(Map<String, Object> paramData) throws Exception;

    int removeTchToolWhiteboardDel(Map<String, Object> paramData) throws Exception;

}
