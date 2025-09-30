package com.visang.aidt.lms.api.board.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.board.mapper
 * fileName : StntBoardMapper
 * USER : lsm
 * date : 2025-03-25
 */
@Mapper
public interface StntBoardMapper {

    // 진행중인과제목록조회
    List<Map> selectStntBbsProgressList(Map<String, Object> param) throws Exception;

    // 전체과제목록조회
    List<Map> selectStntBbsListAll(PagingParam<?> paramData) throws Exception;


    // 학생과제파일목록조회
    Map<String, Object>  selectStntBbsStntFileInfo(Map<String, Object> param) throws Exception;
    List<Map> selectStntBbsStntFileList(Map<String, Object> param) throws Exception;


    /* 수정화면진입 */
    List<Map> selectClsStntSltList(Map<String, Object> param) throws Exception;

    //과제종료
    int stntBoardBbsEnd(Map<String, Object> paramData) throws Exception;


}
