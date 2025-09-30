package com.visang.aidt.lms.api.mathexplore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.mathexplore.mapper.MathExploreMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class MathExploreService {

    private final ObjectMapper mapper;

    private final MathExploreMapper mathExploreMapper;

    /**
     * 학급별 랭킹 조회
     *
     * @param paramData 입력 파라미터
     * @return Map
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object selectClaRankList(Map<String, Object> paramData) throws Exception {
        Integer cntInt = Integer.parseInt((String) paramData.get("cnt"));
        paramData.put("cnt", cntInt);

        List<Map<String, Object>> rankList = mathExploreMapper.selectClaRankList(paramData);
        if(rankList == null) {return new ArrayList<>();}

        return rankList;
    }

    /**
     * 게임별 랭킹 조회
     *
     * @param paramData 입력 파라미터
     * @return Map
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object selectGameRankList(Map<String, Object> paramData) throws Exception {
        Integer cntInt = Integer.parseInt((String) paramData.get("cnt"));
        paramData.put("cnt", cntInt);

        List<Map<String, Object>> rankList = mathExploreMapper.selectGameRankList(paramData);
        if(rankList == null) {return new ArrayList<>();}

        return rankList;
    }

    /**
     * 점수 저장
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    public Object saveScr(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        int result = 0;
        result = mathExploreMapper.insertScr(paramData);


        if (result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * 학급별 나의 최고 기록, 랭킹 반환
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object findClaMyBestScr(Map<String, Object> paramData) throws Exception {
        Integer cntInt = Integer.parseInt((String) paramData.get("cnt"));
        paramData.put("cnt", cntInt);
        paramData.put("total", MapUtils.getInteger(paramData,"cnt") + 1);
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> scrMap = mathExploreMapper.findByUserId(paramData);
        Map<String, Object> rankMap = mathExploreMapper.findClaMyRank(paramData);

        returnMap.put("bestScr", MapUtils.getInteger(scrMap, "hgScr", 0));
        returnMap.put("gameId", MapUtils.getString(scrMap, "gameId", ""));
        returnMap.put("rankNum", MapUtils.getInteger(rankMap, "rankNum", 0));

        paramData.remove("total", MapUtils.getInteger(paramData,"cnt") + 1);

        return returnMap;
    }

    /**
     * 게임별 나의 최고 기록, 랭킹 반환
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object findGameMyBestScr(Map<String, Object> paramData) throws Exception {
        Integer cntInt = Integer.parseInt((String) paramData.get("cnt"));
        paramData.put("cnt", cntInt);
        paramData.put("total", MapUtils.getInteger(paramData,"cnt") + 1);
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> scrMap = mathExploreMapper.findByUserId(paramData);
        Map<String, Object> rankMap = mathExploreMapper.findGameMyRank(paramData);

        returnMap.put("bestScr", MapUtils.getInteger(scrMap, "hgScr", 0));
        returnMap.put("rankNum", MapUtils.getInteger(rankMap, "rankNum", 0));

        paramData.remove("total", MapUtils.getInteger(paramData,"cnt") + 1);

        return returnMap;
    }

}
