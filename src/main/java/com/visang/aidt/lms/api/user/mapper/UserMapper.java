package com.visang.aidt.lms.api.user.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.user.mapper
 * fileName : UserMapper
 * USER : kil803
 * date : 2024-01-15
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-15         kil803          최초 생성
 */
@Mapper
public interface UserMapper {

    // user_mst 테이블 정보 조회
    Map<String,Object> findByUserId(Map<String,Object> param) throws Exception;

    // aidt_lms.`user` 테이블 정보 조회
    Map<String,Object> findUserInfoByUserId(String userId) throws Exception;

    // 해당 학급에 속한 학생 목록 조회
    List<Map<String,Object>> findStdtListByClass(Map<String,Object> param) throws Exception;

    // 해당 학급에 속한 학생 정보 조회
    Map<String,Object> findStdtInfo(Map<String,Object> param) throws Exception;

    // 교사(ID) 학급 정보 조회
    Map<String,Object> findClassInfo(Map<String,Object> param) throws Exception;

    // 콘텐츠 오류 신고 등록
    int saveUserClauseagre(Map<String, Object> paramData) throws Exception;

    // 교사) 학생 개인정보 수집/이용동의 여부 조회
    Map<String, Object> findUserClauseAgre(Map<String, Object> param) throws Exception;

    // 교사) 학생 개인정보 수집/이용동의 여부 수정/등록
    int updateUserContsErrDclr(Map<String, Object> param) throws Exception;

    int updateLgnSttsAt(Map<String, Object> param) throws Exception;

    // 모든 유저 로그아웃 처리
    int updateLgnSttsAtAll(Map<String, Object> param) throws Exception;

    List<Map> findUserAccesslogList(PagingParam<?> pagingParam) throws Exception;

    // 학생이 속한 모든 학급 목록 조회 (동일 교과서 기준)
    List<Map<String,Object>> findStudentClassList(Map<String,Object> param) throws Exception;

    // 학생이 속했던 모든 학급 정보
    List<Map> findStudentClassInfoList(Map<String,Object> param) throws Exception;

    // 교사가 속했던 모든 학급 정보
    List<Map> findTchClassInfoList(Map<String,Object> param) throws Exception;
}
