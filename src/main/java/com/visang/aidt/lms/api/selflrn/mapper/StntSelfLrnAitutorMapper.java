package com.visang.aidt.lms.api.selflrn.mapper;

import com.visang.aidt.lms.api.selflrn.dto.AitutorLrngInfoVO;
import com.visang.aidt.lms.api.selflrn.dto.AitutorQuestionVO;
import com.visang.aidt.lms.api.selflrn.dto.AitutorResultInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper
public interface StntSelfLrnAitutorMapper {
    List<Map<String, Object>> selectSlfStdInfoAitutorData(Map<String, Object> paramData) throws Exception;
    void insertSlfStdInfoForAitutor(Map<String, Object> paramData) throws Exception;
    void insertSlfStdResultInfoForAitutor(Map<String, Object> paramData) throws Exception;
    int updateFirstSlfResult(Map<String, Object> paramData) throws Exception;
    int updateAitutorLrngInfoPrgrs(Map<String, Object> paramData) throws Exception;
    String selectReceiveCompleteYn(Map<String, Object> paramData) throws Exception;
    LinkedList<Map<String, Object>> selectEnLrngDivList(Map<String, Object> paramData) throws Exception;
    List<Integer> selectAitutorLibtextIdList(@Param("stdId") Integer stdId) throws Exception;
    Map<String, Object> selectLibtextWord(@Param("unitPrefixValue") String unitPrefixValue, @Param("libtextIdList") List<Integer> libtextIdList) throws Exception;
    Map<String, Object> selectLibtextSentence(@Param("unitPrefixValue") String unitPrefixValue, @Param("libtextIdList") List<Integer> libtextIdList) throws Exception;
    List<String> selectAitutorArticleIdList(@Param("stdId") Integer stdId) throws Exception;
    String selectArticleByDivId(@Param("enLrngDivId") Integer enLrngDivId, @Param("unitPrefixValue") String unitPrefixValue, @Param("articleIdList") List<String> articleIdList) throws Exception;
    List<AitutorLrngInfoVO> selectAitutorLrngInfoByStdId(@Param("stdId") Integer stdId) throws Exception;
    int updateAitutorLrngInfo(AitutorLrngInfoVO paramData) throws Exception;
    int updateAitutorLrngSttsCd(@Param("aitutorLrngInfoId") Integer aitutorLrngInfoId, @Param("lrngSttsCd") Integer lrngSttsCd) throws Exception;

    String selectSlfStdAitutorLastYmd(@Param("stdId") Integer stdId) throws Exception;
    void insertSlfStdAitutorLrngInfo(Map<String, Object> paramData) throws Exception;
    void insertSlfStdAitutorLrngDetail(AitutorQuestionVO paramData) throws Exception;
    int deleteSlfStdAitutorLrngDetailInit(@Param("aitutorLrngInfoId") Integer aitutorLrngInfoId) throws Exception;

    LinkedList<Map<String, Object>> selectAitutorLrngListByStdId(@Param("stdId") Integer stdId, @Param("lowRankUdstdRateAt") String lowRankUdstdRateAt) throws Exception;
    int selectAitutorDivIdByArticle(@Param("stdId") Integer stdId, @Param("articleId") Integer articleId) throws Exception;
    List<Integer> selectArticleChainTypeYn(@Param("articleId") Integer articleId) throws Exception;
    int updateAitutorSubmitAnswer(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStdInfoById(@Param("stdId") Integer stdId) throws Exception;
    AitutorResultInfoVO selectStdResultInfoByStdId(@Param("stdId") Integer stdId) throws Exception;
    Map<String, Object> selectStdResultInfoById(@Param("stdResultId") Integer stdResultId) throws Exception;
    int updateAitutorSubmitChat(Map<String, Object> paramData) throws Exception;
    List<String> selectTaskAitutorExistDateList(Map<String, Object> paramData) throws Exception;
    List<String> selectSlfStdAitutorExistDateList(Map<String, Object> paramData) throws Exception;

    int updateAitutorLrngInfoInitByStdId(@Param("stdId") Integer stdId) throws Exception;
    int deleteSlfStdAitutorLrngDetailInitByStdId(@Param("stdId") Integer stdId) throws Exception;
    int selectEnLrngDivIdByStdId(@Param("stdId") Integer stdId) throws Exception;
    Map<String, Object> selectCurAitutorLrngInfoByStdId(Map<String, Object> paramData) throws Exception;
    int selectAitutorLrngDetailCnt(Map<String, Object> paramData) throws  Exception;
    int updateLibtextId(@Param("stdResultId") Integer stdResultId, @Param("libtextId") Integer libtextId) throws Exception;
}