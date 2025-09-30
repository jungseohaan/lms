package com.visang.aidt.lms.api.user.service;

import com.visang.aidt.lms.api.user.mapper.StntRewardMapper;
import com.visang.aidt.lms.api.user.mapper.TchRewardMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.visang.aidt.lms.api.user.constants.RewardConstants;
import com.visang.aidt.lms.api.user.constants.RewardConstants.RewardAdjustType;
import com.visang.aidt.lms.api.user.constants.RewardConstants.RewardSeCode;
import com.visang.aidt.lms.api.user.constants.RewardConstants.MenuSeCode;
import com.visang.aidt.lms.api.user.constants.RewardConstants.SveSeCode;

@Service
@AllArgsConstructor
public class TchRewardService {

    private final TchRewardMapper tchRewardMapper;
    private final StntRewardMapper stntRewardMapper;

    /**
     * (리워드).리워드 지급 현황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findStntRewardStatus(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rewardStatusInfo = tchRewardMapper.findStntRewardStatus(paramData);
        return rewardStatusInfo;
    }

    /**
     * (리워드).리워드 지급
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> createReward(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    @Transactional(readOnly = true)
    public Object findStntsrchRewardList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>>stntRwdInfo = tchRewardMapper.findStntRwdInfo(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("stntRwdList", stntRwdInfo);

        // 리스트가 null이거나 비어 있는 경우 처리
        if (stntRwdInfo == null || stntRwdInfo.isEmpty()) {
            returnMap.put("stntCnt", 0);  // 또는 null
        } else {
            returnMap.put("stntCnt", stntRwdInfo.get(0).get("stntCnt"));
        }

        return returnMap;
    }

    /** 학생 리워드 조정 */
    @Transactional
    public Map<String, Object> adjustReward(Map<String, Object> paramData) throws Exception {
        if (paramData.get("rwdAdjType") == null) {
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("success", false);
            resultData.put("message", "리워드 조정 유형(rwdAdjType)을 확인해주세요.");
            return resultData;
        }

        String userId = (String) paramData.get("userId");
        String claId = (String) paramData.get("claId");
        Integer rwdAdjType = Integer.parseInt(paramData.get("rwdAdjType").toString());

        Map<String, Object> resultData = new HashMap<>();

        // 리워드 히스토리 파라미터 생성
        Map<String, Object> hist_param = new HashMap<>();
        hist_param.put("userId", userId);
        hist_param.put("claId", claId);

        if(rwdAdjType.equals(RewardAdjustType.EARN.getCode())){
            hist_param.put("seCd", RewardSeCode.EARN.getCode()); // 획득
            hist_param.put("rwdAmt", 1);
            hist_param.put("rwdUseAmt", 0);
        } else if(rwdAdjType.equals(RewardAdjustType.DEDUCT.getCode())){
            hist_param.put("seCd", RewardSeCode.DEDUCT.getCode()); // 차감
            hist_param.put("rwdAmt", 0);
            hist_param.put("rwdUseAmt", 1);
        }

        hist_param.put("menuSeCd", MenuSeCode.TEACHER.getCode());
        hist_param.put("sveSeCd", SveSeCode.REWARD_ADJUST.getCode()); // 리워드 조정
        hist_param.put("trgtId", 0);
        hist_param.put("rwdSeCd", RewardSeCode.HEART.getCode());

        // 1. 학생 리워드 정보 조회
        Map<String, Object> rwdInfo = stntRewardMapper.selectRwdEarnInfo(hist_param);

        // 2. 리워드 잔액 확인 (차감인 경우)
        if(rwdAdjType.equals(RewardAdjustType.DEDUCT.getCode()) && (rwdInfo == null || (int)rwdInfo.get("htBlnc") <= 0)) {
            resultData.put("success", false);
            resultData.put("message", "하트 잔액이 부족합니다.");
            return resultData;
        }

        // 3. 리워드 히스토리 저장
        stntRewardMapper.insertRwdEarnHist(hist_param);

        // 4. 리워드 정보 업데이트
        Map<String, Object> update_param = new HashMap<>();
        update_param.put("userId", userId);
        update_param.put("claId", claId);

        // INSERT ON DUPLICATE KEY UPDATE를 활용하여 데이터 처리
        if(rwdInfo == null) {
            // 신규 데이터 생성
            if(rwdAdjType.equals(RewardAdjustType.EARN.getCode())) {
                update_param.put("htEarnGramt", 1); // 총 획득량 1 생성
                update_param.put("htBlnc", 1);      // 잔액 1 생성
            } else {
                update_param.put("htEarnGramt", 0);
                update_param.put("htBlnc", 0);
            }
            update_param.put("stEarnGramt", 0);
            update_param.put("stBlnc", 0);

            stntRewardMapper.insertRwdEarnInfo(update_param);
        } else {
            // 기존 데이터 업데이트
            if(rwdAdjType.equals(RewardAdjustType.EARN.getCode())) {
                // 하트 증가
                update_param.put("htEarnGramt", (int)rwdInfo.get("htEarnGramt") + 1);
                update_param.put("htBlnc", (int)rwdInfo.get("htBlnc") + 1);
            } else {
                // 하트 차감
                update_param.put("htEarnGramt", (int)rwdInfo.get("htEarnGramt"));
                update_param.put("htBlnc", (int)rwdInfo.get("htBlnc") - 1);
            }
            update_param.put("stEarnGramt", (int)rwdInfo.get("stEarnGramt"));
            update_param.put("stBlnc", (int)rwdInfo.get("stBlnc"));

            stntRewardMapper.updateRwdEarnInfo(update_param);
        }

        resultData.put("success", true);
        // resultData.put("rwdInfo", stntRewardMapper.selectRwdEarnInfo(update_param)); // 업데이트된 리워드 정보 조회
        resultData.put("message", rwdAdjType.equals(RewardAdjustType.EARN.getCode()) ? 
                "하트가 지급되었습니다." : "하트가 차감되었습니다.");

        return resultData;
    }
}
