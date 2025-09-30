package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.repository.dto.TabInfoDTO;
import com.visang.aidt.lms.api.repository.dto.TcCurriculumDTO;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StressService {
    @Value("${app.shop.filePathRoot}")
    private String FILE_PATH_ROOT;

    private final TextbookRepository2 textbookRepository;
    private final TcLastlessonRepository2 tcLastlessonRepository;
    private final TcCurriculumRepository2 tcCurriculumRepository;
    private final UserRepository2 userRepository;
    private final StdtRegInfoRepository2 stdtRegInfoRepository;
    private final TcRegInfoRepository2 tcRegInfoRepository;
    private final TcClaInfoRepository2 tcClaInfoRepository;
    private final TcClaMbInfoRepository2 tcClaMbInfoRepository;
    private final SchoolRepository2 schoolRepository;
    private final CntnLogRepository2 cntnLogRepository;
    private final StressMapper stressMapper;

    private final StntMdulVocalScrService stntMdulVocalScrService;

    @Transactional(readOnly = true)
    public Object findStntMdulQstnView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> stntMdulQstnViewItem = Arrays.asList("detailId", "dtaIemId", "subId", "mrkTy", "eakSttsCd", "eakAt", "mrkCpAt", "eakStDt", "eakEdDt", "subMitAnw", "subMitAnwUrl", "errata", "reIdfCnt", "anwChgCnt", "smExmAt", "textbkDtaIemId", "reExmCnt", "stdFdbAt", "stdFdbDc", "exltAnwAt", "fdbExpAt", "hdwrtCn","bmkYn","bmkId","bmkModuleId","bmkSubId","bmkTchYn","bmkTchId","bmkTchModuleId","bmkTchSubId","noteYn","actYn","preCheckAt","hntUseAt");

        var mdulList = AidtCommonUtil.filterToList(stntMdulQstnViewItem, stressMapper.findStntMdulQstnView(paramData));

        List<Map> histInfoList = null;
        List<Map> otherList = null;
        List<Map> slfSetList = null;
        List<Map> evlAtMap = null;
        List<Map<String, Object>> perSetList = null;

        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(mdulList)) {
            histInfoList = stressMapper.findStntMdulQstnViewHist(mdulList);
            otherList = stressMapper.findStntMdulQstnViewOtherList(mdulList);

            Map<String, Object> slfPerMap = new HashMap<>();
            slfPerMap.put("stntId", paramData.get("userId"));
            slfPerMap.put("gbCd", 1);
            slfPerMap.put("textbkId", paramData.get("textbkId"));
            slfPerMap.put("tabId", paramData.get("tabId"));
            slfPerMap.put("dtaIemId", "mdulList");
            slfPerMap.put("mdulList", mdulList);
            slfSetList = stressMapper.findTchSlfperEvlSlfSetList(slfPerMap);
            perSetList = stressMapper.findTchSlfperEvlPerSetList(slfPerMap);
            evlAtMap = stressMapper.findMdulSlfPerEvlAt(mdulList);
        }

        // 동료
        List<Map> perInfoList = new ArrayList<>();
        List<Map> perInfoIdList = new ArrayList<>();
        List<Map> PerResultList = new ArrayList<>();
        List<Map<String, Object>> templtList = new ArrayList<>();

        Map<String, Object> perInfoMap = new HashMap<>();
        Map<String, Object> perInfoIdMap = new HashMap<>();
        Map<String, Object> PerResultMap = new HashMap<>();
        Map<String, Object> templtMap = new HashMap<>();

        // perSetList null 체크 추가 - CSAP 보안 취약점 수정
        if (perSetList != null) {
            for (Map<String, Object> map : perSetList) {
                if (map != null && org.apache.commons.lang3.ObjectUtils.isNotEmpty(MapUtils.getString(map, "perApraserId"))) {
                perInfoMap = new HashMap<>();
                perInfoMap.put("id", MapUtils.getIntValue(map, "id"));
                perInfoMap.put("perApraserId", MapUtils.getString(map, "perApraserId"));
                perInfoMap.put("flnm", MapUtils.getString(map, "flnm"));
                perInfoMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                perInfoMap.put("subId", MapUtils.getIntValue(map, "subId"));
                perInfoMap.put("stExposAt", MapUtils.getString(map, "stExposAt"));
                perInfoList.add(perInfoMap);

                PerResultMap = new HashMap<>();
                PerResultMap.put("perApraserId", MapUtils.getString(map, "perApraserId"));
                PerResultMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                PerResultMap.put("subId", MapUtils.getIntValue(map, "subId"));
                PerResultMap.put("slfPerEvlDetailId", MapUtils.getIntValue(map, "slfPerEvlDetailId"));
                PerResultMap.put("tmpltItmSeq", MapUtils.getIntValue(map, "tmpltItmSeq"));
                PerResultMap.put("evlDmi", MapUtils.getString(map, "evlDmi"));
                PerResultMap.put("evlIem", MapUtils.getString(map, "evlIem"));
                PerResultMap.put("evlStdrCd", MapUtils.getString(map, "evlStdrCd"));
                PerResultMap.put("evlStdrDc", MapUtils.getString(map, "evlStdrDc"));
                PerResultMap.put("evlResult", MapUtils.getString(map, "evlResult"));
                PerResultMap.put("evlAsw", MapUtils.getString(map, "evlAsw"));
                PerResultList.add(PerResultMap);
            } else {
                perInfoIdMap = new HashMap<>();
                perInfoIdMap.put("id", MapUtils.getIntValue(map, "id"));
                perInfoIdMap.put("stExposAt", MapUtils.getIntValue(map, "stExposAt"));
                perInfoIdMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                perInfoIdMap.put("subId", MapUtils.getIntValue(map, "subId"));
                perInfoIdList.add(perInfoIdMap);
            }

                templtMap = new HashMap<>();
                templtMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                templtMap.put("subId", MapUtils.getIntValue(map, "subId"));
                templtMap.put("slfPerEvlDetailId", MapUtils.getIntValue(map, "slfPerEvlDetailId"));
                templtMap.put("tmpltItmSeq", MapUtils.getIntValue(map, "tmpltItmSeq"));
                templtMap.put("evlDmi", MapUtils.getString(map, "evlDmi"));
                templtMap.put("evlIem", MapUtils.getString(map, "evlIem"));
                templtMap.put("evlStdrCd", MapUtils.getString(map, "evlStdrCd"));
                templtMap.put("evlStdrDc", MapUtils.getString(map, "evlStdrDc"));
                templtList.add(templtMap);
            }
        }
        perInfoList = perInfoList.stream().distinct().collect(Collectors.toList());

        for (Map s : perInfoList) {
            s.put("PerResult", CollectionUtils.emptyIfNull(PerResultList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"perApraserId"), MapUtils.getString(g,"perApraserId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"moduleId"), MapUtils.getString(g,"moduleId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        g.remove("perApraserId");
                        g.remove("moduleId");
                        g.remove("subId");
                        return g;
                    }).toList()
            );
        }

        perInfoIdList = perInfoIdList.stream().distinct().collect(Collectors.toList());

        templtList = templtList.stream().distinct().collect(Collectors.toList());

        templtList.sort(
                Comparator.comparing((Map<String, Object> map) -> MapUtils.getIntValue(map, "tmpltItmSeq"))
        );

        for (LinkedHashMap<Object, Object> s : mdulList) {
            s.put("histInfoList", CollectionUtils.emptyIfNull(histInfoList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"dtaResultDetailId")))
                    .map(g -> {
                        g.remove("dtaResultDetailId");
                        return g;
                    }).toList()
            );

            // 자기 동료 평가
            var slfPerMap = new LinkedHashMap<>();
            slfPerMap.put("stntId", paramData.get("userId"));
            slfPerMap.put("selInfoId", 0);
            slfPerMap.put("perInfoId", 0);
            slfPerMap.put("slfStExposAt", null);
            slfPerMap.put("perStExposAt", null);

            // 자기
            List<Map> slResult = CollectionUtils.emptyIfNull(slfSetList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        slfPerMap.put("selInfoId", MapUtils.getIntValue(g, "id", 0));
                        slfPerMap.put("slfStExposAt", MapUtils.getString(g, "stExposAt"));
                        g.remove("id");
                        g.remove("moduleId");
                        g.remove("subId");
                        g.remove("stExposAt");
                        return g;
                    }).toList();

            // 동료
            List<Map> slfPerInfoList = CollectionUtils.emptyIfNull(perInfoList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        slfPerMap.put("perInfoId", MapUtils.getIntValue(g, "id", 0));
                        slfPerMap.put("perStExposAt", MapUtils.getString(g, "stExposAt"));
                        g.remove("id");
                        g.remove("moduleId");
                        g.remove("subId");
                        g.remove("stExposAt");
                        return g;
                    }).toList();

            if (MapUtils.getIntValue(slfPerMap, "perInfoId") == 0) {
                for (Map map : perInfoIdList) {
                    if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "moduleId"))
                            && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "subId"))
                    ) {
                        slfPerMap.put("perInfoId", MapUtils.getIntValue(map, "id", 0));
                        slfPerMap.put("perStExposAt", MapUtils.getString(map,"stExposAt"));
                    }
                }
            }

            //templt
            List<Map<String, Object>> slfTempltList = CollectionUtils.emptyIfNull(templtList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        g.remove("moduleId");
                        g.remove("subId");
                        return g;
                    }).toList();


            // null 체크 로직 추가
            if (evlAtMap != null) {
                for (Map map : evlAtMap) {
                    if (map != null &&
                            MapUtils.getString(s, "dtaIemId") != null &&
                            MapUtils.getString(map, "moduleId") != null &&
                            MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "moduleId"))) {
                        slfPerMap.put("mdulSlfPerEvlAt", MapUtils.getString(map, "mdulSlfPerEvlAt"));
                    }
                }
            }

            slfPerMap.put("slResult", slResult);
            slfPerMap.put("perInfoList", slfPerInfoList);
            slfPerMap.put("templtList", slfTempltList);
            slfPerMap.put("slfNum", "");
            slfPerMap.put("slfTotNum", slResult.size());
            slfPerMap.put("perNum", "");
            slfPerMap.put("perTotNum", slfPerInfoList.size());

            s.put("slfPerList", slfPerMap);

            s.put("otherList", CollectionUtils.emptyIfNull(otherList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"srcDetailId")))
                    .map(g -> {
                        g.remove("srcDetailId");
                        return g;
                    }).toList()
            );
        }
        returnMap.put("mdulList", mdulList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchSlfperEvlSlfView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = stressMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = stressMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        List<Map> slList = new ArrayList<>();
        List<Map> perInfoList = new ArrayList<>();
        List<Map> perInfoTempList = new ArrayList<>();
        List<Map> templtList = new ArrayList<>();

        int selInfoIdMapId = 0;
        if (selInfoIdMap != null && !selInfoIdMap.isEmpty()) {
            slList = stressMapper.findTchSlfperEvlSlfViewSl(paramData);
            selInfoIdMapId = MapUtils.getInteger(selInfoIdMap, "id");
        }

        int perInfoIdMapId = 0;
        if (perInfoIdMap != null && !perInfoIdMap.isEmpty()) {
            perInfoTempList = stressMapper.findTchSlfperEvlSlfViewPerInfo(paramData);
            List<Map> perResultInfoList = stressMapper.findTchSlfperEvlSlfView_perResultInfoList(paramData);

            perInfoList = CollectionUtils.emptyIfNull(perInfoTempList).stream()
            .map(s->{
                List<Map> perResultList = CollectionUtils.emptyIfNull(perResultInfoList).stream()
                    .filter(t -> StringUtils.equals(MapUtils.getString(s,"perApraserId"), MapUtils.getString(t,"perApraserId")))
                    .map(t -> {
                        t.remove("perApraserId");
                        return t;
                    }).toList();
                s.put("PerResult", perResultList);
                return s;
            }).toList();

            templtList = stressMapper.findSTchSlfperEvlSlfViewTemplt(paramData);
            perInfoIdMapId = MapUtils.getInteger(perInfoIdMap, "id");
        }

        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        returnMap.put("stntId", paramData.get("stntId"));
        returnMap.put("selInfoId", selInfoIdMapId);
        returnMap.put("perInfoId", perInfoIdMapId);

        returnMap.put("slResult", slList);
        returnMap.put("perInfoList", perInfoList);
        returnMap.put("templtList", templtList);

        returnMap.put("slfNum", "");
        returnMap.put("slfTotNum", slList.size());
        returnMap.put("perNum", "");
        returnMap.put("perTotNum", perInfoList.size());

        returnMap.put("slfStExposAt", MapUtils.getString(selInfoIdMap, "stExposAt"));
        returnMap.put("perStExposAt", MapUtils.getString(perInfoIdMap, "stExposAt"));

        var evlAtMap = stressMapper.findMdulSlfPerEvlAt(paramData);

        returnMap.put("mdulSlfPerEvlAt", MapUtils.getString(evlAtMap, "mdulSlfPerEvlAt"));



        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object stTextbookInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        Map<String, Object> textbookInfo = new HashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");
        returnMap.put("textbookInfo", textbookInfo);

        String userId = (String) paramData.getOrDefault("userId", "");

        User2 user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.S.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);

        Map<String, Object> stdtRegInfo = stressMapper.getStdtRegInfo(data);

        String standbyCd = "0";
        long claIdx = -1;
        if (stdtRegInfo != null) {
            data.put("claId", stdtRegInfo.getOrDefault("claId", ""));
            data.put("tcId", stdtRegInfo.getOrDefault("tcId", ""));
            claIdx = (long) stdtRegInfo.getOrDefault("claIdx", -1);
            Map<String, Object> stTextbookInfo = this.getStTextbookInfo(data);
            if (stTextbookInfo != null) {
                textbookInfo.put("textbkId", stTextbookInfo.getOrDefault("textbkId", -1));
                textbookInfo.put("textbkIdxId", stTextbookInfo.getOrDefault("textbkIdxId", -1));
                textbookInfo.put("textbkCrltnId", stTextbookInfo.getOrDefault("textbkCrltnId", -1));
                textbookInfo.put("textbkNm", stTextbookInfo.getOrDefault("textbkNm", ""));
            } else {
                standbyCd = "1";
            }
            returnMap.put("textbookInfo", textbookInfo);
        } else {
            standbyCd = "2";
        }
        returnMap.put("claIdx", claIdx);
        returnMap.put("standbyCd", standbyCd);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findShopUserInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> shopUserInfo = Arrays.asList("userId","flnm", "userSeCd","rprsGdsAnct", "htBlnc", "stBlnc");
        List<String> shopItemProfileInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "pfAllImg196", "pfCtImg", "pfUiImg", "mrSdImg", "mrCpImg", "mrEpImg", "tbStImg", "tbCpImg", "tbEpImg", "cpMkImg", "rmTitImg", "rmWalImg", "dcLfImg", "dcRtImg", "dcDrImg", "dcDrAniImg");
        List<String> shopItemGameInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg");
        List<String> shopItemSkinInfo = Arrays.asList("id", "kornImgNm", "engImgNm", "tme", "htNtslAmt", "stNtslAmt", "gdsIndctImg", "spIndctImg", "loadImg", "almImg", "tstStartImg", "qzImg", "tstWaitImg", "scrMvImg", "rwdImg", "lkAheadImg", "mkWaitImg", "hiImg", "tnkImg", "stdImg","uiLoading", "uiAlarm", "uiTestStart", "uiQuiz", "uiTestWait", "uiScreenMove", "uiReward", "uiLookAhead", "uiMakeWait", "uiHi", "uiThankyou", "uiStudy");

        //UserInfo
        LinkedHashMap<Object, Object> findShopUserInfo = AidtCommonUtil.filterToMap(shopUserInfo, stressMapper.findShopUserInfo(paramData));

        //유저유형셋팅
        paramData.put("userSeCd", findShopUserInfo.get("userSeCd"));

        //ItemProfile Service
        LinkedHashMap<Object, Object> shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, stressMapper.findShopItemProfileInfo(paramData));
        if(MapUtils.isEmpty(shopItemProfileInfoTemp)) {
            shopItemProfileInfoTemp = AidtCommonUtil.filterToMap(shopItemProfileInfo, stressMapper.findShopItemProfileInfoDefault(paramData));
        }
        //ItemGame Service
        LinkedHashMap<Object, Object> shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, stressMapper.findShopItemGameInfo(paramData));
        if(MapUtils.isEmpty(shopItemGameInfoTemp)) {
            shopItemGameInfoTemp = AidtCommonUtil.filterToMap(shopItemGameInfo, stressMapper.findShopItemGameInfoDefault(paramData));
        }
        //ItemSkin Service
        LinkedHashMap<Object, Object> shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, stressMapper.findShopItemSkinInfo(paramData));
        if(MapUtils.isEmpty(shopItemSkinInfoTemp)) {
            shopItemSkinInfoTemp = AidtCommonUtil.filterToMap(shopItemSkinInfo, stressMapper.findShopItemSkinInfoDefault(paramData));
        }


        //1
        findShopUserInfo.put("itemProfileInfo", shopItemProfileInfoTemp);
        findShopUserInfo.put("itemGameInfo", shopItemGameInfoTemp);
        findShopUserInfo.put("itemSkinInfo", shopItemSkinInfoTemp);

        //2
        returnMap.put("userInfo", findShopUserInfo);
        returnMap.put("filePathRoot", FILE_PATH_ROOT); //상점 아이템 파일 서버 경로-하드코딩

        paramData.remove("userSeCd");
        return returnMap;
    }

    @Transactional(readOnly = true)
     public Object findStntSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
/* 검색 조건 제외 (module_subm_at)
        String moduleSubmAt = "N";
        String moduleId = MapUtils.getString(paramData, "moduleId");
        if (ObjectUtils.isNotEmpty(moduleId)) {
            moduleSubmAt = "Y";
        }
        paramData.put("moduleSubmAt", moduleSubmAt);
*/
        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = stressMapper.findStntSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = stressMapper.findStntSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        var slList = stressMapper.findStntSlfperEvlSlfSetSlList(paramData);
        //var perInfoList = stntSlfperEvalMapper.findStntSlfperEvlSlfSetPerInfoList(paramData);
        var perInfoList = stressMapper.findStntSlfperEvlSlfPerinfo(paramData);
        var templtList = stressMapper.findStntSlfperEvlSlfSetTempltList(paramData);
        var slfperYnMap = stressMapper.findStntSlfperEvlSlfSetSlfperYn(paramData);
        var templtYnMap = stressMapper.findStntSlfperEvlSlfSetTempltYn(paramData);

        returnMap.put("apraserId", MapUtils.getString(paramData, "stntId"));
        returnMap.put("selInfoId", MapUtils.getInteger(paramData, "selInfoId"));
        returnMap.put("perInfoId", MapUtils.getInteger(paramData, "perInfoId"));
        returnMap.put("slList", slList);
        returnMap.put("perInfoList", perInfoList);
        returnMap.put("templtList", templtList);
        returnMap.put("slfTotNum", slList.size());
        returnMap.put("perTotNum", perInfoList.size());

        String slfperYn = MapUtils.getString(slfperYnMap, "slfperYn", "N");
        String templtYn = MapUtils.getString(templtYnMap, "slfperYn", "N");

        if ("Y".equals(slfperYn) || "Y".equals(templtYn)) {
            slfperYn = "Y";
        }

        returnMap.put("slfperYn", slfperYn);

        returnMap.put("stSlExposAt", MapUtils.getString(selInfoIdMap, "stExposAt"));
        returnMap.put("stPltExposAt", MapUtils.getString(perInfoIdMap, "stExposAt"));

        paramData.remove("slfPerEvlSetInfo");
        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        return returnMap;
    }

    public Object createStntMdulQstnRecheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<Map<String, Object>> qstnList = (List<Map<String, Object>>) paramData.get("qstnList");

        if (org.apache.commons.lang3.ObjectUtils.isEmpty(qstnList)) {
            return returnMap;
        }

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        var setsMap = stressMapper.findStntMdulQstnResetSetsId(paramData);
        if (MapUtils.getString(paramData, "setsId") == null) {
            paramData.put("setsId", MapUtils.getString(setsMap, "setsId"));
        }
        log.info("setsId::" + MapUtils.getString(paramData, "setsId"));
        String strTabAddAt = MapUtils.getString(setsMap, "tabAddAt");

        Map<String, Object> resultSDRI = stressMapper.findStntMdulQstnResetSDRI(paramData);

        if (org.apache.commons.lang3.ObjectUtils.isEmpty(resultSDRI)) {
            int result2 = 0;
            result2 = stressMapper.createStntMdulQstnResetSDRI(paramData);
            log.info("result2:{}", result2);
        } else {
            if (MapUtils.getInteger(resultSDRI, "eakSttsCd") == 1) {
                int result1 = stressMapper.modifyStntMdulQstnResetSDRI(paramData);
                log.info("result1:{}", result1);
            }
        }

        List<Map> recheckList = new ArrayList<>();

        for (Map<String, Object> qstnMap : qstnList) {
            Integer subId2 = MapUtils.getInteger(qstnMap, "subId");
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(subId2)) {
                qstnMap.put("subId", 0);
            }

            paramData.put("articleId", MapUtils.getString(qstnMap,"articleId"));
            paramData.put("subId", MapUtils.getInteger(qstnMap,"subId"));
            paramData.put("articleTypeSttsCd", MapUtils.getInteger(qstnMap,"articleTypeSttsCd"));

            Map<String, Object> resultSDRD = stressMapper.findStntMdulQstnResetSDRD(paramData);
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(resultSDRD)) {
                int result4 = stressMapper.createStntMdulQstnResetSDRD(paramData);
                log.info("result4:{}", result4);
                /*
                if ("Y".equals(strTabAddAt)) { //tab_info.tab_add_at 값이 Y 일 경우 setsummary 테이블 존재
                    int result4 = stntMdulQstnMapper.createStntMdulQstnResetSDRD2(paramData);
                    log.info("result4:{}", result4);
                } else {
                    int result4 = stntMdulQstnMapper.createStntMdulQstnResetSDRD(paramData);
                    log.info("result4:{}", result4);
                }
                */
            } else {
                if (MapUtils.getInteger(resultSDRD, "eakSttsCd") != 3) {
                    int result3 = stressMapper.modifyStntMdulQstnResetSDRD(paramData);
                    log.info("result3:{}", result3);
                }
            }

            List<String> findStntMdulQstnRecheckItem = Arrays.asList("infoId", "detailId");
            //returnMap.put("infoId", "");
            //returnMap.put("detailId", "");

            var recheckMap = new HashMap<>();
            recheckMap.put("infoId", "");
            recheckMap.put("detailId", "");
            recheckMap.putAll(AidtCommonUtil.filterToMap(findStntMdulQstnRecheckItem, stressMapper.findStntMdulQstnRecheck(paramData)));

            // 영어교과템 유형일 경우 교과템 mother 데이터를 쌓는다
            if (MapUtils.getString(paramData, "articleTypeSttsCd", "1").equals("2")) {
                String articleId = MapUtils.getString(paramData, "articleId", "0");
                Integer subId3 = MapUtils.getInteger(paramData, "subId", 0);
                int resultDetailId = MapUtils.getInteger(recheckMap, "detailId", 0);
                if (articleId == "0" || resultDetailId == 0) {
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "실패 - empty → articleId : " + articleId + " / resultDetailId : " + resultDetailId);
                    log.error("error5:{}", "실패 - empty → articleId : " + articleId + " / resultDetailId : " + resultDetailId);
                    return returnMap;
                }
                List<Map<String, Object>> engtempInfoList = stressMapper.selectEngtempInfoByArticleId(articleId, subId3);
                if (CollectionUtils.isEmpty(engtempInfoList)) {
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "실패 - empty → selectEngtempInfoByArticleId");
                    log.error("error6:{}", "실패 - empty → selectEngtempInfoByArticleId");
                    return returnMap;
                }
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("resultDetailId", resultDetailId);
                for (Map<String, Object> engTempMap : engtempInfoList) {
                    int engTempId = MapUtils.getInteger(engTempMap, "engTempId", 0);
                    if (engTempId == 0) {
                        returnMap.put("resultOk", false);
                        returnMap.put("resultMsg", "실패 - empty → engTempId");
                        log.error("error7:{}", "실패 - empty → engTempId");
                        return returnMap;
                    }
                    String templateDivCode = MapUtils.getString(engTempMap, "templateDivCode", "").toUpperCase();
                    paramMap.put("engTempId", engTempId);
                    // voca일 경우 scriptId와 활동Id는 0 설정
                    if (templateDivCode.equals("VOCA") || templateDivCode.equals("VC")) {
                        paramMap.put("scriptId", 0);
                        paramMap.put("tmpltActvId", 0);
                        // 학생 학습 상세 id 정보와 교과템Id로 교과템 존재 여부를 조회한다
                        String existsYn = stressMapper.selectEngtempExistsYn(resultDetailId, engTempId);
                        if (existsYn.equals("Y")) {
                            continue;
                        }
                        stressMapper.insertLesnRsc(paramMap);
                    } else {
                        paramMap.put("scriptId", engTempMap.get("scriptId"));
                        // 학생 학습 상세 id 정보와 교과템Id로 교과템 목록을 조회한다 (교과템 활동이 있을 경우 해당 교과템 결과의 id subquery로 전달됨)
                        List<Map<String, Object>> engtempResultInfoList = stressMapper.selectEngtempAtivityList(resultDetailId, engTempId);
                        if (CollectionUtils.isEmpty(engtempResultInfoList)) {
                            continue;
                        }
                        for (Map<String, Object> map : engtempResultInfoList) {
                            // 교과템플릿 활동 결과 id
                            int engTempResultInfoId = MapUtils.getInteger(map, "engTempResultInfoId", 0);
                            // 이미 insert되어 있을 경우 해당 값이 0보다 크다
                            if (engTempResultInfoId > 0) {
                                continue;
                            }
                            int scriptId = MapUtils.getInteger(map, "scriptId", 0);
                            int tmpltActvId = MapUtils.getInteger(map, "tmpltActvId", 0);
                            // voca가 아닌데도 scriptId와 활동Id가 없으면 오류
                            if (scriptId == 0 || tmpltActvId == 0) {
                                returnMap.put("resultOk", false);
                                returnMap.put("resultMsg", "실패 - empty → scriptId : " + scriptId + " / tmpltActvId : " + tmpltActvId);
                                log.error("error8:{}", "실패 - empty → scriptId : " + scriptId + " / tmpltActvId : " + tmpltActvId);
                                return returnMap;
                            }
                            paramMap.put("scriptId", scriptId);
                            paramMap.put("tmpltActvId", tmpltActvId);
                            stressMapper.insertLesnRsc(paramMap);
                        }
                    }
                }
            }
            recheckMap.put("articleId", MapUtils.getString(qstnMap,"articleId"));
            recheckMap.put("subId", MapUtils.getInteger(qstnMap,"subId"));
            recheckMap.put("articleTypeSttsCd", MapUtils.getInteger(qstnMap,"articleTypeSttsCd"));
            recheckList.add(recheckMap);
        }

        paramData.remove("articleId");
        paramData.remove("subId");
        paramData.remove("articleTypeSttsCd");

        //returnMap.putAll(recheckMap);
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");
        returnMap.put("recheckList", recheckList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception {
        return stressMapper.findStntSlfperEvlSlfPerinfo(paramData);
    }

    @Transactional(readOnly = true)
    public Object findStntActMdulList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> actResultInfoItem = Arrays.asList(
            "id", "actId","actSttsCd","actSttsNm","actWy",
            "actWyNm","thumbnail","actSubmitUrl","actSubmitDc","actStDt",
            "actEdDt", "fdbDc", "fdbUrl"
        );

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        // 활동결과 정보
        List<LinkedHashMap<Object, Object>> actResultList = AidtCommonUtil.filterToList(actResultInfoItem, stressMapper.findStntActMdulList(paramData));

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("actResultList",actResultList);
        return respMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCurriculumList(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        // 2024-05-14
        // (학생)의 커리큘럼 조회인 경우
        String stntId = null;
        if (paramData.containsKey("stntId")) {
            stntId = MapUtils.getString(paramData, "stntId");
            paramData.remove("stntId");
        }

        TcCurriculumDTO tcCurriculumDTO = TcCurriculumDTO.mapToDto(paramData);
        if (null == tcCurriculumDTO.getTextbkIdxId()) {
            throw new IllegalArgumentException("textbook index id is required");
        }

        TextbookEntity2 textbook = textbookRepository.findByTextbookIndexId(
                tcCurriculumDTO.getTextbkIdxId()).orElseThrow(() -> new IllegalArgumentException("textbook doesn't exist"));

        // 2024-04-03
        // 해당 교과서 ID값을 web_textbook_id 컬럼에 갖고 있는 ebook 교과서에서 pdf_url값을 구함.
        TextbookEntity2 ebook = textbookRepository.findTop1ByWebTextbookIdAndIsActive(
                textbook.getId(), true).orElse(null);

        // 교과서 기본정보 저장
        rtnMap.put("textbkId", textbook.getId());
        rtnMap.put("textbkName", textbook.getName());
        rtnMap.put("ebkId", ObjectUtils.isEmpty(ebook) ? 0L : ebook.getId());
        rtnMap.put("pdfUrl", ObjectUtils.isEmpty(ebook) ? "" : ebook.getPdfUrl());

        // 마지막 수업위치 조회
        Long lastPosition = 0L;
        TcLastlessonEntity2 lastlessonEntity
                = tcLastlessonRepository.findByWrterIdAndClaIdAndTextbkIdAndTextbkIdxId(
                tcCurriculumDTO.getUserId(), tcCurriculumDTO.getClaId(), tcCurriculumDTO.getTextbkId(), tcCurriculumDTO.getTextbkIdxId()).orElse(null);

        if (!ObjectUtils.isEmpty(lastlessonEntity)) {
            lastPosition = lastlessonEntity.getCrculId();
        }

        // 2024-05-14
        // (학생)의 커리큘럼 조회인 경우
        // - 학생의 마지막 위치값이 존재하면 학생의 마지막 위치값 사용
        // - 그렇지 않은 경우에는 교사의 마지막 위치값 사용
        if (stntId != null
                && paramData.containsKey("lastPosition")) {
            lastPosition = MapUtils.getLong(paramData, "lastPosition");
            paramData.remove("lastPosition"); // key 삭제
        }

        List<Map<String, Object>> curriList = stressMapper.selectCurriculumList(paramData);
        if (CollectionUtils.isEmpty(curriList)) {
            return rtnMap;
        }

        Collections.sort(curriList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                int orderNo1_1 = MapUtils.getInteger(map1, "order", 0);
                int orderNo1_2 = MapUtils.getInteger(map2, "order", 0);
                if (orderNo1_1 < orderNo1_2) {
                    return -1;
                } else if (orderNo1_1 > orderNo1_2) {
                    return 1;
                }
                return 0;
            }
        });

        Map<Long, Map> curriMapForKey = new HashMap<>();
        for (Map<String, Object> map : curriList) {
            Long key = MapUtils.getLong(map, "key", 0L);
            if (key == 0L) {
                continue;
            }
            Integer order = MapUtils.getInteger(map, "order", 0);
            Long parent = MapUtils.getLong(map, "parent", 0L);
            // 9999를 넘어가는 커리큘럼 개수는 없다고 가정
            String curriCd = null;
            // order가 0 보다 큰 경우는 CMS 데이터 (cms에서는 insert 시 order 처리를 한다)
            if (order > 0) {
                curriCd = StringUtils.leftPad(order.toString(), 4, "0");
            } else {
                curriCd = StringUtils.leftPad(key.toString(), 4, "0");
            }

            String curriOrder = null;
            // 24 (6depth가 최고라고 가정)
            if (parent == 0L) {
                // 1000100000000000000000000
                curriOrder = "1" + StringUtils.rightPad(curriCd, 24, "0");
            } else {
                // 1000100010000000000000000, 1000100020000000000000000 ... 1000100030001000200000000
                Map<String, Object> parentMap = curriMapForKey.get(parent);
                String parentCurriCd = MapUtils.getString(parentMap, "curriCd", "");
                curriCd = parentCurriCd + curriCd;
                String tempCurriCd = StringUtils.rightPad(curriCd, 24, "0");
                curriOrder = "1" + tempCurriCd;

            }
            curriMapForKey.put(key, map);
            map.put("curriCd", curriCd);
            // 자리수 25자리 string 정보로 정렬 처리
            map.put("curriOrder", curriOrder);
            if (key == lastPosition) {
                map.put("lastPosition", true);
            }
        }

        // curriOrder로 정렬 (order는 무시하고 depth 구조로 정렬)
        Collections.sort(curriList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                String orderNo1_1 = MapUtils.getString(map1, "curriOrder");
                String orderNo1_2 = MapUtils.getString(map2, "curriOrder");
                return StringUtils.compare(orderNo1_1, orderNo1_2);
            }
        });

        List<TcCurriculumDTO> curriDtoList = new ArrayList<>();
        int no = 1;
        for (Map<String, Object> map : curriList) {
            // order culumn 추가
            map.put("order", no++);
            // 정렬을 위해 추가했던 key 제거
            map.remove("curriCd");
            map.remove("curriOrder");
            TcCurriculumDTO curriDto = TcCurriculumDTO.mapToDto(map);
            curriDtoList.add(curriDto);
        }
        rtnMap.put("curriculumList", curriDtoList);

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntCrcuLastposition(Map<String, Object> paramData)throws Exception {
        List<String> listItem = Arrays.asList(
                "id","userId", "textbkId", "claId", "crculId"
        );
        LinkedHashMap<Object, Object> resultMap = AidtCommonUtil.filterToMap(listItem, stressMapper.selectStntCrcuLastposition(paramData));
        return resultMap;
    }

     public Map saveStntCrcuLastposition(Map<String, Object> paramData)throws Exception {
        LinkedHashMap<Object, Object> resultMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
               "id","userId", "textbkId", "claId", "crculId"
       );
        LinkedHashMap<Object, Object> selectMap = AidtCommonUtil.filterToMap(listItem, stressMapper.selectStntCrcuLastposition(paramData));
        int cnt = 0;
        if(com.nimbusds.oauth2.sdk.util.MapUtils.isEmpty(selectMap)) {
            cnt = stressMapper.createStntCrcuLastposition(paramData);
        } else {
            paramData.put("id", selectMap.get("id"));
            cnt = stressMapper.updateStntCrcuLastposition(paramData);
        }

        if(cnt>0) {
            resultMap = AidtCommonUtil.filterToMap(listItem, stressMapper.selectStntCrcuLastposition(paramData));
        } else {
            resultMap.put("id", null);
            resultMap.put("userId", paramData.get("userId"));
            resultMap.put("textbkId", paramData.get("textbkId"));
            resultMap.put("claId", paramData.get("claId"));
            resultMap.put("crculId", paramData.get("crculId"));
        }

        paramData.remove("id");
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findCrcuInfo(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();

        TcCurriculumDTO tcCurriculumDTO = TcCurriculumDTO.mapToDto(paramData);
        TcCurriculumEntity2 textbookCurriculum = tcCurriculumRepository
                .findAllByWrterIdAndClaIdAndTextbkIdAndTextbkIdxIdAndKey(
                        tcCurriculumDTO.getUserId(), tcCurriculumDTO.getClaId(), tcCurriculumDTO.getTextbkId(), tcCurriculumDTO.getTextbkIdxId(),tcCurriculumDTO.getCrculId()).orElseThrow(() -> new IllegalArgumentException("curriculum doesn't exist"));

        AtomicBoolean isFirst = new AtomicBoolean(true);
        Map<String, Object> searchParam = new HashMap<>();

        // 해당 교과서 ID값을 web_textbook_id 컬럼에 갖고 있는 ebook 교과서에서 pdf_url값을 구함.
        TextbookEntity2 ebook = textbookRepository.findTop1ByWebTextbookIdAndIsActive(
                MapUtils.getLong(paramData, "textbkId"), true).orElse(null);

        // 탭 목록 조회
        List<Map<String,Object>> tabInfoList = stressMapper.findCrcuTabList(paramData);

        TcCurriculumDTO2 textbookCurriculumDTO = TcCurriculumDTO2.toDTO(textbookCurriculum);
        textbookCurriculumDTO.setEbkId(ObjectUtils.isEmpty(ebook) ? 0L : ebook.getId());
        textbookCurriculumDTO.setPdfUrl(ObjectUtils.isEmpty(ebook) ? "" : ebook.getPdfUrl());
        textbookCurriculumDTO.setTextbookTabList(
            CollectionUtils.emptyIfNull(tabInfoList)
                    .stream()
                    .map(tabInfo -> TabInfoDTO.mapToDto(tabInfo))
                    /*
                    .peek(tabInfoDTO -> {
                        // 첫번째 탭에 연결된 셋트지의 모듈 목록정보 조회하여 설정해줌.
                        if(isFirst.get()) {
                            isFirst.set(false);

                            try {
                                searchParam.put("tabId", tabInfoDTO.getId());

                                Map<String, Object> tabInfoMap = tchCrcuTabService.findCrcuTabMdulList(searchParam);
                                if (!tabInfoMap.isEmpty()) {
                                    tabInfoDTO.setSet((SetsDTO) tabInfoMap.get("set"));
                                }
                            } catch(Exception e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                    })*/
                    .collect(Collectors.toList())
        );

        rtnMap.put("curriculumInfo", textbookCurriculumDTO);

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findUserInfo(Map<String, Object> paramData) throws Exception {

        Exception ex = null;

        Map<String, Object> rtnMap = new LinkedHashMap<>();

        String userId = MapUtils.getString(paramData, "userId", "");
        String semester = MapUtils.getString(paramData, "semester", "");
        String claId = MapUtils.getString(paramData,  "claId", "");

        User2 user = userRepository.findByUserId(userId);
        if (user == null) {
            rtnMap.put("success", false);
            rtnMap.put("resultMessage", "findUserInfo > findByUserId\r\nuser empty error - userId : " + userId);
            return rtnMap;
        }

        String userSeCd = user.getUserSeCd(); // 사용자구분(S:학생,T:교사,P:학부모)
        rtnMap.put("gubun", userSeCd);

        // 유저 정보 설정
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("uuid", user.getUserId());
        userInfo.put("name", user.getFlnm());
        userInfo.put("firstName", "");
        userInfo.put("lastName", "");
        userInfo.put("thumbnail", "");
        userInfo.put("defaultThumbnail", "");
        userInfo.put("profileThumbnail", "");
        userInfo.put("birthday", user.getBrth());
        userInfo.put("gender", user.getSex());
        userInfo.put("age", 0);
        userInfo.put("frIdx", 0);
        userInfo.put("token", "");

        rtnMap.put("userInfo", userInfo);

        // 학생 목록
        List<Map<String, Object>> studentList = new ArrayList<>();

        // 학급명
        String classroomName = null;

        // 클래스 정보 설정
        Map<String, Object> classInfo = new HashMap<>();
        Map<String, Object> claInfo = null;
        Integer classId = 0;
        Map<String, Object> textbookInfo = new HashMap<>();

        switch (userSeCd) {
            case "T":
                // 교사 학급 정보 조회
                claInfo = stressMapper.findClassInfo(paramData);
                if (claInfo == null) {
                    rtnMap.put("success", false);
                    rtnMap.put("resultMessage", "findUserInfo > findUserInfo > findClassInfo\r\nteacher claInfo empty error - " + paramData);
                    return rtnMap;
                }
                classId = MapUtils.getInteger(claInfo, "id", 0);
                if (classId == 0) {
                    rtnMap.put("success", false);
                    rtnMap.put("resultMessage", "findUserInfo > findUserInfo > classId\r\nteacher classId zero error - " + paramData);
                    return rtnMap;
                }

                List<Map<String, Object>> stdtList = stressMapper.findStdtListByClass(paramData);
                if (CollectionUtils.isNotEmpty(stdtList)) {
                    int idx = 0;
                    for (Map<String, Object> stdtInfo : stdtList) {
                        if (stdtInfo == null) {
                            continue;
                        }
                        if (idx == 0) {
                            classroomName = MapUtils.getString(stdtInfo, "classroomName");
                        }
                        Map<String, Object> stdtMap = this.getStdtInfo(stdtInfo);
                        if (MapUtils.isEmpty(stdtMap)) {
                            continue;
                        }
                        studentList.add(stdtMap);
                        idx++;
                    }
                }

                classInfo.put("students", studentList); // 학생 목록

                //교과서정보
                Map<String, Object> tcParam = new HashMap<>();
                tcParam.put("wrterId", user.getUserId());
                tcParam.put("claId", claId);
                tcParam.put("smteCd", semester);
                Map<String, Object> tcTextbookInfo = this.getTcTextbookInfo(tcParam);
                textbookInfo.put("textbookId", MapUtils.getLong(tcTextbookInfo, "textbkId", 0L));
                textbookInfo.put("textbookIndexId", MapUtils.getLong(tcTextbookInfo, "textbkIdxId", 0L));
                textbookInfo.put("textbookName", MapUtils.getString(tcTextbookInfo, "textbkNm", ""));
                break;
            case "S":
                Map<String, Object> studentInfo = stressMapper.findStdtInfo(paramData);
                Map<String, Object> stdtInfo = this.getStdtInfo(studentInfo);
                if (MapUtils.isNotEmpty(stdtInfo)) {
                    Map<String, Object> searchMap = new HashMap<>();
                    searchMap.put("userId", stdtInfo.get("teacherId"));
                    searchMap.put("claId", stdtInfo.get("claId"));

                    // 교사 학급 정보 조회
                    claInfo = stressMapper.findClassInfo(searchMap);
                    if (claInfo == null) {
                        rtnMap.put("success", false);
                        rtnMap.put("resultMessage", "findUserInfo > findUserInfo > findClassInfo\r\nstudent claInfo empty error - " + paramData);
                        return rtnMap;
                    }
                    classId = MapUtils.getInteger(claInfo, "id", 0);
                    if (classId == 0) {
                        rtnMap.put("success", false);
                        rtnMap.put("resultMessage", "findUserInfo > findUserInfo > classId\r\nstudent classId zero error - " + paramData);
                        return rtnMap;
                    }

                    classInfo.put("student", stdtInfo);
                    classroomName = MapUtils.getString(studentInfo, "classroomName");

                    //교과서정보
                    Map<String, Object> stParam = new HashMap<>();
                    stParam.put("tcId", stdtInfo.get("teacherId"));
                    stParam.put("claId", claId);
                    Map<String, Object> stTextbookInfo = this.getStTextbookInfo(stParam);
                    textbookInfo.put("textbookId", MapUtils.getLong(stTextbookInfo, "textbkId", 0L));
                    textbookInfo.put("textbookIndexId", MapUtils.getLong(stTextbookInfo, "textbkIdxId", 0L));
                    textbookInfo.put("textbookName", MapUtils.getString(stTextbookInfo, "textbkNm", ""));
                }
                break;
            case "P":
                break;
        }
        classInfo.put("id", classId);
        classInfo.put("name", classroomName);

        rtnMap.put("classInfo", classInfo);
        rtnMap.put("textbookInfo", textbookInfo);

        return rtnMap;
    }

    public Map<String,Object> getStdtInfo(Map<String,Object> stdtInfo) throws Exception {
        if (stdtInfo == null) {
            return null;
        }
        String[] keys = new String[]{"id", "userId", "flnm", "schlNm"/*, "gradeCd", "claCd"*/, "classroomName", "teacherId", "claId", "teacherIdx"};
        Map<String, Object> studentInfo = new HashMap<>();
        for (String key : keys) {
            Object obj = stdtInfo.get(key);
            if (obj == null) {
                continue;
            }
            studentInfo.put(key, obj);
        }
        return studentInfo;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTcTextbookInfo(Map<String, Object> data) throws Exception {
        return stressMapper.getTcTextbookInfo(data);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStTextbookInfo(Map<String, Object> data) throws Exception {
        return stressMapper.getStTextbookInfo(data);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        String userId = String.valueOf(paramData.get("uuid"));
        String semester = (String) paramData.getOrDefault("semester","");
        String claId = (String) paramData.getOrDefault("claId","");

        // 아이디 미 입력
        if (StringUtils.isEmpty(userId)) {
            resultMap.put("result", 100);
            resultMap.put("returnType", "Error - uuid required");
            return resultMap;
        }

        insertAccessLog(paramData); // 학생 접속로그 기록

        //로그인 여부 업데이트
        paramData.put("lgnSttsAt", 1);
        updateLoginStatus(paramData);

        //UserDTO userDto = UserDTO.builder().build();

        User2 user = userRepository.findByUserId(userId);
        if (user == null || !MapUtils.getString(paramData, "userDiv").equals(user.getUserSeCd())) {
            resultMap.put("result", 102);
            resultMap.put("returnType", "Error - No User exists");
            return resultMap;
        }

        // ---> [S] 소켓 및 학습 필수 로직
        String userDiv = user.getUserSeCd();
        String schlCd = null;
        Long classid = 0L;
        long textbkId = 0;
        long textbkIdxId = 0;

        if (StringUtils.equals(userDiv, UserDiv.S.getCode())) {
            StdtRegInfoEntity2 userInfo = stdtRegInfoRepository.findByUserId(user.getUserId()).orElse(null);
            if (userInfo != null) {
                schlCd = userInfo.getSchlCd();
            }
            List<TcClaMbInfoEntity2> tcClaMbInfoList = tcClaMbInfoRepository.findByStdtId(userId);
            // 학생이 속해있는 클래스가 다수일 경우 선생님이 로그인 후 학생에게 클래스를 전달하는 등의 프로세스 검토
            String tcId = "";
            if (CollectionUtils.isNotEmpty(tcClaMbInfoList)) {
                // 현재는 학생 1:1 매치 가정하여 로직 구현
                claId = tcClaMbInfoList.get(0).getClaId();
                tcId = tcClaMbInfoList.get(0).getUserId();
            }
            // 학급 구성원의 ID 정보로 선생님 학급 테이블에서 id 조회
            if (StringUtils.isNotEmpty(claId)) {
                TcClaInfoEntity2 tcClaInfo = tcClaInfoRepository.findByClaIdAndUserId(claId, tcId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            }
            //학생 교과서조회
            Map<String, Object> stParam = new HashMap<>();
            stParam.put("tcId", tcId);
            stParam.put("claId", claId);
            Map<String, Object> stTextbookInfo = stressMapper.getStTextbookInfo(stParam);
            textbkId = MapUtils.getLong(stTextbookInfo, "textbkId", 0L);
            textbkIdxId = MapUtils.getLong(stTextbookInfo, "textbkIdxId", 0L);
        }
        // 선생일 경우 선생 매치 클래스 테이블에서 직접 데이터 조회 (선생님과 클래스간 구조가 바뀔 경우 로직 수정 검토 - 예 n:n )
        else if (StringUtils.equals(userDiv, UserDiv.T.getCode())) {
            TcRegInfoEntity2 userInfo = tcRegInfoRepository.findByUserId(user.getUserId()).orElse(null);
            if (userInfo != null) {
                schlCd = userInfo.getSchlCd();
            }

            if (StringUtils.isNotEmpty(claId)) {
                TcClaInfoEntity2 tcClaInfo = tcClaInfoRepository.findByClaId(claId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            } else {
                TcClaInfoEntity2 tcClaInfo = tcClaInfoRepository.findTop1ByUserId(userId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                    claId = tcClaInfo.getClaId();
                }
            }

            //교사 교과서조회
            Map<String, Object> tcParam = new HashMap<>();
            tcParam.put("wrterId", user.getUserId());
            tcParam.put("claId", claId);
            tcParam.put("smteCd", semester);
            Map<String, Object> tcTextbookInfo = stressMapper.getTcTextbookInfo(tcParam);
            textbkId = MapUtils.getLong(tcTextbookInfo, "textbkId", 0L);
            textbkIdxId = MapUtils.getLong(tcTextbookInfo, "textbkIdxId", 0L);
        }
        int frIdx = 0;
        if (StringUtils.isNotEmpty(schlCd)) {
            School2 school = schoolRepository.findBySchlCd(schlCd).orElse(null);
            if (school != null) {
                frIdx = school.getId();
            }
        }

        resultMap.put("frIdx", frIdx);
        resultMap.put("classid", classid);
        resultMap.put("claId", claId);
        resultMap.put("textbkId", textbkId);
        resultMap.put("textbkIdxId", textbkIdxId);
        // --/> [E] 소켓 및 학습 필수 로직

        resultMap.put("birthday", user.getBrth());
        resultMap.put("thumbnail", "");

        /*SSOToken 제거*/
        resultMap.put("gender", user.getSex());
        /*schIdx 제거*/
        /*brcIdx 제거*/
        resultMap.put("nickName", user.getFlnm()); // nick name이 없어서 이름으로 대체
        resultMap.put("defaultThumbnail", "");
        resultMap.put("uuid", userId);
        /*token 제거*/
        resultMap.put("result", 0);
        // 학생일때는 프로필 이미지를 랜덤으로 생성해서 보내준다.
        // resultMap.put("profileThumbnail", MemberUtil.getStudentProfileImage());
        resultMap.put("profileThumbnail", "");
        resultMap.put("name", user.getFlnm());
        resultMap.put("id", user.getId());
        resultMap.put("enc", "");
        resultMap.put("pwd", "");
        resultMap.put("userDiv", paramData.get("userDiv"));
        resultMap.put("resultType", "Success");
        resultMap.put("key", "");

        return resultMap;
    }

    public Map<String, Object> insertAccessLog(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String userDiv = (String) paramData.get("userDiv");
            if("S".equals(userDiv)) { // 학생일 경우만 기록
                LocalDateTime now = LocalDateTime.now();
                String uuid = (String) paramData.get("uuid");

                // ip 값이 NULL 이면 저장하지 않는다.
                String ipAddr = MapUtils.getString(paramData, "ip");
                if(StringUtils.isEmpty(ipAddr)) {
                    resultMap.put("resultOk",false);
                    resultMap.put("resultMsg","IP 주소값이 존재하지않습니다.");
                    return resultMap;
                }

                CntnLogEntity2 cntnLog = CntnLogEntity2.builder()
                    .userId(uuid)
                    .userSeCd(userDiv)
                    .cntnDt(now)
                    .cntnIpAddr(StringUtils.defaultIfEmpty((String)paramData.get("ip"),""))
                    .deviceInfo(StringUtils.defaultIfEmpty((String)paramData.get("device"),""))
                    .osInfo(StringUtils.defaultIfEmpty((String)paramData.get("os"),""))
                    .brwrInfo(StringUtils.defaultIfEmpty((String)paramData.get("browser"),""))
                    .rgtr(uuid)
                    .regDt(now)
                    .mdfr(uuid)
                    .mdfyDt(now)
                    .build();

                CntnLogEntity2 saved = cntnLogRepository.save(cntnLog);
                resultMap.put("id",saved.getId());
                resultMap.put("resultOk",true);
                resultMap.put("resultMsg","저장완료");
            }
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk",false);
            resultMap.put("resultMsg","저장실패");
        }
        return resultMap;
    }

    public Map<String, Object> updateLoginStatus(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            stressMapper.updateLgnSttsAt(paramData);
            resultMap.put("result", 0);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk",false);
            resultMap.put("resultMsg","저장실패");
        }
        return resultMap;
    }

    public Object selectTchTool(Object paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                 "tolId", "claId","textbkId","userSeCd", "monitor","attention","pentool", "mathtool","aiSpeaking","aiWriting","timer"
                ,"picker", "bookmark","hideShow","quiz", "opinionBoard","whiteBoard","sbjctCd"
        );

        Map<String, Object> isExist = stressMapper.selectTchToolExistCheck(paramData);

        if(isExist != null){
            return AidtCommonUtil.filterToMap(infoItem, isExist);
        }else{
            int cnt = stressMapper.insertTchToolInfo(paramData);
            if(cnt > 0){
                return AidtCommonUtil.filterToMap(infoItem, stressMapper.selectTchToolExistCheck(paramData));
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Object findTchMdulQstnAnsw(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //List<String> tchMdulQstnAnswItem = Arrays.asList("dtaIemId", "crrtStntCnt", "submStntCnt", "totalStntCnt", "rating", "bmkYn");
        List<String> tchMdulQstnAnswItem = Arrays.asList("dtaIemId", "subId", "submStntCnt", "bmkYn", "bmkId");
        List<String> tchMdulQstnAnswStntItem = Arrays.asList("userIdx", "subMitAnw", "userId", "flnm", "errata", "dtaIemId", "subId");
        List<String> tchMdulQstnAnswSelfStdItem = Arrays.asList("userIdx", "userId", "flnm", "dtaIemId", "subId");

        //var sdriMap =  tchMdulQstnMapper.findTchMdulQstnSDRI(paramData);
        //paramData.put("textbkTabId", MapUtils.getInteger(sdriMap, "textbkTabId"));

        var mdulList = AidtCommonUtil.filterToList(tchMdulQstnAnswItem, stressMapper.findTchMdulQstnAnswResultDetailInfo(paramData));

        if (!org.apache.commons.lang3.ObjectUtils.isEmpty(mdulList)) {
            var qstnInfoList = AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, stressMapper.findTchMdulQstnAnswResultInfo(mdulList, paramData));
            var selfStdList = AidtCommonUtil.filterToList(tchMdulQstnAnswSelfStdItem, stressMapper.findTchMdulQstnAnswSelfStd(mdulList, paramData));

            for (LinkedHashMap<Object, Object> s : mdulList) {
                s.put("qstnInfoList", CollectionUtils.emptyIfNull(qstnInfoList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"dtaIemId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        g.remove("dtaIemId");
                        g.remove("subId");
                        return g;
                    }).toList()
                );
                s.put("selfStdList", CollectionUtils.emptyIfNull(selfStdList).stream()
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"dtaIemId")))
                    .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                    .map(g -> {
                        g.remove("dtaIemId");
                        g.remove("subId");
                        return g;
                    }).toList()
                );
            }
        }

        returnMap.put("mdulList", mdulList);
        /*
        List<Map> resultInfoList = tchMdulQstnMapper.findTchMdulQstnAnswResultInfo(paramData);

        var qstnInfo = new LinkedHashMap<>();
        List<Map> qstnInfo1 = new ArrayList<>();
        List<Map> qstnInfo2 = new ArrayList<>();
        List<Map> qstnInfo3 = new ArrayList<>();
        List<Map> qstnInfo4 = new ArrayList<>();
        List<Map> qstnInfo5 = new ArrayList<>();


        resultInfoList.stream().forEach( s -> {
            if ("1".equals(MapUtils.getString(s, "subMitAnw"))) {
                qstnInfo1.add(s);
            } else if ("2".equals(MapUtils.getString(s, "subMitAnw"))) {
                qstnInfo2.add(s);
            } else if ("3".equals(MapUtils.getString(s, "subMitAnw"))) {
                qstnInfo3.add(s);
            } else if ("4".equals(MapUtils.getString(s, "subMitAnw"))) {
                qstnInfo4.add(s);
            } else if ("5".equals(MapUtils.getString(s, "subMitAnw"))) {
                qstnInfo5.add(s);
            }
        });

        qstnInfo.put("qstnInfoList1", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, qstnInfo1));
        qstnInfo.put("qstnCnt1", qstnInfo1.size());
        qstnInfo.put("qstnInfoList2", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, qstnInfo2));
        qstnInfo.put("qstnCnt2", qstnInfo2.size());
        qstnInfo.put("qstnInfoList3", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, qstnInfo3));
        qstnInfo.put("qstnCnt3", qstnInfo3.size());
        qstnInfo.put("qstnInfoList4", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, qstnInfo4));
        qstnInfo.put("qstnCnt4", qstnInfo4.size());
        qstnInfo.put("qstnInfoList5", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, qstnInfo5));
        qstnInfo.put("qstnCnt5", qstnInfo5.size());

        returnMap.put("qstnInfoList", AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, resultInfoList));
        returnMap.put("selfStdList", AidtCommonUtil.filterToList(tchMdulQstnAnswSelfStdItem, tchMdulQstnMapper.findTchMdulQstnAnswSelfStd(paramData)));
         */

        paramData.remove("textbkTabId");
        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementClassdDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "stdAt"
        );
        List<String> listItem2 = Arrays.asList(
                "code", "codeNm", "usdAchScrPercent", "diffType"
        );

        // 초등 5,6 학년 영어일 시 "grammar" 제외
        List<String> languageCodes;
        if(paramData.get("textbookId").equals("6981") || paramData.get("textbookId").equals("6982")){
            languageCodes = List.of("pronunciation", "vocabulary");
        } else {
            languageCodes = List.of("pronunciation", "grammar", "vocabulary");
        }
        paramData.put("languageCodes", languageCodes);

        // 학급 평균
        List<LinkedHashMap<Object, Object>> classDistribution = AidtCommonUtil.filterToList(listItem, stressMapper.selectTchDsbdAreaAchievementDistribution(paramData));

        for (Map map : classDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("classDistribution", classDistribution);

        // 학급 요약
        List<LinkedHashMap<Object, Object>> achievementSummary = AidtCommonUtil.filterToList(listItem2, stressMapper.selectTchDsbdAreaAchievementDistributionSummary(paramData));

        for (Map map : achievementSummary) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("achievementSummary", achievementSummary);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object selectTchDsbdUnitAchievementListAll(Map<String, Object> paramData) throws Exception {
        // 반환할 결과 맵 초기화
        var returnMap = new LinkedHashMap<>();

        // 필터링에 사용할 항목 리스트 정의
        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        // 모든 기본 단원 정보 조회
        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = stressMapper.selectTchDsbdUnitInfo(paramData);

        // "전 단원" 정보를 담을 맵 생성
        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfoMap.put("unit", "");
        // "전 단원" 정보를 단원 목록의 첫번째 위치에 추가
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);
        // 필요한 항목만 필터링하여 새로운 리스트 생성
        List<LinkedHashMap<Object, Object>> materAreaUnitInfo = AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);



        // 성취도 정보 조회
        List<Map> unitAchievement = stressMapper.selectTchDsbdUnitAchievementList(paramData);

        // 전체 단원 평균 성취도 계산
        double totalAchievement = 0.0;
        int unitCount = 0;

        for (Map unit : unitAchievement) {
            if (unit.containsKey("avgUsdAchScrPercent")) {
                totalAchievement += Double.parseDouble(unit.get("avgUsdAchScrPercent").toString());
                unitCount++;
            }
        }

        double averageAchievement = (unitCount > 0) ? (totalAchievement / unitCount) : 0;

        // 전체 평균을 나타내는 Map 생성 (JSON 형식에 맞춤)
        Map<String, Object> totalAchievementMap = new LinkedHashMap<>();
        totalAchievementMap.put("unitNum", 0);  // 단원 번호는 0으로 설정
        totalAchievementMap.put("avgUsdAchScrPercent", averageAchievement);

        // UnitAchievementList에 전체 단원 데이터 추가 (맨 앞에 추가)
        unitAchievement.add(0, totalAchievementMap);

        for (Map map : unitAchievement) {
            // avgUsdAchScrPercent 반올림
            if (map.containsKey("avgUsdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("avgUsdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("avgUsdAchScrPercent", roundedValue);
            }
        }

        // 최고/최저 성취도 단원 찾기
        String highestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .max(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");

        String lowestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .min(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");


        // 최종 결과 맵에 데이터 추가
        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("UnitAchievementList", unitAchievement);
        returnMap.put("highestUnitName", highestUnitName);
        returnMap.put("lowestUnitName", lowestUnitName);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdAreaAchievementStudentDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 학생 개인
        List<Map> studentDistribution = stressMapper.selectTchDsbdAreaAchievementStudentDstribution(paramData);

        // 반올림 처리
        for (Map map : studentDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("studentDistribution", studentDistribution);

        // 학생의 최고/최저 성취도 영역 조회
        List<Map> achievementSummary = stressMapper.selectStntDsbdAreaAchievementStudentDistributionSummary(paramData);

        returnMap.put("achievementSummary", achievementSummary);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 ALL(단원별)
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdUnitAchievementListAll(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 필터링에 사용할 항목 리스트 정의
        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        int unitNumChk = 0;
        if (paramData.get("unitNum") != null && !paramData.get("unitNum").equals("")) {
            unitNumChk = Integer.parseInt(paramData.get("unitNum").toString());
        }


        // 모든 기본 단원 정보 조회
        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = stressMapper.selectStntDsbdUnitInfo(paramData);

        // "전 단원" 정보를 담을 맵 생성
        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfoMap.put("unit", "");
        // "전 단원" 정보를 단원 목록의 첫번째 위치에 추가
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);
        // 필요한 항목만 필터링하여 새로운 리스트 생성
        List<LinkedHashMap<Object, Object>> materAreaUnitInfo = AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);



        // 학생 이름 조회
        String stntId = paramData.get("userId").toString();
        Map<String, Object> loginUserInfo = stressMapper.findUserInfoByUserId(stntId);
        returnMap.put("flnm", loginUserInfo.get("flnm"));



        // 성취도 정보 조회
        List<Map> unitAchievement = stressMapper.selectStntDsbdUnitAchievementList(paramData);

        // 전체 단원의 평균 성취도 계산을 위한 변수 초기화
        double totalAchievementPercent = 0.0;
        int unitCount = 0;

        if (unitNumChk <= 0) {
            for (Map unit : unitAchievement) {
                if (unit.containsKey("avgUsdAchScrPercent")) {
                    totalAchievementPercent += Double.parseDouble(unit.get("avgUsdAchScrPercent").toString());
                    unitCount++;
                }
            }

            double averageAchievement = (unitCount > 0) ? (totalAchievementPercent / unitCount) : 0;

            // 전체 평균을 나타내는 Map 생성 (JSON 형식에 맞춤)
            Map<String, Object> totalAchievementMap = new LinkedHashMap<>();
            totalAchievementMap.put("unitNum", 0);  // 단원 번호는 0으로 설정
            totalAchievementMap.put("avgUsdAchScrPercent", averageAchievement);

            // UnitAchievementList에 전체 단원 데이터 추가 (맨 앞에 추가)
            unitAchievement.add(0, totalAchievementMap);
        }

        for (Map map : unitAchievement) {
            // avgUsdAchScrPercent 반올림
            if (map.containsKey("avgUsdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("avgUsdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("avgUsdAchScrPercent", roundedValue);
            }
        }

        // 최고/최저 성취도 단원 찾기
        String highestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .max(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");

        String lowestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .min(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");

        // 최종 결과 맵에 데이터 추가
        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("UnitAchievementList", unitAchievement);
        returnMap.put("highestUnitName", highestUnitName);
        returnMap.put("lowestUnitName", lowestUnitName);

        return returnMap;
    }

    public List<Map<String, Object>> getTextbookCrcuList(Map<String, Object> paramData) throws Exception {
        return stressMapper.findTextbookCrcuList(paramData);
    }

    public Object modifyStntEvalSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 학생 문항지 조희 후 제출여부 확인
        Map<String, Object> resultInfoMap = stressMapper.findStntEvalStart(paramData);
        if ("Y".equals(MapUtils.getString(resultInfoMap, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        // 문항 상태 : mrk_ty {1 : 자동채점, 2 : 수동채점, 3 : 채점불가}
        // 문항 타입 : articleType {20 : 개념 concept, 21 : 문항 question, 22 : 활동 movement}


        List<String> returnItem = Arrays.asList("userId", "evlResultId", "evlIemId", "subId");

        String errata = String.valueOf(paramData.get("errata"));
        String evlIemScr = "0";

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        if ("1".equals(errata)) {
            // 점수 배점표 조회 evl_iem_info : 자동채점(1)일때 점수부여
            Map<String, Object> evlIemInfoMap = stressMapper.findStntEvalSaveIemScr(paramData);

            if (evlIemInfoMap != null) {
                evlIemScr = String.valueOf(evlIemInfoMap.get("evlIemScr"));
            }
        }

        paramData.put("evlIemScr", evlIemScr);

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        int result2 = stressMapper.modifyStntEvalSaveResultInfo(paramData);
        log.info("result2:{}", result2);

        return returnMap;
    }
}
