package com.visang.aidt.lms.api.dashboard.service;

import com.visang.aidt.lms.api.dashboard.mapper.StntDsbdInfoMapper;
import com.visang.aidt.lms.api.dashboard.mapper.TchDsbdMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CamelHashMap;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntDsbdInfoService {

    private final StntDsbdInfoMapper stntDsbdInfoMapper;
    private final TchDsbdMapper tchDsbdMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;


    // 개념별 이해도
    @Transactional(readOnly = true)
    public Object selectStntDsbdConceptUsdList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> listItem = Arrays.asList(
                "metaId", "unitNum","unitNm", "kwgMainId", "kwgNm", "stdAt" ,"usdScr" , "unitLastLesnAt", "kwgLastLesnAt"
        );
        List<String> listItem1 = Arrays.asList(
                "metaId", "unitNum", "kwgMainId", "stdDt", "stdDtLabel", "usdScr"
        );

        List<LinkedHashMap<Object, Object>> chptUnitKwgCombo;
        List<LinkedHashMap<Object, Object>> cncptUsdList;

        String metaId = (String) paramData.get("metaId");
        String kwgMainId = (String) paramData.get("kwgMainId");
        String allSrhYn = (String) paramData.get("allSrhYn");

        Map<Object, Object> rtnMap = new LinkedHashMap<>();
        if(!"Y".equals(allSrhYn) && "".equals(kwgMainId)){
            // 단원별 지식요인 정보(콤보박스)
            List<Map> kwgComboList = stntDsbdInfoMapper.selectStntDsbdChptUnitKwgCombo(paramData);
            // (전체) 추가
            if(CollectionUtils.isNotEmpty(kwgComboList)) {
                CamelHashMap allCombo = new CamelHashMap();
                allCombo.putAll(kwgComboList.get(0));

                allCombo.put("kwgMainId",0);
                allCombo.put("kwgNm","전체");
                // kwgComboList에서 stdAt이 'Y'인 값이 하나라도 있는지 확인
                boolean isStudyAt = kwgComboList.stream()
                    .anyMatch(map -> "Y".equals(map.get("stdAt")));
                allCombo.put("stdAt", isStudyAt ? "Y" : "N");
                allCombo.put("usdScr",kwgComboList.get(0).get("totalUsdScr"));
                allCombo.put("unitLastLesnAt","N");
                allCombo.put("kwgLastLesnAt","N");

                kwgComboList.add(0, allCombo);
            }

            chptUnitKwgCombo = AidtCommonUtil.filterToList(listItem, kwgComboList);
            rtnMap.put("chptUnitKwgCombo", chptUnitKwgCombo);


            allSrhYn = "Y";
            rtnMap.put("allSrhYn", allSrhYn);

        }else {
            rtnMap.put("chptUnitKwgCombo", null);
            rtnMap.put("allSrhYn", allSrhYn);
        }

        List<Map> rawCncptUsdList = new ArrayList<>();
        if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
            rawCncptUsdList = "Y".equals(allSrhYn)
                            ? stntDsbdInfoMapper.selectStntDsbdCncptUsdByDateList_Main(paramData) // 단원에 대한 일자별 이해도 조회
                            : stntDsbdInfoMapper.selectStntDsbdCncptUsdList_Main(paramData);      // 단원별 학생 분포 정보 // 기존
        } else {
            rawCncptUsdList = "Y".equals(allSrhYn)
                            ? stntDsbdInfoMapper.selectStntDsbdCncptUsdByDateList(paramData) // 단원에 대한 일자별 이해도 조회
                            : stntDsbdInfoMapper.selectStntDsbdCncptUsdList(paramData);      // 단원별 학생 분포 정보 // 기존
        }

        // 반올림 처리
        for (Map map : rawCncptUsdList) {
            if (map.containsKey("usdScr")) {
                Object usdScrObj = map.get("usdScr");
                double usdScr = 0.0;

                if (usdScrObj instanceof Number) {
                    usdScr = ((Number) usdScrObj).doubleValue();
                }

                int roundedValue = (int) Math.round(usdScr);
                map.put("usdScr", roundedValue);
            }

            if (map.containsKey("prevUsdScr")) {
                Object prevUsdScrObj = map.get("prevUsdScr");
                double prevUsdScr = 0.0;

                if (prevUsdScrObj instanceof Number) {
                    prevUsdScr = ((Number) prevUsdScrObj).doubleValue();
                }

                int roundedValue = (int) Math.round(prevUsdScr);
                map.put("prevUsdScr", roundedValue);
            }
        }

        // 필터링하여 필요한 필드만 추출
        cncptUsdList = AidtCommonUtil.filterToList(listItem1, rawCncptUsdList);
        rtnMap.put("cncptUsdList", cncptUsdList);

        // metaId와 kwgMainId를 사용하여 이름 조회
        Set<String> idSet = new HashSet<>();

        // 유효한 ID만 추가
        if (metaId != null && !metaId.isEmpty()) {
            idSet.add(metaId);
        }
        if (kwgMainId != null && !kwgMainId.isEmpty()) {
            idSet.add(kwgMainId);
        }

        // ID가 있는 경우에만 조회
        if (!idSet.isEmpty()) {
            // 한 번의 쿼리로 모든 ID에 대한 정보 조회
            Map<String, Object> param = new HashMap<>();
//            param.put("ids", String.join("','", idSet));
            param.put("idsList", new ArrayList<>(idSet)); //CSAP 25.08.12.lhr
            List<Map> metaInfoList = tchDsbdMapper.findMetaInfoList(param);

            // ID를 키로 하는 맵 생성
            Map<String, String> idNameMap = new HashMap<>();
            for (Map metaInfo : metaInfoList) {
                idNameMap.put(metaInfo.get("id").toString(), metaInfo.get("val").toString());
            }

            // 조회된 이름 정보를 응답에 추가
            if (metaId != null && !metaId.isEmpty() && idNameMap.containsKey(metaId)) {
                rtnMap.put("unitName", idNameMap.get(metaId));
            }

            if (kwgMainId != null && !kwgMainId.isEmpty() && idNameMap.containsKey(kwgMainId)) {
                rtnMap.put("kwgName", idNameMap.get(kwgMainId));
            }
        }

        return rtnMap;
    }

    // 개념별 이해도 상세
    @Transactional(readOnly = true)
    public Object selectStntDsbdConceptUsdDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> listItem1 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeCd", "trgtSeNm", "tabId", "trgtNm", "rpOthbcAt"
        );

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        Map<Object, Object> rtnMap = new HashMap<>();

        String allSrhYn = (String) paramData.get("allSrhYn");

        List<Map> usdSrcList  = new ArrayList<>();
        if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
            usdSrcList = stntDsbdInfoMapper.selectStntDsbdConceptUsdDetail_Main(pagingParam);
        } else {
            usdSrcList = stntDsbdInfoMapper.selectStntDsbdConceptUsdDetail(pagingParam);
        }

        if (!usdSrcList.isEmpty()) {
            total = (long) usdSrcList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(usdSrcList, pageable, total);

        LocalDate date = LocalDate.of(NumberUtils.toInt(paramData.get("stdDt").toString().substring(0,4)), NumberUtils.toInt(paramData.get("stdDt").toString().substring(4,6)), NumberUtils.toInt(paramData.get("stdDt").toString().substring(6,8)));
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        rtnMap.put("stdDtDay",paramData.get("stdDt").toString().substring(2,4)+"-"+paramData.get("stdDt").toString().substring(4,6)+"-"+paramData.get("stdDt").toString().substring(6,8)+"("+dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)+")");
        rtnMap.put("usdSrcList", AidtCommonUtil.filterToList(listItem1, usdSrcList));
        rtnMap.put("page",page);


        return rtnMap;
    }

    /**
     * 학습맵 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdChptUnitInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //List<String> listItem1 = Arrays.asList(
        //        "metaId", "unitNum", "unitNm", "unitLastLesnAt"
        //);
        List<String> listItem = Arrays.asList(
                "metaId", "unitNum", "kwgMainId", "kwgNm", "usdScr",
                "prevMetaId", "prevUnitNum", "prevKwgMainId", "prevKwgNm", "prevUsdScr"
        );

        //Map<Object, Object> chptUnitInfo = AidtCommonUtil.filterToMap(listItem1, stntDsbdInfoMapper.selectStntDsbdChptUnitInfo(paramData));
        List<LinkedHashMap<Object, Object>> stdMapUsdList = AidtCommonUtil.filterToList(listItem, stntDsbdInfoMapper.selectStntDsbdStdMapUsdList(paramData));

        //returnMap.put("chptUnitInfo", chptUnitInfo);
        returnMap.put("stdMapUsdList", stdMapUsdList);
        //returnMap.put("cncptLinkedList", null); //추후 수정할것.api 추가할 부분.

        return returnMap;
    }

    /**
     * 학습맵 이해도 (개념)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStdCncptUsdInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "kwgNm", "kwgUsdScr", "cncptCurri","kwgMainId"
        );
        List<String> listItem = Arrays.asList(
                "kwgMainId", "kwgNm", "kwgUsdScr"
        );

        Map<Object, Object> cncptUsdInfo = AidtCommonUtil.filterToMap(listItem1, stntDsbdInfoMapper.selectStntDsbdStdCncptUsdInfo(paramData));

        List<LinkedHashMap<Object, Object>> stdMapUsdList = AidtCommonUtil.filterToList(listItem, stntDsbdInfoMapper.selectStntDsbdStdMapKwgList(paramData));
        cncptUsdInfo.put("kwgUsdList", stdMapUsdList);

        returnMap.put("cncptUsdInfo", cncptUsdInfo);

        return returnMap;
    }

    /**
     * 학습맵 이해도 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStdMapUsdInfo(Map<String, Object> paramData, Pageable pageable) throws Exception {

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeCd", "trgtSeNm", "trgtNm", "rpOthbcAt"
        );

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        Map<Object, Object> rtnMap = new HashMap<>();
        Map<Object, Object> cncptPathNm = stntDsbdInfoMapper.selectStntDsbdCncptPathNmInfo(paramData);
        if(cncptPathNm != null){
            rtnMap= cncptPathNm;
        }else{
            rtnMap.put("cncptPathNm","");
        }

        List<Map> cncptStdtList = stntDsbdInfoMapper.selectStntDsbdStdMapCncptStdtList(pagingParam);

        if (!cncptStdtList.isEmpty()) {
            total = (long) cncptStdtList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(cncptStdtList, pageable, total);

        rtnMap.put("usdSrcList", AidtCommonUtil.filterToList(listItem2, cncptStdtList));
        rtnMap.put("page",page);

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntDsbdStatusStudyMapInd(Map<String, Object> paramData) throws Exception {
         var returnMap = new LinkedHashMap<>();

         Map<Object, Object> studyMapDetailmap = stntDsbdInfoMapper.findStntDsbdStatusStudyMapDetail(paramData);
         List<Map> studyMapDetaillist = stntDsbdInfoMapper.findStntDsbdStatusStudyMapDetail_list(paramData);

         returnMap.put("unitNm", MapUtils.getString(studyMapDetailmap, "unitNm"));
         returnMap.put("kwgAchNum", MapUtils.getInteger(studyMapDetailmap, "kwgAchNum"));

         returnMap.put("stdMapDetailList", studyMapDetaillist);

         return returnMap;
     }
}