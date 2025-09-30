package com.visang.aidt.lms.api.board.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.board.mapper
 * fileName : TchBoardMapper
 * USER : lsm
 * date : 2025-03-25
 */
@Mapper
public interface TchBoardMapper {


    // 진행중인과제목록조회
    List<Map> selectTchBbsProgressList(Map<String, Object> param) throws Exception;

    // 게시판마스터조회
    Map<String, Object> selectTchBbsMaster(Map<String, Object> param) throws Exception;

    // 전체과제목록조회
    List<Map> selectTchBbsListAll(PagingParam<?> paramData) throws Exception;

    // 과제상세조회
    Map<String, Object> selectTchBbsDetail(Map<String, Object> param) throws Exception;

    // 과제상세조회(단건,파일포함,화면수정용)
    Map<String, Object> selectTchBbsInfTcnFileInfo(Map<String, Object> param) throws Exception;

    // 교사과제파일목록조회
    List<Map> selectTchBbsInfTcnFileList(Map<String, Object> param) throws Exception;

    // 교사과제파일목록조회
    List<Map> selectTchBbsTchFileList(Map<String, Object> param) throws Exception;

    // 다건파일다운로드
    List<Map> selectTchBbsTchFileDetailList(Map<String, Object> param) throws Exception;

    // 학생과제파일목록조회
    List<Map> selectTchBbsStntFileList(Map<String, Object> param) throws Exception;


    /* 새게시글작성진입 */
    //게시판마스터생성
    int insertTchClaBbsMasterPreEnter(Map<String, Object> paramData) throws Exception;

    //첨부파일마스터 키채번
    int insertClaAtchForKey(Map<String, Object> paramData) throws Exception;

    //첨부파일상세생성
    int insertClaAtchDetail(Map<String, Object> paramData) throws Exception;

    //첨부파일상세삭제
    int deleteClaAtchDtlFile(Map<String, Object> paramData) throws Exception;

    // 교사상세파일테이블조회
    List<Map> selectClaAtchDetail(Map<String, Object> param) throws Exception;

    // 파일단건조회
    Map<String, Object> selectBbsFileDownInfo(Map<String, Object> param) throws Exception;

    // 요청유저구분조회
    Map<String, Object> selectUserInfoBfDwn(Map<String, Object> param) throws Exception;

    // 파일다건조회
    List<Map> selectBbsFileDownList(Map<String, Object> param) throws Exception;

    //학생리스트
    List<Map> selectClsStntList(Map<String, Object> param) throws Exception;

    /* 수정화면진입 */
    List<Map> selectClsStntSltList(Map<String, Object> param) throws Exception;

    //학생삭제
    int deleteStnt(Map<String, Object> paramData) throws Exception;

    //수정된학생목록업데이트
    int insertClaBbsStntEditList(Map<String, Object> paramData) throws Exception;

    //과제종료
    int tchBoardBbsEnd(Map<String, Object> paramData) throws Exception;

    // (교사)첨부파일사용변경
    int updateClaAtchY(Map<String, Object> paramData) throws Exception;

    //첨부파일순번업데이트
    int updateClaAtchDtlFileSn(Map<String, Object> paramData) throws Exception;

    //bbs생성
    int insertTchClaBbs(Map<String, Object> paramData) throws Exception;
    //bbs업데이트
    int updateTchClaBbs(Map<String, Object> paramData) throws Exception;
    //bbs카운트업데이트
    int updateTchClaBbsCnt(Map<String, Object> paramData) throws Exception;

    //학생과제테이블조회
    List<Map> selectClaBbsStnt(Map<String, Object> param) throws Exception;

    //학생과제테이블생성
    int insertClaBbsStnt(Map<String, Object> paramData) throws Exception;
    //학생과제테이블생성리스트
    int insertClaBbsStntList(Map<String, Object> paramData) throws Exception;

    //게시판과제생성
    int insertTchClaBbsPreEnter(Map<String, Object> paramData) throws Exception;

    // 과제출제하기 (새게시물작성)
    int insertTchBbs(Map<String, Object> param) throws Exception;

    // 과제삭제 (새게시물작성)
    int delTchBoardBbs(Map<String, Object> param) throws Exception;

    Map<String, Object> selectUserInfo(Map<String, Object> param) throws Exception;

    int selectClaBbsCheck(Map<String, Object> param) throws Exception;



}
