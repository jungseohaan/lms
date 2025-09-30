package com.visang.aidt.lms.api.user.service;

import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.shop.mapper.ShopMapper;
import com.visang.aidt.lms.api.user.mapper.StntRewardMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StntRewardService {
    private final StntRewardMapper stntRewardMapper;
    private final ShopMapper shopMapper;
    private final StntNtcnService stntNtcnService;

    /**
     * 교사에 대한 리워드 최초 적립 메소드
     * (하트: 100000, 스타: 100000)
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    public int createInitialAccumOfTeacherReward(Map<String, Object> paramData) throws Exception {
        int rwdId = 0;

        // 유저유형 (T:교사, S:학생, P:학부모)
        String userSeCd = MapUtils.getString(paramData, "userSeCd");
        // 교사(T)인 경우만 처리
        if ("T".equals(userSeCd)) {
            //1. 리워드획득정보 최초 확인
            Map<String, Object> selectRwdEarnInfo = stntRewardMapper.selectRwdEarnInfo(paramData);
            if (MapUtils.isEmpty(selectRwdEarnInfo)) {
                //선생일 경우 rwd_earn_hist에 리워드생성 히스토리를 먼저 넣어준다. 후에 rwd_earn_info생성 시 남은 리워드 자동 계산이 됨.
                Map<String, Object> teacher_param = new HashMap<>();
                teacher_param.put("userId", paramData.get("userId"));
                teacher_param.put("claId", paramData.get("claId"));
                teacher_param.put("seCd", "1"); //획득
                teacher_param.put("menuSeCd", null);
                teacher_param.put("sveSeCd", null);
                teacher_param.put("trgtId", 0);
                teacher_param.put("rwdSeCd", "1");  //하트
                teacher_param.put("rwdAmt", 100000); //10만
                teacher_param.put("rwdUseAmt", 0); //10만

                //하트적립
                stntRewardMapper.insertRwdEarnHist(teacher_param);

                //스타적립
                teacher_param.put("rwdSeCd", "2"); //스타
                stntRewardMapper.insertRwdEarnHist(teacher_param);

                // 2. 리워드획득정보 최초 생성
                List<Map<String, Object>> HtStSum = stntRewardMapper.selectRwdEarnHistHtStSum(paramData); //리워드히스토리에서 sum 불러오기
                Map<String, Object> insertRwdEarnHistData = new LinkedHashMap<>();
                insertRwdEarnHistData.put("userId", paramData.get("userId"));
                insertRwdEarnHistData.put("claId", paramData.get("claId"));
                for (Map<String, Object> temp : HtStSum) {
                    BigDecimal rwdAmtSum = new BigDecimal(temp.get("rwdAmtSum").toString()); //리워드금액SUM
                    BigDecimal rwdRemainSum = new BigDecimal(temp.get("rwdRemainSum").toString()); //리워드총금액에 리워드사용금액 빼기

                    if ("1".equals(temp.get("rwdSeCd").toString())) {
                        insertRwdEarnHistData.put("htEarnGramt", rwdAmtSum);
                        insertRwdEarnHistData.put("htBlnc", rwdRemainSum);
                    } else if ("2".equals(temp.get("rwdSeCd").toString())) {
                        insertRwdEarnHistData.put("stEarnGramt", rwdAmtSum);
                        insertRwdEarnHistData.put("stBlnc", rwdRemainSum);
                    }
                }
                stntRewardMapper.insertRwdEarnInfo(insertRwdEarnHistData); // rwd_earn_info 최초 생성
                rwdId = MapUtils.getInteger(insertRwdEarnHistData, "rwdId");
                if(log.isDebugEnabled()) {
                    log.debug("rwdId : {}", rwdId);
                }
            }
        }

        return rwdId;
    }

    /**
     * (학생).리워드생성 메소드
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> createReward(Map<String, Object> paramData) throws Exception {
            Map<String, Object> returnMap = new HashMap<>();
            int rwdId = 0;      //저장된 리워드획득정보 ID값 return
            int rwdHistId = 0;  //저장된 리워드획득이력 ID값 return

            returnMap.put("rwdId", null); //리워드획득정보ID
            returnMap.put("rwdHistId", null); //리워드획득이력ID
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");

            //0. 필수값 validation 체크
            if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
                returnMap.put("resultMsg", "userId를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("claId") == null || ("").equals(paramData.get("claId"))) {
                returnMap.put("resultMsg", "claId를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("seCd") == null || ("").equals(paramData.get("seCd"))) {
                paramData.put("seCd", "1"); //1:획득
            } else {
                if ("2".equals(paramData.get("seCd").toString())) { //사용 리워드 체크 rwdUseAmt
                    returnMap.put("resultMsg", "seCd:2, 리워드 사용 메소드가 아닙니다.");
                    return returnMap;
                }
            }
            if (paramData.get("menuSeCd") == null || ("").equals(paramData.get("menuSeCd"))) {
                returnMap.put("resultMsg", "menuSeCd를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("sveSeCd") == null || ("").equals(paramData.get("sveSeCd"))) {
                returnMap.put("resultMsg", "sveSeCd를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("trgtId") == null || ("").equals(paramData.get("trgtId"))) {
                returnMap.put("resultMsg", "trgtId를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("rwdSeCd") == null || ("").equals(paramData.get("rwdSeCd"))) {
                returnMap.put("resultMsg", "rwdSeCd를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("correctAnwNum") == null || ("").equals(paramData.get("correctAnwNum"))) {
                paramData.put("correctAnwNum", 1); //정답수 Default:1
            }

            paramData.put("rwdUseAmt", 0); //생성일때는 리워드사용금액이 0


            //유저 유효 여부 확인
            Map<String, Object> findShopUserInfo = shopMapper.findShopUserInfo(paramData);
//            if(MapUtils.isEmpty(findShopUserInfo)) {
//                returnMap.put("rwdId", rwdId); //리워드획득정보ID
//                returnMap.put("rwdHistId", rwdHistId); //리워드획득이력ID
//                returnMap.put("resultOk", false);
//                returnMap.put("resultMsg", "일치하는 유저정보가 없습니다.");
//                return returnMap;
//            }
            String userSeCd = findShopUserInfo.get("userSeCd").toString(); //학생, 교사 구분용

            //0. 생성할 획득 리워드 계산
            Map<String, Object> rewardPcyMap = stntRewardMapper.selectRwdEarnPcy(paramData);
            if(MapUtils.isEmpty(rewardPcyMap)) { //리워드 정책이 없을 경우 rwdAmt 필수
                if (paramData.get("rwdAmt") == null || ("").equals(paramData.get("rwdAmt"))) {
                    returnMap.put("resultMsg", "리워드 정책이 없습니다. rwdAmt를 입력해주세요");
                    return returnMap;
                }
            } else { //리워드 정책이 있을 때, 없으면 인입 리워드 사용
                if("3".equals(paramData.get("sveSeCd").toString()) || "4".equals(paramData.get("sveSeCd").toString())){
                    //서비스구분 - 3:과제, 4:평가인 경우
                    paramData.put("rwdAmt", rewardPcyMap.get("rwdPoint"));
                } else if("1".equals(paramData.get("sveSeCd").toString()) || "2".equals(paramData.get("sveSeCd").toString())
                 ||"5".equals(paramData.get("sveSeCd").toString()) || "6".equals(paramData.get("sveSeCd").toString())
                        || "7".equals(paramData.get("sveSeCd").toString())){
                    //서비스구분 - 1:문제, 2:활동, 5:AI학습, 6:선택학습, 7:다른문제풀기
                    paramData.put("rwdAmt", rewardPcyMap.get("rwdPointMultiply"));
                } else {
                    //서비스구분 -  또는 외
                }
            }

            // 1. 리워드획득이력 생성
            int insertRwdEarnHist = stntRewardMapper.insertRwdEarnHist(paramData);
            rwdHistId = MapUtils.getInteger(paramData, "rwdHistId");

            if (insertRwdEarnHist > 0) {
                //2. 리워드획득정보 최초 확인
                Map<String, Object> selectRwdEarnInfo = stntRewardMapper.selectRwdEarnInfo(paramData);

                if (MapUtils.isEmpty(selectRwdEarnInfo)) {
                    //선생일 경우 rwd_earn_hist에 리워드생성 히스토리를 먼저 넣어준다. 후에 rwd_earn_info생성 시 남은 리워드 자동 계산이 됨.
                    /* 2024-07-23, initialAccumOfTeacherReward 메소드에서 처리 */
                    /*
                    if("T".equals(userSeCd)){
                        Map<String, Object> teacher_param = new HashMap<>();
                        teacher_param.put("userId", paramData.get("userId"));
                        teacher_param.put("claId", paramData.get("claId"));
                        teacher_param.put("seCd", "1"); //획득
                        teacher_param.put("menuSeCd", null);
                        teacher_param.put("sveSeCd", null);
                        teacher_param.put("trgtId", 0);
                        teacher_param.put("rwdSeCd", "1");  //하트
                        teacher_param.put("rwdAmt", 100000); //10만
                        teacher_param.put("rwdUseAmt", 0); //10만

                        //하트적립
                        int teacher_specialReward = stntRewardMapper.insertRwdEarnHist(teacher_param);

                        //스타적립
                        teacher_param.put("rwdSeCd", "2"); //스타
                        teacher_specialReward = stntRewardMapper.insertRwdEarnHist(teacher_param);
                    }*/

                    // 리워드획득정보 최초 생성
                    List<Map<String, Object>> HtStSum = stntRewardMapper.selectRwdEarnHistHtStSum(paramData); //리워드히스토리에서 sum 불러오기
                    Map<String, Object> insertRwdEarnHistData = new LinkedHashMap<>();
                    insertRwdEarnHistData.put("userId", paramData.get("userId"));
                    insertRwdEarnHistData.put("claId", paramData.get("claId"));
                    for(Map<String, Object> temp:HtStSum) {
                        BigDecimal rwdAmtSum = new BigDecimal(temp.get("rwdAmtSum").toString()); //리워드금액SUM
                        BigDecimal rwdRemainSum = new BigDecimal(temp.get("rwdRemainSum").toString()); //리워드총금액에 리워드사용금액 빼기

                        if ("1".equals(temp.get("rwdSeCd").toString())) {
                            insertRwdEarnHistData.put("htEarnGramt", rwdAmtSum);
                            insertRwdEarnHistData.put("htBlnc", rwdRemainSum);

                        } else if ("2".equals(temp.get("rwdSeCd").toString())) {
                            insertRwdEarnHistData.put("stEarnGramt", rwdAmtSum);
                            insertRwdEarnHistData.put("stBlnc", rwdRemainSum);
                        }
                    }
                    stntRewardMapper.insertRwdEarnInfo(insertRwdEarnHistData); // rwd_earn_info 최초 생성
                    rwdId = MapUtils.getInteger(insertRwdEarnHistData, "rwdId");

                } else {
                    rwdId = Integer.parseInt(selectRwdEarnInfo.get("id").toString()); //이미존재하는 리워드획득정보 ID

                    //리워드획득정보 업데이트
                    List<Map<String, Object>> HtStSum = stntRewardMapper.selectRwdEarnHistHtStSum(paramData); //리워드히스토리에서 sum 불러오기
                    Map<String, Object> insertRwdEarnInfoData = new LinkedHashMap<>();
                    insertRwdEarnInfoData.put("userId", paramData.get("userId"));
                    insertRwdEarnInfoData.put("claId", paramData.get("claId"));
                    for(Map<String,Object> temp : HtStSum) {
                        BigDecimal rwdAmtSum = new BigDecimal(temp.get("rwdAmtSum").toString()); //리워드금액SUM
                        BigDecimal rwdRemainSum = new BigDecimal(temp.get("rwdRemainSum").toString()); //리워드총금액에 리워드사용금액 빼기

                        if ("1".equals(temp.get("rwdSeCd").toString())) {
                            insertRwdEarnInfoData.put("htEarnGramt", rwdAmtSum);
                            insertRwdEarnInfoData.put("htBlnc", rwdRemainSum);

                        } else if ("2".equals(temp.get("rwdSeCd").toString())) {
                            insertRwdEarnInfoData.put("stEarnGramt", rwdAmtSum);
                            insertRwdEarnInfoData.put("stBlnc", rwdRemainSum);
                        }
                    }
                    stntRewardMapper.updateRwdEarnInfo(insertRwdEarnInfoData); //잔액업데이트
                }
            } else {
                //실패
                paramData.remove("rwdHistId");
                returnMap.put("rwdId", 0); //리워드획득정보ID
                returnMap.put("rwdHistId", 0); //리워드획득이력ID
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "리워드획득이력 저장실패");
                return returnMap;
            }
            String ntcnCn = "";
            String rwdSeCd = paramData.get("rwdSeCd").toString().equals("1") ? "하트" : "스타";
            String rwdAmt = paramData.get("rwdAmt").toString();
            String trgetTyCd = "";
            switch (paramData.get("sveSeCd").toString()) {
                case "1":
                    ntcnCn = "문제 풀이를 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "5";
                    break;
                case  "2":
                    ntcnCn = "활동 진행을 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "5";
                    break;
                case  "3":
                    ntcnCn = "과제 풀이를 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "9";
                    break;
                case  "4":
                    ntcnCn = "평가 진행을 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "10";
                    break;
                case  "5":
                    ntcnCn = "AI 학습을 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "1";
                    break;
                case  "6":
                    ntcnCn = "스스로 학습을 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "5";
                    break;
                case  "7":
                    ntcnCn = "다른 문제 풀기를 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "5";
                    break;
                case  "12":
                    ntcnCn = "노트 정리를 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "18";
                    break;
                case  "13":
                    ntcnCn = "다시 풀기를 완료하여 " + rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "19";
                    break;
                default:
                    ntcnCn = rwdSeCd + " 리워드 " + rwdAmt + "개를 획득하였습니다.";
                    trgetTyCd = "5";
                    break;
            }

            Map<String, Object> rewardSendInfo = stntRewardMapper.findRewardSendInfo(paramData);

            if(!MapUtils.isEmpty(rewardSendInfo)) {
                Map<String, Object> ntcnMap = new HashMap<>();
                ntcnMap.put("userId", rewardSendInfo.get("wrterId"));
                ntcnMap.put("rcveId", rewardSendInfo.get("stntId"));
                ntcnMap.put("textbkId", paramData.get("textbkId"));
                ntcnMap.put("claId", rewardSendInfo.get("claId"));
                ntcnMap.put("trgetCd", "S");
                ntcnMap.put("linkUrl", "");
                ntcnMap.put("stntNm", rewardSendInfo.get("flnm"));
                ntcnMap.put("ntcnTyCd", "3");
                ntcnMap.put("trgetTyCd", trgetTyCd);
                ntcnMap.put("ntcnCn", ntcnCn);

                stntNtcnService.createStntNtcnSave(ntcnMap);

                returnMap.put("ntcnCn", ntcnCn);
            }

            paramData.remove("rwdHistId");
            returnMap.put("rwdId", rwdId); //리워드획득정보ID
            returnMap.put("rwdHistId", rwdHistId); //리워드획득이력ID
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");

        return returnMap;
    }



    /**
     * (학생).리워드사용 메소드
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> useReward(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new HashMap<>();
        int rwdId = 0;      //저장된 리워드획득정보 ID값 return
        int rwdHistId = 0;  //저장된 리워드획득이력 ID값 return

        returnMap.put("rwdId", null); //리워드획득정보ID
        returnMap.put("rwdHistId", null); //리워드획득이력ID
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "저장실패");


            //0. 필수값 validation 체크
            if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
                returnMap.put("resultMsg", "userId를 입력해주세요");
                return returnMap;
            }
            if (paramData.get("claId") == null || ("").equals(paramData.get("claId"))) {
                returnMap.put("resultMsg", "claId를 입력해주세요");
                return returnMap;
            }

            //구분
            paramData.put("rwdAmt", 0); //사용일때는 리워드금액이 0
            if (paramData.get("seCd") == null || ("").equals(paramData.get("seCd"))) {
                paramData.put("seCd", "2"); //2:사용
                if (paramData.get("rwdUseAmt") == null || ("").equals(paramData.get("rwdUseAmt"))) {
                    returnMap.put("resultMsg", "rwdUseAmt를 입력해주세요");
                    return returnMap;
                }
            } else {
                if ("1".equals(paramData.get("seCd").toString())) { //획득리워드 체크 rwdAmt
                    return returnMap;
                } else if ("2".equals(paramData.get("seCd").toString())) { //사용 리워드 체크 rwdUseAmt
                    if (paramData.get("rwdUseAmt") == null || ("").equals(paramData.get("rwdUseAmt"))) {
                        returnMap.put("resultMsg", "rwdUseAmt를 입력해주세요");
                        return returnMap;
                    }
                }
            }

            //메뉴구분
            if (paramData.get("menuSeCd") == null || ("").equals(paramData.get("menuSeCd"))) {
                returnMap.put("resultMsg", "menuSeCd를 입력해주세요");
                return returnMap;
            }
            //서비스구분
            if (paramData.get("sveSeCd") == null || ("").equals(paramData.get("sveSeCd"))) {
                returnMap.put("resultMsg", "sveSeCd를 입력해주세요");
                return returnMap;
            }
            //대상ID
            if (paramData.get("trgtId") == null || ("").equals(paramData.get("trgtId"))) {
                returnMap.put("resultMsg", "trgtId를 입력해주세요");
                return returnMap;
            }
            //리워드구분 1:하트 2:스타
            if (paramData.get("rwdSeCd") == null || ("").equals(paramData.get("rwdSeCd"))) {
                returnMap.put("resultMsg", "rwdSeCd를 입력해주세요");
                return returnMap;
            }

            // 1. 리워드획득이력 생성
            int insertRwdEarnHist = stntRewardMapper.insertRwdEarnHist(paramData);
            rwdHistId = MapUtils.getInteger(paramData, "rwdHistId");

            if (insertRwdEarnHist > 0) {
                //2. 리워드획득정보 최초 확인
                Map<String, Object> insertRwdEarnInfo = stntRewardMapper.selectRwdEarnInfo(paramData); //Insert 후 ID값 가져오기
                rwdId = MapUtils.getInteger(insertRwdEarnInfo, "id");


                //리워드획득정보 업데이트 하트,스타 관계없이 다시 설정
                List<Map<String, Object>> HtStSum = stntRewardMapper.selectRwdEarnHistHtStSum(paramData); //리워드히스토리에서 sum 불러오기
                Map<String, Object> insertRwdEarnInfoData = new LinkedHashMap<>();
                insertRwdEarnInfoData.put("userId", paramData.get("userId"));
                insertRwdEarnInfoData.put("claId", paramData.get("claId"));
                for(Map<String,Object> temp : HtStSum) {
                    BigDecimal rwdAmtSum = new BigDecimal(temp.get("rwdAmtSum").toString()); //리워드금액SUM
                    BigDecimal rwdRemainSum = new BigDecimal(temp.get("rwdRemainSum").toString()); //리워드총금액에 리워드사용금액 빼기

                    if ("1".equals(temp.get("rwdSeCd").toString())) {
                        insertRwdEarnInfoData.put("htEarnGramt", rwdAmtSum);
                        insertRwdEarnInfoData.put("htBlnc", rwdRemainSum);
                    } else if ("2".equals(temp.get("rwdSeCd").toString())) {
                        insertRwdEarnInfoData.put("stEarnGramt", rwdAmtSum);
                        insertRwdEarnInfoData.put("stBlnc", rwdRemainSum);
                    }
                }
                stntRewardMapper.updateRwdEarnInfo(insertRwdEarnInfoData); //잔액 업데이트
            }

            returnMap.put("rwdId", rwdId); //리워드획득정보ID
            returnMap.put("rwdHistId", rwdHistId); //리워드획득이력ID
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "저장완료");

        return returnMap;
    }
    /**
     * (학생).리워드회수 메소드
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> rewardReset(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new HashMap<>();
        int rwdId = 0;      //저장된 리워드획득정보 ID값 return
        int rwdHistId = 0;  //저장된 리워드획득이력 ID값 return

        returnMap.put("rwdId", null); //리워드획득정보ID
        returnMap.put("rwdHistId", null); //리워드획득이력ID
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "저장실패");


        //0. 필수값 validation 체크
        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("claId") == null || ("").equals(paramData.get("claId"))) {
            returnMap.put("resultMsg", "claId를 입력해주세요");
            return returnMap;
        }

        //구분
        paramData.put("rwdAmt", 0); //사용일때는 리워드금액이 0
        if (paramData.get("seCd") == null || ("").equals(paramData.get("seCd"))) {
            paramData.put("seCd", "1"); //1:획득, 2:사용 - 획득한 리워드 히스토리 삭제를 위해 1
            if (paramData.get("rwdUseAmt") == null || ("").equals(paramData.get("rwdUseAmt"))) {
                returnMap.put("resultMsg", "rwdUseAmt를 입력해주세요");
                return returnMap;
            }
        }

        //메뉴구분
        if (paramData.get("menuSeCd") == null || ("").equals(paramData.get("menuSeCd"))) {
            returnMap.put("resultMsg", "menuSeCd를 입력해주세요");
            return returnMap;
        }
        //서비스구분
        if (paramData.get("sveSeCd") == null || ("").equals(paramData.get("sveSeCd"))) {
            returnMap.put("resultMsg", "sveSeCd를 입력해주세요");
            return returnMap;
        }
        //대상ID
        if (paramData.get("trgtId") == null || ("").equals(paramData.get("trgtId"))) {
            returnMap.put("resultMsg", "trgtId를 입력해주세요");
            return returnMap;
        }
        //리워드구분 1:하트 2:스타
        if (paramData.get("rwdSeCd") == null || ("").equals(paramData.get("rwdSeCd"))) {
            returnMap.put("resultMsg", "rwdSeCd를 입력해주세요");
            return returnMap;
        }

        // 1. 리워드회득이력 삭제
        int deleteRwdEarnHist = stntRewardMapper.deleteRwdEarnHist(paramData);


        if (deleteRwdEarnHist > 0) {
            //2. 리워드획득정보 최초 확인
            Map<String, Object> insertRwdEarnInfo = stntRewardMapper.selectRwdEarnInfo(paramData); //Insert 후 ID값 가져오기
            rwdId = MapUtils.getInteger(insertRwdEarnInfo, "id");


            //리워드획득정보 업데이트 하트,스타 관계없이 다시 설정
            List<Map<String, Object>> HtStSum = stntRewardMapper.selectRwdEarnHistHtStSum(paramData); //리워드히스토리에서 sum 불러오기
            Map<String, Object> insertRwdEarnInfoData = new LinkedHashMap<>();
            insertRwdEarnInfoData.put("userId", paramData.get("userId"));
            insertRwdEarnInfoData.put("claId", paramData.get("claId"));
            insertRwdEarnInfoData.put("seCd", 2);  // 2:사용
            for(Map<String,Object> temp : HtStSum) {
                BigDecimal rwdAmtSum = new BigDecimal(temp.get("rwdAmtSum").toString()); //리워드금액SUM
                BigDecimal rwdRemainSum = new BigDecimal(temp.get("rwdRemainSum").toString()); //리워드총금액에 리워드사용금액 빼기

                if ("1".equals(temp.get("rwdSeCd").toString())) {
                    insertRwdEarnInfoData.put("htEarnGramt", rwdAmtSum);
                    insertRwdEarnInfoData.put("htBlnc", rwdRemainSum);
                } else if ("2".equals(temp.get("rwdSeCd").toString())) {
                    insertRwdEarnInfoData.put("stEarnGramt", rwdAmtSum);
                    insertRwdEarnInfoData.put("stBlnc", rwdRemainSum);
                }
            }
            stntRewardMapper.updateRwdEarnInfo(insertRwdEarnInfoData); //잔액 업데이트
        }

        returnMap.put("rwdId", rwdId); //리워드획득정보ID
        returnMap.put("rwdHistId", rwdHistId); //리워드획득이력ID
        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "저장완료");

        return returnMap;
    }
    /**
     * (학생).리워드 현황 조회 메소드
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findRewardStatus(Map<String, Object> paramData) throws Exception {
        List<String> rewardInfoItem = Arrays.asList("userId", "flnm", "smtHtEarnGramt", "monHtEarnGramt", "htBlnc");
        LinkedHashMap<Object, Object> rewardInfoMap = AidtCommonUtil.filterToMap(rewardInfoItem, stntRewardMapper.findRewardStatusInfo(paramData));

        return rewardInfoMap;
    }

    /**
     *
     * @param paramData
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Object findRewardList(Map<String, Object> paramData, Pageable pageable) throws Exception {

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> myStntRwdInfolist = stntRewardMapper.findStntMyRewardInfoList(pagingParam);

        if (!myStntRwdInfolist.isEmpty()) {
            total = (long) myStntRwdInfolist.get(0).get("fullCount");
        }
        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(myStntRwdInfolist, pageable, total);

        myStntRwdInfolist.forEach(s -> {
            s.remove("fullCount");
        });

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        returnMap.put("MyRwdInfo", myStntRwdInfolist);
        returnMap.put("page",page);

        return returnMap;
    }
}
