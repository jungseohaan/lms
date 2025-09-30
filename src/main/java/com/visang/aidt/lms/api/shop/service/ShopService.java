package com.visang.aidt.lms.api.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.shop.mapper.ShopMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class ShopService {
    private final ShopMapper shopMapper;
    private final StntRewardService stntRewardService;

    @Value("${app.shop.filePathRoot}")
    private String FILE_PATH_ROOT;

    /**
     * (샵).유저 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    /* 메소드 내부에 insert 메소드를 포함하고 있어서 @Transactional(readOnly = true) 처리 하지 않음 */
    public Object findShopUserInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        if(ObjectUtils.isEmpty(paramData.get("userId"))){
            returnMap.put("resultMsg", "UUID가 존재하지 않습니다.");
            returnMap.put("resultOk", true);
            return returnMap;
        }

        List<String> shopUserInfo = Arrays.asList("userId","flnm", "userSeCd","rprsGdsAnct", "htBlnc", "stBlnc");
        List<String> shopItemProfileInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
        List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");

        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(shopUserInfo, shopMapper.findShopUserInfo(paramData));

        // 유저유형 (T:교사, S:학생, P:학부모)
        String userSeCd = MapUtils.getString(findShopUserInfo, "userSeCd");
        //유저유형셋팅
        paramData.put("userSeCd", userSeCd);

        // 2024-07-23, (교사) 스타/하트 100000점 적립 추가
        // 교사(T)인 경우만 처리
        if ("T".equals(userSeCd)) {
            int rwdId = stntRewardService.createInitialAccumOfTeacherReward(paramData);
            // 리워드획득정보를 등록한 경우 다시 조회.
            if (rwdId > 0) {
                findShopUserInfo = AidtCommonUtil.filterToMap(shopUserInfo, shopMapper.findShopUserInfo(paramData));
            }
        }

        //ItemProfile Service
        LinkedHashMap<Object, Object> shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfo(paramData));
        if(MapUtils.isEmpty(shopItemProfileInfoTemp)) {
            shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfoDefault(paramData));
        }
        //ItemGame Service
        LinkedHashMap<Object, Object> shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfo(paramData));
        if(MapUtils.isEmpty(shopItemGameInfoTemp)) {
            shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfoDefault(paramData));
        }
        //ItemSkin Service
        LinkedHashMap<Object, Object> shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfo(paramData));
        if(MapUtils.isEmpty(shopItemSkinInfoTemp)) {
            shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfoDefault(paramData));
        }


        //1
        findShopUserInfo.put("itemProfileInfo", shopItemProfileInfoTemp);
        findShopUserInfo.put("itemGameInfo", shopItemGameInfoTemp);
        findShopUserInfo.put("itemSkinInfo", shopItemSkinInfoTemp);

        //2
        returnMap.put("userInfo", findShopUserInfo);
        returnMap.put("resultOk", true);
        returnMap.put("filePathRoot", FILE_PATH_ROOT); //상점 아이템 파일 서버 경로

        paramData.remove("userSeCd");
        return returnMap;
    }


    /**
     * (샵).상점 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findShopItemList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> findShopUserInfoForm = Arrays.asList("userId","flnm", "userSeCd", "rprsGdsAnct", "htBlnc", "stBlnc");
        List<String> findShopUserInfoForm2 = Arrays.asList("prchsGdsSeNm","prchsGdsSeNm2");
        List<String> shopItemProfileInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
       // List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "gmAllImg196");
        List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");
        // List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "skAllImg196", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");


        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값
        LinkedHashMap<Object, Object> findShopUserInfo2 = AidtCommonUtil.filterToMap(findShopUserInfoForm2, shopMapper.findShopUserInfo(paramData)); //Response 맨 바깥에 있는 값

        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));

        //ItemProfile Service
        LinkedHashMap<Object, Object> shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfo(paramData));
        if(MapUtils.isEmpty(shopItemProfileInfoTemp)) {
            shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfoDefault(paramData));
        }
        //ItemGame Service
        LinkedHashMap<Object, Object> shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfo(paramData));
        if(MapUtils.isEmpty(shopItemGameInfoTemp)) {
            shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfoDefault(paramData));
        }
        //ItemSkin Service
        LinkedHashMap<Object, Object> shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfo(paramData));
        if(MapUtils.isEmpty(shopItemSkinInfoTemp)) {
            shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfoDefault(paramData));
        }

        //UserInfo 객체 끝에 추가
        findShopUserInfo.put("itemProfileInfo", shopItemProfileInfoTemp);
        findShopUserInfo.put("itemGameInfo", shopItemGameInfoTemp);
        findShopUserInfo.put("itemSkinInfo", shopItemSkinInfoTemp);

        //1 : UserInfo
        returnMap.put("userInfo", findShopUserInfo); //리턴맵에 UserInfo 셋팅

        //2 : 상점구분ID, 상점구분명, category 셋팅
        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); //상점구분코드
        returnMap.put("prchsGdsSeNm", findShopUserInfo2.get("prchsGdsSeNm2")); //상점구분명

        List<String> categoryForm = Arrays.asList("category");
        List<LinkedHashMap<Object, Object>> categoryList = AidtCommonUtil.filterToList(categoryForm, shopMapper.findSpTmeInfoCtgry(paramData));
        String[] categoryArray = new String[categoryList.size()];
        int num = 0;
        for(int i=0; i<categoryList.size(); i++){
            if(categoryList.get(i).get("category") != null && !"".equals(categoryList.get(i).get("category").toString())) {
                categoryArray[i] = categoryList.get(i).get("category").toString();
                num++;
            }
        }

        String[] categoryArrays = new String[num];
        int index = 0;
        for(String s : categoryArray) {
            if(s != null) {
                categoryArrays[index++] = s;
            }
        }

        returnMap.put("themeList", categoryArrays); //themeList

        //3 : ShopItemInfo
        List<String> shopItemInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");
        List<String> shopItemInfoFormP = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg","pfAllImg196", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");
//        List<String> shopItemInfoFormP = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "theme", "ownYn", "prchsId", "invSeCd");
//        List<String> shopItemInfoFormS = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "skAllImg196", "theme", "ownYn", "prchsId", "invSeCd");
//        List<String> shopItemInfoFormG = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "gmAllImg196", "theme", "ownYn", "prchsId", "invSeCd");

        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoP = AidtCommonUtil.filterToList(shopItemInfoFormP, shopMapper.findShopItemInfoP(paramData));
            returnMap.put("shopItemInfo", findShopItemInfoP); //shopItemList

        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoS = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoS(paramData));
//            List<LinkedHashMap<Object, Object>> findShopItemInfoS = AidtCommonUtil.filterToList(shopItemInfoFormS, shopMapper.findShopItemInfoS(paramData));
            returnMap.put("shopItemInfo", findShopItemInfoS); //shopItemList

        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoG = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoG(paramData));
//            List<LinkedHashMap<Object, Object>> findShopItemInfoG = AidtCommonUtil.filterToList(shopItemInfoFormG, shopMapper.findShopItemInfoG(paramData));
            returnMap.put("shopItemInfo", findShopItemInfoG); //shopItemList
        } else {
            returnMap.put("shopItemInfo", ""); //shopItemList
        }

        paramData.remove("userSeCd");
        return returnMap;
    }


    /**
     * (샵).상점 상세 요청
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findShopItemDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // 1. 상점 구분 returnMap

        //UserInfo에서 유저유형 가져오기
        List<String> findShopUserInfoForm = Arrays.asList("userSeCd");
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값
        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));


        //3: 상점 아이템 정보 shopItemList
        List<String> shopItemProfileInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
        List<String> shopItemSkinInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");

        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            LinkedHashMap<Object, Object> findShopItemProfileDetail = AidtCommonUtil.filterToMap(shopItemProfileInfoForm, shopMapper.findShopItemProfileDetail(paramData));
            returnMap.put("shopItemInfo", findShopItemProfileDetail); //shopItemList

        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            LinkedHashMap<Object, Object> findShopItemGameDetail = AidtCommonUtil.filterToMap(shopItemGameInfoForm, shopMapper.findShopItemGameDetail(paramData));
            returnMap.put("shopItemInfo", findShopItemGameDetail); //shopItemList

        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            LinkedHashMap<Object, Object> findShopItemSkinDetail = AidtCommonUtil.filterToMap(shopItemSkinInfoForm, shopMapper.findShopItemSkinDetail(paramData));
            returnMap.put("shopItemInfo", findShopItemSkinDetail); //shopItemList
        } else {
            returnMap.put("shopItemInfo.", ""); //shopItemList
        }

        paramData.remove("userSeCd");
        return returnMap;
    }

    /**
     * (샵).상점 구매 요청
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public  Map<String, Object> getShopbuyItem(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();


        List<String> findShopUserInfoForm = Arrays.asList("userId","flnm", "userSeCd", "rprsGdsAnct", "htBlnc", "stBlnc");

        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값

        if(MapUtils.isEmpty(findShopUserInfo)) {
            returnMap.put("itemId", paramData.get("itemId"));
            returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "유저 정보가 없습니다.");
            return returnMap;
        }

        String userSeCd = findShopUserInfo.get("userSeCd").toString(); //학생, 교사 구분용

        //기구매 아이템 체크
        /*List<Map> chkHaveItem = shopMapper.findSpPrchsHistItmChk(paramData);
        if(chkHaveItem.size() > 0) {
            returnMap.put("itemId", paramData.get("itemId"));
            returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "이미 구매한 아이템입니다.");
            return returnMap;
        }*/

        //set UserInfo
        returnMap.put("userInfo", findShopUserInfo);

        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));
        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // 1. 상점 구분 returnMap

        //3 : ShopItemInfo
        List<String> shopItemInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");
      //  List<String> shopItemInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "pfAllImg196", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");

        BigDecimal htNtslAmt = BigDecimal.ZERO;
        BigDecimal stNtslAmt = BigDecimal.ZERO;

        LinkedHashMap<String, Object> insertPrchsInfo = new LinkedHashMap<>(); //구매이력 인서트용
        insertPrchsInfo.put("userId", paramData.get("userId"));
        insertPrchsInfo.put("userSeCd", findShopUserInfo.get("userSeCd"));
        insertPrchsInfo.put("claId", paramData.get("claId"));
        insertPrchsInfo.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // P, S, G
        insertPrchsInfo.put("invSeCd", "1"); // 1:구매목록, 2:보관함
        insertPrchsInfo.put("rwdSeCd", paramData.get("rwdSeCd")); //1:하트, 2:스타
        insertPrchsInfo.put("prchsGdsId", paramData.get("itemId")); //구매상품ID

        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            LinkedHashMap<Object, Object> findShopItemProfileDetail = AidtCommonUtil.filterToMap(shopItemInfoForm, shopMapper.findShopItemProfileDetail(paramData)); //계산용
            if("1".equals(paramData.get("rwdSeCd").toString())) {//하트
                insertPrchsInfo.put("ntslGdsAmt", findShopItemProfileDetail.get("htNtslAmt"));
            } else { //스타
                insertPrchsInfo.put("ntslGdsAmt", findShopItemProfileDetail.get("stNtslAmt"));
            }
            htNtslAmt = new BigDecimal(findShopItemProfileDetail.get("htNtslAmt").toString());
            stNtslAmt = new BigDecimal(findShopItemProfileDetail.get("stNtslAmt").toString());

        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            LinkedHashMap<Object, Object> findShopItemGameDetail = AidtCommonUtil.filterToMap(shopItemInfoForm, shopMapper.findShopItemGameDetail(paramData)); //계산용
            if("1".equals(paramData.get("rwdSeCd").toString())) {//하트
                insertPrchsInfo.put("ntslGdsAmt", findShopItemGameDetail.get("htNtslAmt"));
            } else { //스타
                insertPrchsInfo.put("ntslGdsAmt", findShopItemGameDetail.get("stNtslAmt"));
            }
            htNtslAmt = new BigDecimal(findShopItemGameDetail.get("htNtslAmt").toString());
            stNtslAmt = new BigDecimal(findShopItemGameDetail.get("stNtslAmt").toString());

        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            LinkedHashMap<Object, Object> findShopItemSkinDetail = AidtCommonUtil.filterToMap(shopItemInfoForm, shopMapper.findShopItemSkinDetail(paramData)); //계산용
            if("1".equals(paramData.get("rwdSeCd").toString())) {//하트
                insertPrchsInfo.put("ntslGdsAmt", findShopItemSkinDetail.get("htNtslAmt"));
            } else { //스타
                insertPrchsInfo.put("ntslGdsAmt", findShopItemSkinDetail.get("stNtslAmt"));
            }
            htNtslAmt = new BigDecimal(findShopItemSkinDetail.get("htNtslAmt").toString());
            stNtslAmt = new BigDecimal(findShopItemSkinDetail.get("stNtslAmt").toString());
        } else {
            returnMap.put("shopItemList", ""); //shopItemList
        }


        //상점 구매 요청 처리
        //사용자 하트, 또는 스타 금액확인
        BigDecimal htBlnc = new BigDecimal(findShopUserInfo.get("htBlnc").toString()); //사용자하트
        BigDecimal stBlnc = new BigDecimal(findShopUserInfo.get("stBlnc").toString()); //사용자스타
        List<String> ntslGdsAmtSumForm = Arrays.asList("ntslGdsAmtSum");


        if("1".equals(paramData.get("rwdSeCd").toString())) {//하트로 구매
            //if(BigDecimal.ZERO.compareTo(htNtslAmt) == 0 && !"T".equals(userSeCd)) {
            if(BigDecimal.ZERO.compareTo(htNtslAmt) == 0) {
                returnMap.remove("userInfo");
                paramData.remove("userSeCd");
                returnMap.put("itemId", paramData.get("itemId"));
                returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "해당 리워드종류로 구매할수 없습니다.");
                return returnMap;
            }
            //if(htBlnc.compareTo(htNtslAmt) > -1 || "T".equals(userSeCd)) { //가진 하트가 같거나 커야하거나, 교사일때 구매가능
            if(htBlnc.compareTo(htNtslAmt) > -1) { //가진 하트가 같거나 커야함
                if(shopMapper.insertSpPrchsHist(insertPrchsInfo) > 0) { //상점구매이력 구매내역 저장
                    LinkedHashMap<Object, Object> ntslGdsAmtSumMap = AidtCommonUtil.filterToMap(ntslGdsAmtSumForm, shopMapper.findSpPrchsHistHtStSum(paramData)); //상점구매이력에서 하트, 스타 sum 구하기
                    paramData.put("ntslGdsAmtSum", ntslGdsAmtSumMap.get("ntslGdsAmtSum"));

                    //sp_prchs_info 존재여부 확인
                    Map<String, Object> chkSpPrchsInfo = shopMapper.selectChkPrchsInfo(insertPrchsInfo);
                    if(MapUtils.isEmpty(chkSpPrchsInfo)) {
                        //sp_prchs_info insert
                        insertPrchsInfo.put("htPrchsAmt", htNtslAmt);
                        insertPrchsInfo.put("stPrchsAmt", 0);
                        insertPrchsInfo.put("rprsGdsAnct", null);

                        shopMapper.insertSpPrchsInfo(insertPrchsInfo);

                    } else {
                        //sp_prchs_info update
                        shopMapper.updateSpPrchsInfoHt(paramData); //sp_prchs_info의 하트구매금액 업데이트
                    }

                    //학생일때
                    //if("S".equals(paramData.get("userSeCd").toString())) {
                        //1.리워드획득이력에 리워드사용금액정보를 insert(학생일때)
                        //TODO::('se_cd', 'menu_se_cd','sve_se_cd')에 대한 인서트 정보가 없음
                        paramData.put("seCd", "2"); //구분 1:획득, 2:사용
                        paramData.put("rwdAmt", 0); //사용일때는 리워드금액이 0
                        paramData.put("rwdUseAmt", htNtslAmt); //구매할 하트값
                        paramData.put("trgtId",paramData.get("itemId")); //대상ID
                        paramData.put("menuSeCd", "6"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:자기주도학습, 5:게임, 6:상점
                        paramData.put("sveSeCd", "10"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습,  7:오답노트, 8:게임, 9:우수답안, 10:아이템구매
                        //2. 리워드획득정보에서 구매금액을 리워드잔액에서 차감한다.
                        stntRewardService.useReward(paramData); //리워드사용 메소드 호출

                   //}

                }
            } else { //잔액부족
                returnMap.remove("userInfo");
                paramData.remove("userSeCd");
                returnMap.put("itemId", paramData.get("itemId"));
                returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "리워드 잔액이 부족합니다.");
                return returnMap;
            }
        } else if("2".equals(paramData.get("rwdSeCd").toString())){ //스타가격이므로 스타로 구매
            //if(BigDecimal.ZERO.compareTo(stNtslAmt) == 0 && !"T".equals(userSeCd)) {
            if(BigDecimal.ZERO.compareTo(stNtslAmt) == 0) {
                returnMap.remove("userInfo");
                paramData.remove("userSeCd");
                returnMap.put("itemId", paramData.get("itemId"));
                returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "해당 재화로 구매할수 없습니다.");
                return returnMap;
            }
            //if(stBlnc.compareTo(stNtslAmt) > -1 || "T".equals(userSeCd)) { //가진 스타가 같거나 커야한다, 또는 교사일때
            if(stBlnc.compareTo(stNtslAmt) > -1) { //가진 스타가 같거나 커야한다
                if(shopMapper.insertSpPrchsHist(insertPrchsInfo) > 0) { //상점구매이력 구매내역 저장
                    LinkedHashMap<Object, Object> ntslGdsAmtSumMap = AidtCommonUtil.filterToMap(ntslGdsAmtSumForm, shopMapper.findSpPrchsHistHtStSum(paramData)); //상점구매이력에서 하트, 스타 sum 구하기

                    paramData.put("ntslGdsAmtSum", ntslGdsAmtSumMap.get("ntslGdsAmtSum"));

                    //sp_prchs_info 존재여부 확인
                    Map<String, Object> chkSpPrchsInfo = shopMapper.selectChkPrchsInfo(insertPrchsInfo);
                    if(MapUtils.isEmpty(chkSpPrchsInfo)) {
                        //sp_prchs_info insert
                        insertPrchsInfo.put("htPrchsAmt", 0);
                        insertPrchsInfo.put("stPrchsAmt", stNtslAmt);
                        insertPrchsInfo.put("rprsGdsAnct", null);

                        shopMapper.insertSpPrchsInfo(insertPrchsInfo);

                    } else {
                        //sp_prchs_info update
                        shopMapper.updateSpPrchsInfoHt(paramData); //sp_prchs_info의 스타구매금액 업데이트
                    }

                    //학생일때
                    //if("S".equals(paramData.get("userSeCd").toString())) {
                        //1.리워드획득이력에 리워드사용금액정보를 insert(학생일때)
                        //TODO::('se_cd', 'menu_se_cd','sve_se_cd')에 대한 인서트 정보가 없음
                        paramData.put("seCd", "2"); //구분 1:획득, 2:사용
                        paramData.put("trgtId",paramData.get("itemId")); //대상ID
                        paramData.put("rwdAmt", 0); //사용일때는 리워드금액이 0
                        paramData.put("rwdUseAmt", stNtslAmt); //구매할스타값
                        paramData.put("menuSeCd", "6"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:자기주도학습, 5:게임, 6:상점
                        paramData.put("sveSeCd", "10"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습,  7:오답노트, 8:게임, 9:우수답안, 10:아이템구매
                        //2.리워드획득정보에서 구매금액을 리워드잔액에서 차감한다. (학생일때만 처리)
                        stntRewardService.useReward(paramData); //리워드사용 메소드 호출
                    //}

                }
            } else { //잔액부족
                returnMap.remove("userInfo");
                paramData.remove("userSeCd");
                returnMap.put("itemId", paramData.get("itemId"));
                returnMap.put("rwdSeCd", paramData.get("rwdSeCd"));
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "리워드 잔액이 부족합니다.");
                return returnMap;
            }
        }

        //UserInfo 셋팅
        findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값
        returnMap.put("userInfo", findShopUserInfo);
        //유저유형 셋팅
        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // 1. 상점 구분 returnMap
        //ShopitemInfo셋팅
        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoP = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoP(paramData));
            returnMap.put("shopItemList", findShopItemInfoP); //shopItemList
        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoS = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoS(paramData));
            returnMap.put("shopItemList", findShopItemInfoS); //shopItemList
        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoG = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoG(paramData));
            returnMap.put("shopItemList", findShopItemInfoG); //shopItemList
        } else {
            returnMap.put("shopItemList", ""); //shopItemList
        }

        paramData.remove("seCd");
        paramData.remove("trgtId");
        paramData.remove("rwdAmt");
        paramData.remove("rwdUseAmt");
        paramData.remove("menuSeCd");
        paramData.remove("sveSeCd");
        paramData.remove("rwdHistId");
        paramData.remove("userSeCd");
        paramData.remove("ntslGdsAmtSum");

        return returnMap;
    }

    /**
     * (샵).아이템 사용 요청
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> getShopUseItem(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        if ("0".equals(paramData.get("itemId").toString())) { //아이템 default
            paramData.put("itemId", 1);
        }

        int count = shopMapper.selectSpPrchsInfoCheck(paramData);
        if(count == 0 && (int) paramData.get("itemId") != 1) {
            return null;
        }

        List<String> findShopUserInfoForm = Arrays.asList("userId","flnm", "userSeCd", "rprsGdsAnct", "htBlnc", "stBlnc");
        List<String> shopItemProfileInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
        List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");

        //sp_prchs_info의 rprs_gds_id 업데이트
        int updateResult = shopMapper.updateSpPrchsInfoRgi(paramData);

        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값

        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));

        //사용중인 아이템 프로필, 게임, 스킨 정보
        //ItemProfile Service
        LinkedHashMap<Object, Object> shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfo(paramData));
        if(MapUtils.isEmpty(shopItemProfileInfoTemp)) {
            shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfoDefault(paramData));
        }
        //ItemGame Service
        LinkedHashMap<Object, Object> shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfo(paramData));
        if(MapUtils.isEmpty(shopItemGameInfoTemp)) {
            shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfoDefault(paramData));
        }
        //ItemSkin Service
        LinkedHashMap<Object, Object> shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfo(paramData));
        if(MapUtils.isEmpty(shopItemSkinInfoTemp)) {
            shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfoDefault(paramData));
        }

        //UserInfo 객체 끝에 추가
        findShopUserInfo.put("itemProfileInfo", shopItemProfileInfoTemp);
        findShopUserInfo.put("itemGameInfo", shopItemGameInfoTemp);
        findShopUserInfo.put("itemSkinInfo", shopItemSkinInfoTemp);

        returnMap.put("userInfo", findShopUserInfo); //1. userInfo returnMap 담기
        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // 2. 상점 구분 returnMap

        //3 : ShopItemInfo
        List<String> shopItemInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");
       // List<String> shopItemInfoFormP = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "pfAllImg196", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");

        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoP = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoP(paramData));
            returnMap.put("shopItemList", findShopItemInfoP); //shopItemList

        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoS = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoS(paramData));
            returnMap.put("shopItemList", findShopItemInfoS); //shopItemList

        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoG = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoG(paramData));
            returnMap.put("shopItemList", findShopItemInfoG); //shopItemList
        } else {
            returnMap.put("shopItemList", ""); //shopItemList
        }

        paramData.remove("userSeCd");

        return returnMap;
    }

    /**
     * (샵).마이룸
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getShopMyroom(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> classMateItem = Arrays.asList("userId", "flnm", "userSeCd", "rprsGdsAnct", "itemProfileInfo", "stdSttsCd");

        //학습상태 입장(1:학습중), 퇴장(2:학습완료) --> 1(로그인) 값이 없을 때(left join으로 null)는 로그아웃이므로 3, conn_status에 3은없음
        List<LinkedHashMap<Object, Object>> classMateList = AidtCommonUtil.filterToList(classMateItem, shopMapper.selectMyroomClassMate(paramData));

        for(int i=0;i<classMateList.size(); i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Object tempObj = objectMapper.readValue(classMateList.get(i).get("itemProfileInfo").toString(), Object.class);
                classMateList.get(i).put("itemProfileInfo", tempObj);
            } catch (NullPointerException e) {
                log.error("parseItemProfileInfo - NullPointerException:", e);
                CustomLokiLog.errorLog(e);
            } catch (IllegalArgumentException e) {
                log.error("parseItemProfileInfo - IllegalArgumentException:", e);
                CustomLokiLog.errorLog(e);
            } catch (JsonProcessingException e) {
                log.error("parseItemProfileInfo - JsonProcessingException:", e);
                CustomLokiLog.errorLog(e);
            } catch (RuntimeException e) {
                log.error("parseItemProfileInfo - RuntimeException:", e);
                CustomLokiLog.errorLog(e);
            } catch (Exception e) {
                log.error("parseItemProfileInfo - Exception:", e);
                CustomLokiLog.errorLog(e);
            }
        }

        List<LinkedHashMap<Object, Object>> resultList = new ArrayList<>();

        returnMap.put("classmateList", classMateList);
        return returnMap;
    }


    /**
     * (샵).상품 위치 변경
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> getShopChangeItemInv(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        if ("0".equals(paramData.get("prchsId").toString())) { //아이템 default
            returnMap.put("invSeCd", paramData.get("invSeCd"));
            returnMap.put("prchsId", paramData.get("prchsId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "요청하신 상품은 기본으로 제공하는 상품이므로 보관함으로 이동할 수 없습니다.");
            return returnMap;
        }

        List<String> findShopUserInfoForm = Arrays.asList("userId","flnm", "userSeCd", "rprsGdsAnct", "htBlnc", "stBlnc");
        List<String> shopItemProfileInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
        List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");

        //sp_prchs_info의 hist_inv_se_cd 업데이트(인벤토리의 1:구매목록, 2:보관함 업데이트)
        int updateResult = shopMapper.updateSpPrchsHistInvSeCd(paramData);

        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값

        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));

        //사용중인 아이템 프로필, 게임, 스킨 정보
        //ItemProfile Service
        LinkedHashMap<Object, Object> shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfo(paramData));
        if(MapUtils.isEmpty(shopItemProfileInfoTemp)) {
            shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, shopMapper.findShopItemProfileInfoDefault(paramData));
        }
        //ItemGame Service
        LinkedHashMap<Object, Object> shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfo(paramData));
        if(MapUtils.isEmpty(shopItemGameInfoTemp)) {
            shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, shopMapper.findShopItemGameInfoDefault(paramData));
        }
        //ItemSkin Service
        LinkedHashMap<Object, Object> shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfo(paramData));
        if(MapUtils.isEmpty(shopItemSkinInfoTemp)) {
            shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, shopMapper.findShopItemSkinInfoDefault(paramData));
        }

        //UserInfo 객체 끝에 추가
        findShopUserInfo.put("itemProfileInfo", shopItemProfileInfoTemp);
        findShopUserInfo.put("itemGameInfo", shopItemGameInfoTemp);
        findShopUserInfo.put("itemSkinInfo", shopItemSkinInfoTemp);

        returnMap.put("userInfo", findShopUserInfo); //1. userInfo returnMap 담기
        returnMap.put("prchsGdsSeCd", paramData.get("prchsGdsSeCd")); // 2. 상점 구분 returnMap

        //3 : ShopItemInfo
        List<String> shopItemInfoForm = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");
     //   List<String> shopItemInfoFormP = Arrays.asList("id", "kornImgNm", "engImgNm", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "pfAllImg196", "spIndctImg", "theme", "ownYn", "prchsId", "invSeCd");

        if(("P").equals(paramData.get("prchsGdsSeCd").toString())) { //프로필일 경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoP = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoP(paramData));
            returnMap.put("shopItemList", findShopItemInfoP); //shopItemList

        } else if(("S").equals(paramData.get("prchsGdsSeCd").toString())) { //스킨일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoS = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoS(paramData));
            returnMap.put("shopItemList", findShopItemInfoS); //shopItemList

        } else if(("G").equals(paramData.get("prchsGdsSeCd").toString())) { //게임일경우
            List<LinkedHashMap<Object, Object>> findShopItemInfoG = AidtCommonUtil.filterToList(shopItemInfoForm, shopMapper.findShopItemInfoG(paramData));
            returnMap.put("shopItemList", findShopItemInfoG); //shopItemList
        } else {
            returnMap.put("shopItemList", ""); //shopItemList
        }

        paramData.remove("userSeCd");

        return returnMap;
    }

    /**
     * (샵).보상지급
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> saveReward(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String tabId = MapUtils.getString(paramData, "tabId");
        paramData.put("tabId", StringUtils.isEmpty(tabId) ? "0" : tabId);

        //보상 리워드 list map
        String rewardListStr = paramData.get("rewardList").toString();
        List<Map<String, Object>> rewardLists = AidtCommonUtil.objectStringToListMap(rewardListStr);

        List<Map<String, Object>> createRewardResult = new ArrayList<>();
        for(int i=0; i<rewardLists.size(); i++) {

            rewardLists.get(i).put("seCd", "1"); //구분 1:획득, 2:사용
            rewardLists.get(i).put("rwdUseAmt", 0); //지급일때는 0
            rewardLists.get(i).put("trgtId", paramData.get("tabId")); //대상ID
            rewardLists.get(i).put("menuSeCd", "5"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:자기주도학습, 5:게임
            rewardLists.get(i).put("sveSeCd", "8"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트, 8:게임
            rewardLists.get(i).put("rwdSeCd", "2"); //리워드구분 2:스타
            rewardLists.get(i).put("textbkId", 0);

            Map<String, Object> rewardResult = stntRewardService.createReward(rewardLists.get(i));

            createRewardResult.add(i,rewardResult);
        }

        returnMap.put("resultList",createRewardResult); //TODO:resultList이름 바꾸기

        return returnMap;
    }

    /**
     * (샵).문제정보
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object getShopMdulInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> findShopTabSetsInfoForm = Arrays.asList("tabId","tabNm", "setsId", "setsNm");
        List<String> findShopSetsAtcInfoForm = Arrays.asList("gmIemId","name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen", "isEditable");

        LinkedHashMap<Object, Object> findShopTabSetsInfo = AidtCommonUtil.filterToMap(findShopTabSetsInfoForm, shopMapper.findShopTabSetsInfo(paramData));
        List<LinkedHashMap<Object, Object>> findShopSetsAtcInfo = AidtCommonUtil.filterToList(findShopSetsAtcInfoForm, shopMapper.findShopSetsAtcInfo(paramData));

        if(MapUtils.isEmpty(findShopTabSetsInfo)) {
            returnMap.put("tabId", paramData.get("tabId"));
            returnMap.put("tabNm", "");
            returnMap.put("setsId", "");
            returnMap.put("setsNm", "");
            returnMap.put("eamExmNum", 0);
            returnMap.put("gmIemList", findShopSetsAtcInfo);
        } else {
            returnMap.put("tabId", paramData.get("tabId"));
            returnMap.put("tabNm", findShopTabSetsInfo.get("tabNm"));
            returnMap.put("setsId", findShopTabSetsInfo.get("setsId"));
            returnMap.put("setsNm", findShopTabSetsInfo.get("setsNm"));

            paramData.put("setsId", findShopTabSetsInfo.get("setsId"));
            findShopSetsAtcInfo = AidtCommonUtil.filterToList(findShopSetsAtcInfoForm, shopMapper.findShopSetsAtcInfo(paramData));
            returnMap.put("eamExmNum", findShopSetsAtcInfo.size());
            returnMap.put("gmIemList", findShopSetsAtcInfo);
        }

        paramData.remove("setsId");

        return returnMap;
    }

    /**
     * (샵).유저상태메시지
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> saveUserMessage(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);
        //파라미터 체크
        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("claId") == null || ("").equals(paramData.get("claId"))) {
            returnMap.put("resultMsg", "claId를 입력해주세요");
            return returnMap;
        }
//        if (paramData.get("rprsGdsAnct") == null || ("").equals(paramData.get("rprsGdsAnct"))) {
//            returnMap.put("resultMsg", "rprsGdsAnct를 입력해주세요");
//            return returnMap;
//        }

        //UserInfo
        paramData.put("prchsGdsSeCd" , "P");
        List<String> findShopUserInfoForm = Arrays.asList("userId","flnm", "userSeCd", "rprsGdsAnct", "htBlnc", "stBlnc");
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값

        if(MapUtils.isEmpty(findShopUserInfo)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "유저 정보가 없습니다.");
            return returnMap;
        }

        //해당 아이템이 없으면 sp_prchs_info 체크
        Map<String, Object> selectChkPrchsInfo = shopMapper.selectChkPrchsInfo(paramData);
        Map<String, Object> insertParam = new HashMap<>();
        insertParam.put("userId", paramData.get("userId"));
        insertParam.put("userSeCd", findShopUserInfo.get("userSeCd"));
        insertParam.put("claId", paramData.get("claId"));
        insertParam.put("prchsGdsSeCd", "P");
        insertParam.put("htPrchsAmt", 0);
        insertParam.put("stPrchsAmt", 0);
        insertParam.put("prchsGdsId", null);
        insertParam.put("rprsGdsAnct", paramData.get("rprsGdsAnct"));

        if(MapUtils.isEmpty(selectChkPrchsInfo)){
            //sp_prchs_info insert
            int insertSpPrchsInfo = shopMapper.insertSpPrchsInfo(insertParam);
        } else {
            //sp_prchs_info update
            int updateSpPrchsInfo =shopMapper.updateSpPrchsInfoUserMsg(insertParam);
        }

        findShopUserInfo = AidtCommonUtil.filterToMap(findShopUserInfoForm, shopMapper.findShopUserInfo(paramData)); //Response(UserInfo)에 있는 값
        returnMap.put("userInfo", findShopUserInfo);
        returnMap.remove("resultOk");
        returnMap.remove("resultMsg");
        paramData.remove("prchsGdsSeCd");
        return returnMap;
    }

    public Map<String, Object> insertSpPrchsHist() throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        //insert대상자의 조건때문에 Hist테이블에 먼저 insert후 info 테이블에 insert 한다.
        int spInfoCnt = 0;
        int skInfoCnt = 0;
        int gmInfoCnt = 0;

        returnMap.put("btchExcnRsltCnt", spInfoCnt + skInfoCnt + gmInfoCnt);
        returnMap.put("resultOk", true);
        try {
            if (shopMapper.insertSpPrchsHistBatch() > 0) {
                spInfoCnt = shopMapper.insertSpPrchsInfoBatch();
                log.info("ShopPrchsHistBatchJob > makePrchsHistBatch() > 프로필 :" + spInfoCnt + " 명");
            }

            if (shopMapper.insertSkPrchsHistBatch() > 0) {
                skInfoCnt = shopMapper.insertSkPrchsInfoBatch();
                log.info("ShopPrchsHistBatchJob > makePrchsHistBatch() > 스킨 :" + skInfoCnt + " 명");
            }

            if (shopMapper.insertGmPrchsHistBatch() > 0) {
                gmInfoCnt = shopMapper.insertGmPrchsInfoBatch();
                log.info("ShopPrchsHistBatchJob > makePrchsHistBatch() > 게임 :" + gmInfoCnt + " 명");
            }
        } catch (NullPointerException e) {
            log.error("makePrchsHistBatch - NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("makePrchsHistBatch - IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
            throw e;
        } catch (DataAccessException e) {
            log.error("makePrchsHistBatch - DataAccessException:", e);
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
            throw e;
        } catch (RuntimeException e) {
            log.error("makePrchsHistBatch - RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
            throw e;
        } catch (Exception e) {
            log.error("makePrchsHistBatch - Exception:", e);
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));
            throw e;
        }

        returnMap.put("btchExcnRsltCnt", spInfoCnt + skInfoCnt + gmInfoCnt);
        log.info("ShopPrchsHistBatchJob > makePrchsHistBatch() > END");
        return returnMap;

    }
}
