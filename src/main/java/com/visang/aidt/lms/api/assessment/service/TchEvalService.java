package com.visang.aidt.lms.api.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchSlfperEvalMapper;
import com.visang.aidt.lms.api.kafka.service.KafkaBatchService;
import com.visang.aidt.lms.api.mq.service.AssessmentSubmittedService;
import com.visang.aidt.lms.api.repository.EvlInfoRepository;
import com.visang.aidt.lms.api.repository.EvlResultInfoRepository;
import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import com.visang.aidt.lms.api.repository.entity.EvlResultInfoEntity;
import com.visang.aidt.lms.api.repository.entity.StdtRegInfoEntity;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class TchEvalService {

    private final EvlInfoRepository evlInfoRepository;
    private final EvlResultInfoRepository evlResultInfoRepository;

    private final TchEvalMapper tchEvalMapper;
    private final TchSlfperEvalService tchSlfperEvalService;
    private final TchSlfperEvalMapper tchSlfperEvalMapper;

    private final TchReportEvalService tchReportEvalService;
    private final AssessmentSubmittedService assessmentSubmittedService;

    private final KafkaBatchService kafkaBatchService;

    /**
     * (평가).평가 확인 요소 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findEvalCountByElement(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "count": 10,
            }
        """).toMap();
    }

    /**
     * (평가).평가 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        List<String> evalListItem = new ArrayList<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        //List<Map> evalInfoList = (List<Map>) tchEvalMapper.findTchEvalListEvalList(pagingParam);

        List<Map> evalInfoList = new ArrayList<>();
        evalListItem = Arrays.asList("id", "evlNm", "eamMth", "tmprStrgAt", "evlSttsCd", "evlSttsNm", "evlPrgDt", "evlCpDt", "targetCnt", "submitCnt", "setsId", "creatorTyYn", "regDt", "delYn");
        evalInfoList = (List<Map>) tchEvalMapper.findTchEvalListEvalList(pagingParam);
        /*
        if ("N".equals(paramData.get("tmprStrgAt"))) {
            evalListItem = Arrays.asList("no", "id", "eamMth", "evlNm", "eamTrget", "evlSttsCd", "evlSttsNm", "evlPrgDt", "evlCpDt", "targetCnt", "submitCnt", "isEncouragement", "reportLinkYn");
            evalInfoList = (List<Map>) tchEvalMapper.findTchEvalListEvalList(pagingParam);
        } else {
            evalListItem = Arrays.asList("no", "id", "eamMth", "evlNm", "regDt");
            evalInfoList = (List<Map>) tchEvalMapper.findTchEvalListEvalListTmpr(pagingParam);
        }
         */

        if (!evalInfoList.isEmpty()) {
            total = (long) evalInfoList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(evalInfoList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        List<String> finalTaskListItem = evalListItem;
        List<LinkedHashMap<Object, Object>> evalList = CollectionUtils.emptyIfNull(evalInfoList).stream().map(s -> {
            var tgtMap = new LinkedHashMap<>();
            if(Objects.isNull(s)) return tgtMap;
            var srcMap = new ObjectMapper().convertValue(s, Map.class);
            finalTaskListItem.forEach(ss -> {
                if ("isEncouragement".equals(ss)){
                    if (MapUtils.getInteger(srcMap, ss) == 1) {
                        tgtMap.put(ss, true);
                    } else {
                        tgtMap.put(ss, false);
                    }
                } else {
                    tgtMap.put(ss, srcMap.get(ss));
                }
            });
            return tgtMap;
        }).toList();

        returnMap.put("evalList", evalList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * (평가).평가 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findEvalInfo(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlSeCd", "eamTrget", "eamExmNum", "timTime", "rwdSetAt", "rwdPoint", "pdEvlStDt", "pdEvlEdDt", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm", "tmprStrgAt", "rpOthbcAt", "rpOthbcDt", "aiTutSetAt", "lesnEvalAt", "reportLinkYn", "submitCnt");

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalInfoEvalInfo(paramData));

        return evalInfoMap;
    }

    /**
     * (평가).평가 정보 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> modifyEval(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (평가).평가 다시 시작
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> removeEvalResult(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (평가).평가 결과 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findEvalResult(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (평가).평가 현황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findEvalStatus(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlPrgDt", "evlCpDt", "timTime", "targetCnt", "examCnt", "eamExmNum");
        List<String> studentInfoItem = Arrays.asList("id", "userId", "flnm", "evlAdiSec", "isLogout", "isNormalEvl", "evalResultDetList");
        List<String> evalResultDetItem = Arrays.asList("id", "evlResultId", "evlIemId", "subId", "subMitAnw", "subMitAnwUrl");

        resultMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalPreviewEvalInfo(paramData));

        List<LinkedHashMap<Object, Object>> evalResultDetList = AidtCommonUtil.filterToList(evalResultDetItem, tchEvalMapper.findTchEvalStatusEvalResultDet(paramData));
        List<LinkedHashMap<Object, Object>> studentInfoList = CollectionUtils.emptyIfNull(tchEvalMapper.findTchEvalStatusStudentInfo(paramData)).stream()
                .map(s -> {
                    List<LinkedHashMap<Object, Object>> evalResultList = CollectionUtils.emptyIfNull(evalResultDetList).stream()
                            .filter(t -> {
                                return StringUtils.equals(MapUtils.getString(s,"eriId"), MapUtils.getString(t,"evlResultId"));
                            }).toList();

                    s.put("evalResultDetList", evalResultList);
                    return AidtCommonUtil.filterToMap(studentInfoItem, s);
                }).toList();


        resultMap.put("StudentInfo", studentInfoList);

        return resultMap;
    }

    /**
     * (평가).평가 응시시간 추가하기 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findEvalTimeAdd(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Optional<EvlInfoEntity> result;

        if( paramData.get("evlId") instanceof String ) {
            String id = (String) paramData.get("evlId");
            result =  evlInfoRepository.findById(Long.parseLong(id));
        } else {
            Integer id = (Integer) paramData.get("evlId");
            result =  evlInfoRepository.findById(id.longValue());
        }

        if( result.isEmpty() ) {
            returnMap.put("errorMsg", "평가 id를 찾을 수 없습니다.");
            return returnMap;
        }

        EvlInfoEntity evlInfoEntity = result.get();

        List<EvlResultInfoEntity> studentListList = evlInfoEntity.getEvlResultInfoList();

        List<Map<String, Object>> studentListMap = new ArrayList<>();

        studentListList.stream().forEach( evlResultInfo -> {
            StdtRegInfoEntity stdt = evlResultInfo.getStdtRegInfoEntity();
            Map<String, Object> evlResultInfoMap = new HashMap<>();

            evlResultInfoMap.put("userId", stdt.getUserId());
            evlResultInfoMap.put("flnm", stdt.getFlnm());
            evlResultInfoMap.put("evlAdiSec", evlResultInfo.getEvlAdiSec());

            studentListMap.add(evlResultInfoMap);
        });

        returnMap.put("id", evlInfoEntity.getId());
        returnMap.put("evlPrgDt", evlInfoEntity.getEvlPrgDt());
        returnMap.put("evlCpDt", evlInfoEntity.getEvlCpDt());
        returnMap.put("timTime", evlInfoEntity.getTimTime());
        returnMap.put("targetCnt", studentListList.size());
        returnMap.put("studentList", studentListMap);

        return returnMap;
    }


    /**
     * (평가).평가 응시시간 추가하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    @Transactional(transactionManager = "transactionManager" )
    public Map<String, Object> createEvalTimeAdd(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new HashMap<>();

        Integer evlId = MapUtils.getInteger(paramData, "evlId");
        Optional<EvlInfoEntity> evlInfoEntityOptional = evlInfoRepository.findById(evlId.longValue());

        if( evlInfoEntityOptional.isEmpty() ) {
            returnMap.put("errorMsg", "평가 id를 찾을 수 없습니다.");
            return returnMap;
        }

        EvlInfoEntity evlInfo = evlInfoEntityOptional.get();
        Integer evlAdiSec = MapUtils.getInteger(paramData, "evlAdiSec");
        boolean isSelAll = MapUtils.getBoolean(paramData, "isSelAll", false);

        String wrterId = evlInfo.getWrterId();                      // 수정자
        Date mdfyDt = new Date();                                   // 수정일시

        List<EvlResultInfoEntity> resultInfoEntityList = evlResultInfoRepository.findByEvlId(evlInfo);
        if( isSelAll ) {
            resultInfoEntityList.forEach( eri -> {
                eri.setEvlAdiSec((ObjectUtils.isEmpty(eri.getEvlAdiSec()) ? 0 : eri.getEvlAdiSec()) + evlAdiSec);
                eri.setMdfyDt(mdfyDt);
                eri.setMdfr("test");
                evlResultInfoRepository.save(eri);
            });

            int result1 = tchEvalMapper.modifyEvalTimeAdd(paramData);
            log.info("result1:{}", result1);
        } else {
            List<String> studentList = (List<String>) paramData.get("studentList");
            if (studentList != null && !studentList.isEmpty()) {
                resultInfoEntityList.stream()
                        .filter(eri -> studentList.contains(eri.getMamoymId()))
                        .forEach(eri -> {
                    eri.setEvlAdiSec((ObjectUtils.isEmpty(eri.getEvlAdiSec()) ? 0 : eri.getEvlAdiSec()) + evlAdiSec);
                    eri.setMdfyDt(mdfyDt);
                    eri.setMdfr("test");
                });
            } else {
                log.warn("studentList가 null이거나 비어있습니다.");
            }

        }

        // 평가 제한시간
        LocalTime timTime = LocalTime.parse(evlInfo.getTimTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        int timTimeSec = timTime.toSecondOfDay();

        // 추가시간 최댓값
        int max = resultInfoEntityList.stream().mapToInt(eri -> eri.getEvlAdiSec() == null? 0 : eri.getEvlAdiSec()).max().orElse(0);

        // 시작시간 + 제한시간 + 추가시간
        LocalDateTime localDateTime = ((Timestamp) evlInfo.getEvlPrgDt()).toLocalDateTime().plusSeconds(timTimeSec + max);//timTimeSec + max
        evlInfo.setEvlCpDt(java.sql.Timestamp.valueOf(localDateTime));

        return findEvalTimeAdd(paramData);
    }

    /**
     * (평가).평가 응시시간 추가하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> modifyEvalTimeAdd(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (평가).평가 현황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findEvalPreview(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlPrgDt", "evlCpDt", "timTime", "targetCnt", "examCnt", "eamExmNum", "eamTrgetYn");
        List<String> evalIemInfoItem = Arrays.asList("id", "evlIemId", "subId", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen");
        List<String> studentInfoItem = Arrays.asList("id", "userId", "flnm", "setsId");

        resultMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalPreviewEvalInfo(paramData));

        if( "Y".equals(String.valueOf(resultMap.get("eamTrgetYn"))) ) {
            List<LinkedHashMap<Object, Object>> studentInfoList = AidtCommonUtil.filterToList(studentInfoItem, tchEvalMapper.findTchEvalPreviewStudentList(paramData));
            resultMap.put("studentList", studentInfoList);
        }

        List<LinkedHashMap<Object, Object>> evalIemInfoList = AidtCommonUtil.filterToList(evalIemInfoItem, tchEvalMapper.findTchEvalPreviewEvalIemInfo(paramData));

        resultMap.put("evalIemList", evalIemInfoList);

        return resultMap;
    }

    /**
     * 조회된 테이블의 결과 값을 최대한 활용하여 중복 조회되지 않도록 수정
     * ※ 정렬 순서가 기존과 다를 수 있지만 데이터 return 구조는 동일
     * - evlPrgDte와 timTime 데이터로 end date 추출
     * - evl_result_info 조회 정보를 활용하여 evl_result_detail 단일 테이블 조회
     * @param paramData
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findEvalResultStatus3(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> evalInfoMap = tchEvalMapper.selectEvlInfoDetail(paramData);
        if (MapUtils.isEmpty(evalInfoMap)) {
            return returnMap;
        }
        String evlCpDt = MapUtils.getString(evalInfoMap, "pdEvlEdDt");
        evalInfoMap.remove("pdEvlEdDt");
        if (StringUtils.isEmpty(evlCpDt)) {
            String evlPrgDt = MapUtils.getString(evalInfoMap, "evlPrgDt");
            String timTime = MapUtils.getString(evalInfoMap, "timTime");
            if (StringUtils.isNotEmpty(evlPrgDt) && StringUtils.contains(timTime, ":")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String[] timeArr = timTime.split(":");
                int seconds = NumberUtils.toInt(timeArr[0]) * 60 + NumberUtils.toInt(timeArr[1]);
                Date startDate = null;
                try {
                    startDate = sdf.parse(evlPrgDt);
                } catch (ParseException e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
                if (startDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.SECOND, seconds);
                    evlCpDt = sdf.format(cal.getTime());
                }
            }
        }
        evalInfoMap.put("evlCpDt", evlCpDt);

        Map<String, Object> evlResultStatMap = tchEvalMapper.selectEvlResultStat(paramData);
        if (MapUtils.isNotEmpty(evlResultStatMap)) {
            evalInfoMap.putAll(evlResultStatMap);
        }

        List<Map<String, Object>> evalIemInfoList = new LinkedList<>();
        List<Map<String, Object>> evalIemInfoListTemp = tchEvalMapper.selectEvlArticleList(paramData);
        // evl_iem_info 테이블 id로 row data 조회해서 값 세팅 하기 위함
        Map<Integer, Map<String, Object>> evlItemInfoMapFromId = new HashMap<>();
        // evl_iem_info의 evl_iem_id을 조건으로 id값을 추출하기 위함
        Map<Integer, Integer> eiiIdMapFromEvlIemId = new HashMap<>();
        for (Map<String, Object> map : evalIemInfoListTemp) {
            int id = MapUtils.getInteger(map, "id", 0);
            if (id == 0) {
                continue;
            }
            evlItemInfoMapFromId.put(id, map);
            int evlIemId = MapUtils.getInteger(map, "evlIemId", 0);
            if (evlIemId == 0) {
                continue;
            }
            eiiIdMapFromEvlIemId.put(evlIemId, id);
        }

        List<Map<String, Object>> evalAdiSecStntList = new LinkedList<>();
        Map<Integer, Map<String, Object>> evlResultUserMapFromId = new HashMap<>();
        // evl_result_info 테이블 조회 결과로 id 추출
        List<Integer> evlResultIdList = new ArrayList<>();
        List<Map<String, Object>> evlResultUserInfoList = tchEvalMapper.selectEvlResultUserInfoList(paramData);
        for (Map<String, Object> map : evlResultUserInfoList) {
            int evlResultId = MapUtils.getInteger(map, "id", 0);
            if (evlResultId == 0) {
                continue;
            }
            evlResultIdList.add(evlResultId);
            int evlAdiSec = MapUtils.getInteger(map, "evlAdiSec", 0);
            if (evlAdiSec > 0) {
                evalAdiSecStntList.add(map);
            }
            evlResultUserMapFromId.put(evlResultId, map);
        }

        // evl_result_info 테이블 조회 결과로 추출된 id가 있을 경우 해당 id 목록으로 조건 처리
        if (CollectionUtils.isNotEmpty(evlResultIdList)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("evlResultIdList", evlResultIdList);
            // loop 돌면서 세팅하지 않고 한 번에 조건 처리
            List<Map<String, Object>> resultDetailList = tchEvalMapper.selectEvlResultDetailList(paramMap);
            for (Map<String, Object> map : resultDetailList) {

                int evlResultId = MapUtils.getInteger(map, "evlResultId", 0);
                int evlIemId = MapUtils.getInteger(map, "evlIemId", 0);
                if (evlResultId == 0 || evlIemId == 0) {
                    continue;
                }

                // 이전 로직에서 추출한 user 정보 활용
                Map<String, Object> userMap = evlResultUserMapFromId.get(evlResultId);
                map.put("id", MapUtils.getInteger(userMap, "userIdx"));
                map.put("userId", MapUtils.getString(userMap, "userId"));
                map.put("flnm", MapUtils.getString(userMap, "flnm"));

                // 이전 로직에서 추출한 evlIemId 별 evl_iem_info 데이터 활용
                int eiiId = eiiIdMapFromEvlIemId.get(evlIemId);
                Map<String, Object> evlMap = evlItemInfoMapFromId.get(eiiId);
                if (MapUtils.isEmpty(evlMap)) {
                    continue;
                }
                Object evalResultDetObj = evlMap.get("evalResultDetList");
                List<Map<String, Object>> evalResultDetList = null;
                if (evalResultDetObj == null) {
                    evalResultDetList = new LinkedList<>();
                } else {
                    evalResultDetList = (List<Map<String, Object>>) evalResultDetObj;
                }

                map.remove("evlResultId");
                evalResultDetList.add(map);
                evlMap.put("evalResultDetList", evalResultDetList);
            }
        }

        for (Map<String, Object> map : evalIemInfoListTemp) {
            evalIemInfoList.add(map);
        }

        evalInfoMap.put("evalIemList", evalIemInfoList);
        evalInfoMap.put("evalAdiSecStntList", evalAdiSecStntList);

        returnMap.put("evalInfo", evalInfoMap);

        return returnMap;
    }

    /**
     * 첫 번쨰 튜닝 버젼
     * - 불필요한 연산 DB 쿼리에서 제거
     * - join 구조 정리
     * - group by join 구조 subquery로 정리
     * - loop 돌면서 쿼리 호출하는 부분 한 번에 가져와서 map으로 처리
     * @param paramData
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findEvalResultStatus2(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> evalInfoMap = tchEvalMapper.selectEvlInfoDetail(paramData);
        if (MapUtils.isEmpty(evalInfoMap)) {
            return returnMap;
        }
        String evlCpDt = MapUtils.getString(evalInfoMap, "pdEvlEdDt");
        evalInfoMap.remove("pdEvlEdDt");
        if (StringUtils.isEmpty(evlCpDt)) {
            String evlPrgDt = MapUtils.getString(evalInfoMap, "evlPrgDt");
            String timTime = MapUtils.getString(evalInfoMap, "timTime");
            if (StringUtils.isNotEmpty(evlPrgDt) && StringUtils.contains(timTime, ":")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String[] timeArr = timTime.split(":");
                int seconds = NumberUtils.toInt(timeArr[0]) * 60 + NumberUtils.toInt(timeArr[1]);
                Date startDate = null;
                try {
                    startDate = sdf.parse(evlPrgDt);
                } catch (ParseException e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
                if (startDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.SECOND, seconds);
                    evlCpDt = sdf.format(cal.getTime());
                }
            }
        }
        evalInfoMap.put("evlCpDt", evlCpDt);

        Map<String, Object> evlResultStatMap = tchEvalMapper.selectEvlResultStat(paramData);
        if (MapUtils.isNotEmpty(evlResultStatMap)) {
            evalInfoMap.putAll(evlResultStatMap);
        }

        List<Map<String, Object>> evalIemInfoList = new LinkedList<>();
        List<Map<String, Object>> evalIemInfoListTemp = tchEvalMapper.selectEvlArticleList(paramData);
        Map<Integer, Map<String, Object>> evlItemInfoMapFromId = new HashMap<>();
        List<Integer> eiiIdList = new LinkedList<>();
        for (Map<String, Object> map : evalIemInfoListTemp) {
            int id = MapUtils.getInteger(map, "id", 0);
            if (id == 0) {
                continue;
            }
            eiiIdList.add(id);
            evlItemInfoMapFromId.put(id, map);
        }

        List<Map<String, Object>> evalAdiSecStntList = new LinkedList<>();
        List<Map<String, Object>> evlResultUserInfoList = tchEvalMapper.selectEvlResultUserInfoList(paramData);
        for (Map<String, Object> map : evlResultUserInfoList) {
            int evlAdiSec = MapUtils.getInteger(map, "evlAdiSec", 0);
            if (evlAdiSec > 0) {
                evalAdiSecStntList.add(map);
            }
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("eiiIdList", eiiIdList);
        // loop 돌면서 세팅하지 않고 한 번에 조건 처리
        List<Map<String, Object>> resultDetailList = tchEvalMapper.selectEvlResultDetailList2(paramMap);
        for (Map<String, Object> map : resultDetailList) {
            int eiiId = MapUtils.getInteger(map, "eiiId", 0);
            if (eiiId == 0) {
                continue;
            }
            Map<String, Object> evlMap = evlItemInfoMapFromId.get(eiiId);
            if (MapUtils.isEmpty(evlMap)) {
                continue;
            }
            Object evalResultDetObj = evlMap.get("evalResultDetList");
            List<Map<String, Object>> evalResultDetList = null;
            if (evalResultDetObj == null) {
                evalResultDetList = new LinkedList<>();
            }
            else {
                evalResultDetList = (List<Map<String, Object>>) evalResultDetObj;
            }
            map.remove("eiiId");
            map.remove("evlId");
            evalResultDetList.add(map);
            evlMap.put("evalResultDetList", evalResultDetList);
        }

        for (Map<String, Object> map : evalIemInfoListTemp) {
            evalIemInfoList.add(map);
        }

        evalInfoMap.put("evalIemList", evalIemInfoList);
        evalInfoMap.put("evalAdiSecStntList", evalAdiSecStntList);

        returnMap.put("evalInfo", evalInfoMap);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findEvalResultStatus(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlPrgDt", "evlCpDt", "timTime", "isEvlAdiSec", "targetCnt", "examCnt", "eamExmNum", "evlSttsCd", "evlSttsNm");
        List<String> evalIemInfoItem = Arrays.asList("id", "evlIemId", "subId", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen", "isEditable", "correctRate", "articleType", "mrkTy", "fullCount");
        List<String> evalResultDetItem = Arrays.asList("evlIemId", "subId", "subMitAnw", "subMitAnwUrl", "userIdx", "userId", "flnm", "submAt","errata", "actvtnAt");

        List<String> subMitAnwStntItem = Arrays.asList("evlIemId", "subId", "subMitAnw", "anwStntList");
        List<String> anwStntItem = Arrays.asList("userIdx", "userId", "flnm");

        List<String> evalAdiSecStntItem = Arrays.asList("id", "userIdx", "userId", "flnm", "evlAdiSec");

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalResultStatusEvalInfo(paramData));
        List<LinkedHashMap<Object, Object>> evalAdiSecStntList = AidtCommonUtil.filterToList(evalAdiSecStntItem, tchEvalMapper.findTchEvalResultStatusEvalAdiSecStnt(paramData));
        List<Map> evalResultDetList = tchEvalMapper.findTchEvalResultStatusEvalResultDet(paramData);

        List<Map> subMitAnwStntList = tchEvalMapper.findTchEvalResultStatusSubMitAnwStnt(paramData);
        List<Map> anwStntList = tchEvalMapper.findTchEvalResultStatusAnwStudent(paramData);

        if (!evalInfoMap.isEmpty() && MapUtils.getInteger(evalInfoMap, "isEvlAdiSec") == 1) {
            evalInfoMap.put("isEvlAdiSec", true);
        } else {
            evalInfoMap.put("isEvlAdiSec", false);
        }

        List<Map> evalIemInfoListMap = tchEvalMapper.findTchEvalResultStatusEvalIemInfo(pagingParam);
        if (!evalIemInfoListMap.isEmpty()) {
            total = (long) evalIemInfoListMap.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(evalIemInfoListMap, pageable, total);

        List<LinkedHashMap<Object, Object>> evalIemInfoList = AidtCommonUtil.filterToList(evalIemInfoItem, evalIemInfoListMap).stream()
                .map(s -> {
                    List<LinkedHashMap<Object, Object>> returnDetList = CollectionUtils.emptyIfNull(evalResultDetList).stream()
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId")))
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                            .map(r -> {
                                return AidtCommonUtil.filterToMap(evalResultDetItem, r);
                            }).toList();
                    s.put("evalResultDetList" , returnDetList);

                    List<LinkedHashMap<Object, Object>> returnSubMitAnwStntList = CollectionUtils.emptyIfNull(subMitAnwStntList).stream()
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId")))
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                            .map(r -> {
                                List<LinkedHashMap<Object, Object>> returnAnwStntList = CollectionUtils.emptyIfNull(anwStntList).stream()
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"evlIemId"), MapUtils.getString(g,"evlIemId")))
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"subId"), MapUtils.getString(g,"subId")))
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"subMitAnw"), MapUtils.getString(g,"subMitAnw")))
                                        .map(g -> {
                                            return AidtCommonUtil.filterToMap(anwStntItem, g);
                                        }).toList();
                                r.put("anwStntList" , returnAnwStntList);
                                return AidtCommonUtil.filterToMap(subMitAnwStntItem, r);
                            }).toList();
                    s.put("subMitAnwStntList" , returnSubMitAnwStntList);

                    return s;
                }).toList();

        evalInfoMap.put("evalIemList", evalIemInfoList);
        evalInfoMap.put("evalAdiSecStntList", evalAdiSecStntList);

        // 학생별 정답률, 제출률
        List<String> tchEvlStntRateItem = Arrays.asList("userId", "eakSttsCd", "eakSttsNm","crrRate","submRate");
        var stntRateList = AidtCommonUtil.filterToList(tchEvlStntRateItem, tchEvalMapper.findTchEvlStntRateList(paramData));
        evalInfoMap.put("stntRateList", stntRateList);

        returnMap.put("evalInfo", evalInfoMap);

        if ("Y".equals(MapUtils.getString(paramData, "pageYn", "N"))) {
            returnMap.put("page",page);
        }

        return returnMap;
    }

    public Map<String, Object> modifyEvalStart(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlSttsCd", "evlSttsNm", "prgEvlAt");
        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("resultOk", true);
        resultData.put("resultMsg", "평가 정보 수정(시작하기)");

        // 1. 같은 학급 내에서 진행중인 다른 (수업중) 평가 정보 조회 (중복 체크용)
        Map<String, Object> result = tchEvalMapper.findTchEvalStartPrgEvalInfo(paramData);
        log.info("result:{}", result);

        if (ObjectUtils.isEmpty(result)) {
            /* 중복으로 진행중인 수업중 평가 없음 (새로운 평가 시작) */
            /* 테이블 데이터 검증 */
            if (validateAndRepairEvalData(paramData)) {
                resultData.put("evlId", paramData.get("evlId"));
                resultData.put("resultOk", false);
                resultData.put("resultMsg", "평가 데이터 이상으로 인한 평가 정보 수정(시작하기) 실패");
                resultData.put("errCode", "setIdErr");

                return resultData;
            }

            int result1 = tchEvalMapper.modifyTchEvalStartEvalInfo(paramData);
            log.info("result1:{}", result1);

            int result2 = tchEvalMapper.modifyTchEvalStartEvalResultInfo(paramData);
            log.info("result2:{}", result2);


            resultData.put("resultData", AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalStartEvalInfo(paramData)));
        } else {
            /* 중복으로 진행중인 수업중 평가 있음 (기존 진행중인 평가로 진행) */
            resultData.put("resultData", AidtCommonUtil.filterToMap(evalInfoItem, result));
        }

        return resultData;
    }

    /**
     * 평가 데이터 검증 및 복구
     *
     * @param paramData 평가 파라미터
     * @return 성공 여부
     */
    private boolean validateAndRepairEvalData(Map<String, Object> paramData) throws Exception {
        /* 평가 데이터 조회 */
        Map<String, Object> checkEvlData = tchEvalMapper.checkEvlDataByEvlId(paramData);

        /* evl_iem_info, evl_result_info, evl_result_detail 중 값이 없는 경우 이상 데이터 */
        if (isDataIncomplete(checkEvlData)) {
            /* 각 테이블 필요 데이터 생성 */
            repairMissingEvalData(paramData, checkEvlData);

            /* 재검증 */
            Map<String, Object> recheckEvlData = tchEvalMapper.checkEvlDataByEvlId(paramData);
            if (isDataIncomplete(recheckEvlData)) {
                String setId = MapUtils.getString(recheckEvlData, "setsId", "");
                String evlId = MapUtils.getString(paramData, "evlId", "");

                log.error("setId 이상으로 인한 평가 데이터 복구 실패 - errCode: setIdErr, setId: {}, evlId: {}", setId, evlId);
                return true;
            }
        }

        return false;
    }

    /**
     * 평가 데이터 완성도 체크
     *
     * @param checkData 체크할 데이터
     * @return 불완전한 데이터 여부
     */
    private boolean isDataIncomplete(Map<String, Object> checkData) {
        int cntEii = MapUtils.getIntValue(checkData, "cntEii", 0);
        int cntEri = MapUtils.getIntValue(checkData, "cntEri", 0);
        int cntErd = MapUtils.getIntValue(checkData, "cntErd", 0);

        return cntEii == 0 || cntEri == 0 || cntErd == 0;
    }

    /**
     * 누락된 평가 데이터 복구
     *
     * @param paramData 평가 파라미터
     * @param checkData 체크 결과 데이터
     */
    private void repairMissingEvalData(Map<String, Object> paramData, Map<String, Object> checkData) throws Exception {
        /* evlId로 sets_id 조회 */
        Map<String, Object> setsIdByEvlId = tchEvalMapper.findSetsIdByEvlId(paramData);
        String setsId = MapUtils.getString(setsIdByEvlId, "setsId");
        paramData.put("setsId", setsId);

        int cntEii = MapUtils.getIntValue(checkData, "cntEii", 0);
        int cntEri = MapUtils.getIntValue(checkData, "cntEri", 0);
        int cntErd = MapUtils.getIntValue(checkData, "cntErd", 0);

        if (cntEii == 0) {
            /* evl_iem_info 생성 */
            tchEvalMapper.createTchEvalCreateForTextbk_evlIemInfo(paramData);
        }

        if (cntEri == 0) {
            /* evl_result_info 생성 */
            tchEvalMapper.createTchEvalCreateForTextbk_evlResultInfo(paramData);
        }

        if (cntErd == 0) {
            /* evl_resutl_detail 생성 */
            tchEvalMapper.createTchEvalCreateForTextbk_evlResultDetail(paramData);
        }
    }

    public Object modifyEvalEnd(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm","rptAutoOthbcAt");

        // 타임아웃여부(timeoutAt) 파라미터 디폴트값 셋팅
        String timeoutAt = MapUtils.getString(paramData, "timeoutAt");
        paramData.put("timeoutAt", StringUtils.isEmpty(timeoutAt) ? "N" : timeoutAt);

        //Optional<EvlInfoEntity> eEvlInfoEntity = evlInfoRepository.findById(Long.parseLong(String.valueOf(paramData.get("evlId"))));
        //paramData.put("wrterId", eEvlInfoEntity.get().getWrterId());

        //int result2 =  tchEvalMapper.modifyTchEvalEndEvalResultInfo(paramData);
        //log.info("result2:{}", result2);

        // 2024-04-24 (확인: 키인스 - 김명수 책임님)
        // 교사-[종료하기] 클릭시 학생-[제출하기]를 현재 처리하고 있다고 함.
        // 미제출자 처리로직 주석처리 (배치에서 미제출자 처리하기 때문에 약간의 시간차가 발생할 수 있음)
        /*
        if ("N".equals(paramData.get("timeoutAt"))) {
            int result3 = tchEvalMapper.modifyEvalSubmAtERD(paramData);
            log.info("result3:{}", result3);
            int result4 = tchEvalMapper.modifyEvalSubmAtERI(paramData);
            log.info("result4:{}", result4);
        }*/

        // 평가 마스터 정보 상태 수정
        int result1 =  tchEvalMapper.modifyTchEvalEndEvalInfo(paramData);
        log.info("result1:{}", result1);

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalEndEvalInfo(paramData));

        // 리포트 자동 공유 여부가 Y 인 경우. 리포트 공유 호출 -- x
        // 2025-09-12 리포트 공유 여부 N 일 경우에 리포트 공유 호출해야함. 시점 변경 및 추가요청 : 학생제출 & 리포트 공개 off 이후 교사 종료 시 : 리포트 생성알림 + 오답노트 추가 알림
        // 재수정요청 - 원복요청 리포트 공유 여부 N 일때 리포트 공유 호출 x
        String strRptAutoOthbcAt = MapUtils.getString(evalInfoMap, "rptAutoOthbcAt", "N");
        if ("Y".equals(strRptAutoOthbcAt)) {
            tchReportEvalService.modifyReportEvalOpen(paramData);
            assessmentSubmittedService.insertAssessmentInfo(paramData);
        }

        evalInfoMap.put("evlPrgDt", AidtCommonUtil.stringToDateFormat((String) evalInfoMap.get("evlPrgDt"),"yyyy-MM-dd HH:mm:ss"));
        evalInfoMap.put("evlCpDt", AidtCommonUtil.stringToDateFormat((String) evalInfoMap.get("evlCpDt"),"yyyy-MM-dd HH:mm:ss"));

        evalInfoMap.remove("rptAutoOthbcAt");
        return evalInfoMap;
    }


    public Object removeEvalReset(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlSttsCd", "evlSttsNm");

        // 진행 중인 (수업중) 평가 정보 조회 (체크용)
        Map<String, Object> result = tchEvalMapper.findTchEvalStartPrgEvalInfo(paramData);
        log.info("result:{}", result);

        // 기존 문항지 히스토리로 이관
        int result0 = tchEvalMapper.createEvaluationHistoryRecord(paramData);
        log.info("result0:{}", result0);

        if (ObjectUtils.isEmpty(result)) {
            Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);
            
            // 수업외 평가
            if ("Y".equals(evlInfoMap.get("pdSetAt").toString())) {
                int result1 = tchEvalMapper.modifyTchEvalResetEvalInfoY(paramData);
                log.info("result1:{}", result1);
            } else {
                // 수업중 평가
                int result1 = tchEvalMapper.modifyTchEvalResetEvalInfoN(paramData);
                log.info("result1:{}", result1);
            }

            int result2 = tchEvalMapper.modifyTchEvalResetEvalResultInfo(paramData);
            log.info("result2:{}", result2);

            int result3 = tchEvalMapper.modifyTchEvalResetEvalResultDetail(paramData);
            log.info("result3:{}", result3);

            return AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalStartEvalInfo(paramData));
        } else {
            return AidtCommonUtil.filterToMap(evalInfoItem, result);
        }
    }

    // 다시하기 집계배치
    private void executeBatchResetLogic(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = tchEvalMapper.findTchEvalStartPrgEvalInfo(paramData);
        log.info("result:{}", result);

        try{
            if (ObjectUtils.isEmpty(result)) {
                Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);
                // 수업외 평가
                if (!"Y".equals(evlInfoMap.get("pdSetAt").toString())) {
                    // 다시풀기 cdc 이전 데이터 삭제
                    paramData.put("trgtSeCd", "3");
                    kafkaBatchService.processContentReset(paramData);
                }
            }
        } catch (Exception e) {
            log.error("집계 배치 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }


    // 수업중 평가 다시하기 + 집계 배치(비동기기)
    public Object removeEvalResetWithBatch(Map<String, Object> paramData) throws Exception {

        // 1. 평가 다시하기
        Object resetResult = removeEvalReset(paramData);
        log.info("수업중 평가 다시하기 완료. evlId: {}", paramData.get("evlId"));

        // 2. 집계 배치  ( 비동기 처리)
        CompletableFuture.runAsync(() -> {
            try {
                log.info("수업중 평가 다시하기 완료 후 배치 작업 시작. evlId: {}", paramData.get("evlId"));
                executeBatchResetLogic(paramData);
                log.info("배치 작업 완료. evlId: {}", paramData.get("evlId"));
            } catch (Exception e) {
                log.error("배치 처리 중 오류 발생. evlId: {}, error: {}", paramData.get("evlId"), e.getMessage(), e);
            }
        });

        return resetResult;
    }

    public Object removeTchEvalDelete(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);

        resultMap.put("evlId", paramData.get("evlId"));

        //평가 예정/진행/종료 모두 삭제 가능
        if (evlInfoMap == null || evlInfoMap.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "삭제실패");
        } else {
            int result1 = tchEvalMapper.deleteTchEvalDeleteEvalResultDetail(paramData);
            int result2 = tchEvalMapper.deleteTchEvalDeleteEvalResultInfo(paramData);
            int result3 = tchEvalMapper.deleteTchEvalDeleteEvalIemInfo(paramData);
            int result4 = tchEvalMapper.deleteTchEvalDeleteEvalTrnTrget(paramData);
            int result5 = tchEvalMapper.deleteTchEvalDeleteEvalInfo(paramData);

            log.info("result1:{}", result1);
            log.info("result2:{}", result2);
            log.info("result3:{}", result3);
            log.info("result4:{}", result4);
            log.info("result5:{}", result5);

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "삭제완료");
        }

        return resultMap;
    }

    public Object removeEvalInit(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlSttsCd", "evlSttsNm");

        Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);

        if ("Y".equals(MapUtils.getString(evlInfoMap, "pdSetAt"))) {
            int result1 =  tchEvalMapper.modifyTchEvalInitEvalInfoY(paramData);
            log.info("result1:{}", result1);
        } else {
            int result1 =  tchEvalMapper.modifyTchEvalInitEvalInfoN(paramData);
            log.info("result1:{}", result1);
        }

        int result2 =  tchEvalMapper.modifyTchEvalInitEvalResultInfo(paramData);
        log.info("result2:{}", result2);

        int result3 =  tchEvalMapper.modifyTchEvalInitEvalResultDetail(paramData);
        log.info("result3:{}", result3);

        return AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalInit(paramData));
    }

    @Transactional(readOnly = true)
    public Object findTchEvalReadInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        LinkedHashMap<Object, Object> studentInfoMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("wrterId", "claId", "textbookId", "setsId", "eamMth", "eamMthNm", "evlNm", "evlSeCd", "bbsSvAt", "bbsNm", "tag", "cocnrAt", "pdSetAt", "pdEvlStDt", "pdEvlEdDt", "ntTrnAt", "timStAt", "timTime", "prscrStdSetAt", "prscrStdStDt", "prscrStdEdDt", "prscrStdNtTrnAt", "aiTutSetAt", "rwdSetAt", "scrSetAt", "evlStdrSetAt", "evlStdrSet", "evlGdStdrScr", "evlAvStdrScr", "evlPsStdrScr", "edGidAt", "edGidDc", "stdSetAt","rptAutoOthbcAt", "materailsSaveYn");
        List<String> studenInfoItem = Arrays.asList("id", "evlId", "trnTrgetId", "trnTrgetNm", "isTrnTrget");

        returnMap.putAll(AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalReadInfo(paramData)));

        studentInfoMap.put("evlId", paramData.get("evlId"));
        studentInfoMap.put("wrterId", returnMap.get("wrterId"));
        studentInfoMap.put("claId", returnMap.get("claId"));

        List<LinkedHashMap<Object, Object>> studentInfoList = CollectionUtils.emptyIfNull(tchEvalMapper.findTchEvalReadInfoStudentInfo(studentInfoMap)).stream().map(s -> {
            var tgtMap = new LinkedHashMap<>();
            if(Objects.isNull(s)) return tgtMap;

            var srcMap = new ObjectMapper().convertValue(s, Map.class);
            studenInfoItem.forEach(ss -> {
                if ("isTrnTrget".equals(ss)){
                    //if (((Integer) srcMap.get(ss) == 1)){
                    if (MapUtils.getInteger(srcMap, ss) == 1) {
                        tgtMap.put(ss, true);
                    } else {
                        tgtMap.put(ss, false);
                    }
                } else {
                    tgtMap.put(ss, srcMap.get(ss));
                }
            });
            return tgtMap;
        }).toList();

        returnMap.put("studentInfoList", studentInfoList);

        return returnMap;
    }

    public Map<String, Object> createTchEvalSave(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        Map<String, Object> setsInsertParamMap = new HashMap<>();
        int evlId = MapUtils.getIntValue(paramData, "evlId", 0);

        if (evlId == 0) {
            returnMap.put("evlId", evlId);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "평가 ID 필요");
            return returnMap;
        }

        // 데이터 완성도 검증 및 복구
        if (validateAndRepairEvalData(paramData)) {
            returnMap.put("evlId", evlId);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "평가 데이터 불완전으로 자료설정 변경 불가");
            returnMap.put("errCode", "setIdErr");

            return returnMap;
        }

        Map<String, Object> countParam = new HashMap<>();
        countParam.put("evlId", evlId);
        Map<String, Object> evlInfoOriginMap = tchEvalMapper.findEvlInfo(countParam);
        int evlResultInfoCount = tchEvalMapper.findEvlResultInfoCount(countParam);
        // 기간 변경 여부 체크
        String originalStartDate = String.valueOf(evlInfoOriginMap.get("evlPrgDt"));
        String originalEndDate = String.valueOf(evlInfoOriginMap.get("evlCpDt"));
        String newStartDate = String.valueOf(paramData.get("pdEvlStDt"));
        String newEndDate = String.valueOf(paramData.get("pdEvlEdDt"));

        // T를 공백으로 치환하여 정규화
        String normalizedOriginalStart = originalStartDate.replace("T", " ");
        String normalizedOriginalEnd = originalEndDate.replace("T", " ");
        String normalizedNewStart = newStartDate.replace("T", " ");
        String normalizedNewEnd = newEndDate.replace("T", " ");

        if ((originalStartDate != null && !originalStartDate.isEmpty() && !"null".equals(originalStartDate) &&
                originalEndDate != null && !originalEndDate.isEmpty() && !"null".equals(originalEndDate)) &&
                "N".equals(evlInfoOriginMap.get("tmprStrgAt")) &&
                (!normalizedOriginalStart.equals(normalizedNewStart) || !normalizedOriginalEnd.equals(normalizedNewEnd))) {
            paramData.put("isPeriodChanged", "Y");
        }else{
            paramData.put("isPeriodChanged", "N");
        }

        if("N".equals(evlInfoOriginMap.get("tmprStrgAt")) && "N".equals(paramData.get("isPeriodChanged")) && evlResultInfoCount > 0){
            tchEvalMapper.updateEvalInfo(paramData);

            returnMap.put("evlId", evlId);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }else if("Y".equals(paramData.get("isPeriodChanged")) && "1".equals(evlInfoOriginMap.get("evlSttusCd")) && evlResultInfoCount > 0){
            // 기간변경이 있고 평가 상태가 1인 경우 - updateEvalInfo 실행
            tchEvalMapper.updateEvalInfo(paramData);

            returnMap.put("evlId", evlId);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }else{
            // 기간변경이 있고 평가 상태가 1이 아닌 경우 - 기존 로직 실행
            //update evl info
            int result1 =  tchEvalMapper.modifyTchEvalSave(paramData);
            log.info("result1:{}", result1);

            //Optional<EvlInfoEntity> evlInfoEntity = evlInfoRepository.findById(Long.parseLong(String.valueOf(evlId)));
            Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);

            // setSummary 누락 체크 추가
            String setsId = String.valueOf(evlInfoMap.get("setsId"));
            if (StringUtils.isNotBlank(setsId) && !"null".equals(setsId)) {
                Map<String, Object> setSummaryCheckMap = new HashMap<>();
                setSummaryCheckMap.put("setsId", setsId);
                
                int isSetSummaryExist = tchEvalMapper.findSetSummaryForEval(setSummaryCheckMap);
                if (isSetSummaryExist == 0) {
                    returnMap.put("evlId", evlId);
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "setSummary 데이터가 없어 자료설정 변경 불가");
                    returnMap.put("errCode", "setIdErr");

                    return returnMap;
                }
            }

            tchEvalMapper.increaseModuleUseCnt(evlInfoMap);

            //move from /tch/eval/create
            // 해당 기존 평가 헝목 삭제
            tchEvalMapper.deleteTchEvalItems(evlInfoMap);
            int resultIem = tchEvalMapper.createTchEvalIemCreate(evlInfoMap);
            log.info("resultIem:{}", resultIem);

            if ("Y".equals(paramData.get("bbsSvAt"))) {
                //isnert sets tables
                int result0 = tchEvalMapper.createTchEvalSaveSets(paramData);
                log.info("result0:{}", result0);

                setsInsertParamMap.put("newSetsid", MapUtils.getString(paramData, "newSetsid"));
                setsInsertParamMap.put("oldSetsId", evlInfoMap.get("setsId"));

                int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("result2:{}", result2);

                int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("result3:{}", result3);

                int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("result4:{}", result4);

                int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("result6:{}", result6);

                setsInsertParamMap.put("evlId", evlId);
                int result5 =  tchEvalMapper.modifyTchEvalSaveBbsSetId(setsInsertParamMap);
                log.info("result5:{}", result5);
            }

            // 삭제 전 기존 task_result_info 데이터 조회
            List<Map<String, Object>> existingTaskResultList = tchEvalMapper.findExistingEvalResultInfo(paramData);
            Map<String, String> stntPeriodChangedMap = new HashMap<>();

            // 학생별 isPeriodChanged 결정 (eak_stts_cd 기준)
            for (Map<String, Object> existingData : existingTaskResultList) {
                String stntId = String.valueOf(existingData.get("stntId"));
                String eakSttsCd = String.valueOf(existingData.get("eakSttsCd"));

                // eak_stts_cd에 따른 isPeriodChanged 결정 로직
                // eak_stts_cd가 2이면 period_changed_at = 'Y', 그 외는 'N'
                if (Integer.parseInt(eakSttsCd) == 2) {
                    stntPeriodChangedMap.put(stntId, "Y");
                } else {
                    stntPeriodChangedMap.put(stntId, "N");
                }
            }
            paramData.put("stntPeriodChangedMap", stntPeriodChangedMap);

            int result6 = tchEvalMapper.removeTchEvalSaveETT(paramData);
            log.info("result6:{}", result6);

            int result7 = tchEvalMapper.removeTchEvalSaveERD(paramData);
            log.info("result7:{}", result7);

            int result8 = tchEvalMapper.removeTchEvalSaveERI(paramData);
            log.info("result8:{}", result8);
            //paramData.put("isUpdate", result8 > 0 ? "Y" : "N");



            if ("Y".equals(paramData.get("stdSetAt"))) {
                //isnert student tables
                List<Map<String, Object>> studentList = (List<Map<String, Object>>) paramData.get("studentInfoList");
                studentList.stream().forEach(studentInfo -> {
                    if ((boolean) studentInfo.get("isTrnTrget")) {
                        //studentInfo.put("wrterId", evlInfoEntity.get().getWrterId());
                        studentInfo.put("wrterId", evlInfoMap.get("wrterId"));
                        int result9 = 0;
                        try {
                            result9 = tchEvalMapper.createTchEvalSaveETT(studentInfo);
                        } catch (Exception e) {
                            log.error(CustomLokiLog.errorLog(e));
                        }
                        log.info("result9:{}", result9);
                    }
                });
            }

            int result10 = tchEvalMapper.createTchEvalSaveERI(paramData);
            log.info("result10:{}", result10);

            int resultCountCreateERD = tchEvalMapper.createTchEvalSaveERD(paramData);
            log.info("resultCountCreateERD:{}", resultCountCreateERD);

            paramData.put("resultCountCreateERD", resultCountCreateERD);
            int result11 =  tchEvalMapper.modifyTchEvalSaveEEN(paramData);
            log.info("result11:{}", result11);

            paramData.remove("id");
            paramData.remove("resultCountCreateERD");
            returnMap.put("evlId", evlId);

            int slfEvlInfoId = 0;
            if (paramData.containsKey("slfEvlInfo")) {
                Map<String, Object> slfEvlInfo = (Map<String, Object>) paramData.get("slfEvlInfo");

                if (!ObjectUtils.isEmpty(slfEvlInfo) && !slfEvlInfo.isEmpty()) {
                    Object resultSlfEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(slfEvlInfo);
                    log.info("resultSlfEvlInfo:{}", resultSlfEvlInfo);
                }
                slfEvlInfoId = MapUtils.getInteger(slfEvlInfo, "id", 0);
            }

            int perEvlInfoId = 0;
            if (paramData.containsKey("perEvlInfo")) {
                Map<String, Object> perEvlInfo = (Map<String, Object>) paramData.get("perEvlInfo");

                if (!ObjectUtils.isEmpty(perEvlInfo) && !perEvlInfo.isEmpty()) {
                    Object resultPerEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(perEvlInfo);
                    log.info("resultPerEvlInfo:{}", resultPerEvlInfo);
                }
                perEvlInfoId = MapUtils.getInteger(perEvlInfo, "id", 0);
            }

            if ("Y".equals(paramData.get("bbsSvAt"))) {
                if (slfEvlInfoId != 0) {
                    setsInsertParamMap.put("slfPerEvlSetId", slfEvlInfoId);
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }

                if (perEvlInfoId != 0) {
                    setsInsertParamMap.put("slfPerEvlSetId", perEvlInfoId);
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }
            }

            int result12 =  tchEvalMapper.modifyEvalStatusToInProgress(paramData);
            log.info("result12:{}", result12);

            if (result1 > 0) {
                returnMap.put("evlId", evlId);
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");
            } else {
                returnMap.put("evlId", evlId);
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "실패");
            }

            return returnMap;
        }

    }

    public Object createTchEvalCreate(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        int result1 = tchEvalMapper.createTchEvalCreate(paramData);
        log.info("result1:{}", result1);

        //move to /tch/eval/save
        //int result2 = tchEvalMapper.createTchEvalIemCreate(paramData);
        //log.info("result2:{}", result2);

        returnMap.put("evlId", paramData.get("id"));
        paramData.remove("id");

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Map<String, Object> copyEvalInfo(Map<String, Object> paramData) throws Exception {

        // 1. 기존 evl_info 를 가져옴
        LinkedHashMap orgEvlInfo = tchEvalMapper.getEvlInfoById(paramData);

        if (orgEvlInfo == null) {
            throw new AidtException("ID 에 해당하는 평가정보가 없습니다.");
        }

        LinkedHashMap newEvlInfo = ObjectUtils.clone(orgEvlInfo);
        newEvlInfo.remove("id");

        String pd_set_at = (String) newEvlInfo.get("pd_set_at");
        log.info("pd_set_at={}", pd_set_at);

        if (StringUtils.equals(pd_set_at, "Y")) {
            newEvlInfo.put("evl_prg_dt", orgEvlInfo.get("pd_evl_st_dt"));
            newEvlInfo.put("evl_cp_dt", orgEvlInfo.get("pd_evl_ed_dt"));
            newEvlInfo.put("evl_stts_cd", 2);
        } else {
            newEvlInfo.put("evl_prg_dt", null);
            newEvlInfo.put("evl_cp_dt", null);
            newEvlInfo.put("evl_stts_cd", 1);
        }
        newEvlInfo.put("mrk_cp_dt", null);
        newEvlInfo.put("reg_dt", new Date());
        newEvlInfo.put("mdfy_dt", new Date());

        log.info("+++++++++++++++++++++++++++++++");
        for (Object key : newEvlInfo.keySet()) {
            Object value = newEvlInfo.get(key);
            log.info("{}:{}", key, value);
        }
        log.info("+++++++++++++++++++++++++++++++");

        // evl_info copy - - CSAP_250812_lhr
        int evlInfoCloneCnt = tchEvalMapper.cloneEvlInfo(newEvlInfo);
        log.info("1.evl_info 복사 성공: cnt:{}",evlInfoCloneCnt );

        var newEvlId = newEvlInfo.get("id");
        log.info("[evl_info]gen id:{}", newEvlId);

        // evl_iem_info copy
        Map tmpParamMap = new HashMap();

        tmpParamMap.put("newEvlId", newEvlId);
        tmpParamMap.put("oldEvlId", paramData.get("evlId"));

        int evlIemInfoCloneCnt = tchEvalMapper.copyEvlIemInfoByEvlId(tmpParamMap);
        log.info("2.evl_iem_info 복사 성공: cnt:{}",evlIemInfoCloneCnt );

        // evl_result_info copy
        List<LinkedHashMap> orgEvlResultInfoList = tchEvalMapper.findEvlResultInfoListByEvlId(paramData);

        log.info("orgEvlResultInfoList.size() = {}", orgEvlResultInfoList.size());

        int idx = 0;
        for (LinkedHashMap orgEvlResultInfo : orgEvlResultInfoList) {
            var orgEvlResultId = orgEvlResultInfo.get("id");
            orgEvlResultInfo.remove("id");

            LinkedHashMap newEvlResultInfo = ObjectUtils.clone(orgEvlResultInfo);
            newEvlResultInfo.put("evl_id", newEvlId); // 새로 copy한 evl_id
            newEvlResultInfo.put("eak_stts_cd", 1);
            newEvlResultInfo.put("eak_at", 'N');
            newEvlResultInfo.put("subm_at", 'N');
            newEvlResultInfo.put("mrk_cp_at", 'N');
            newEvlResultInfo.put("evl_adi_sec", null);
            newEvlResultInfo.put("eak_st_dt", null);
            newEvlResultInfo.put("eak_ed_dt", null);
            newEvlResultInfo.put("evl_result_scr", null);
            newEvlResultInfo.put("reg_dt", new Date());
            newEvlResultInfo.put("mdfy_dt", new Date());

            // evl_result_info copy - CSAP_250812_lhr
            int evlResultInfoCloneCnt = tchEvalMapper.cloneEvlResultInfo(newEvlResultInfo);
            log.info("3-{}.evl_result_info 복사 성공: cnt={}", ++idx,evlResultInfoCloneCnt);
            var newEvlResultId = newEvlResultInfo.get("id");
            log.info("[evl_result_info]gen id:{}", newEvlResultId);

            // copy evl_result_detail
            tmpParamMap.clear();
            tmpParamMap.put("oldEvlResultId", orgEvlResultId);
            tmpParamMap.put("newEvlResultId", newEvlResultId);

            int evlResultDetailCloneCnt = tchEvalMapper.copyEvlResultDetailByEvlId(tmpParamMap);
            log.info("4-{}.evl_result_detail 복사 성공: cnt={}", idx, evlResultDetailCloneCnt);
        }
//        tchEvalMapper.cloneAnyTableByMap(orgEvlInfo); // 강제오류발생

        Map resultMap = new HashMap();
        resultMap.put("evlId", paramData.get("evlId"));
        resultMap.put("newEvlId", newEvlId);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findEvalAutoQstnExtr(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> articleInfoItem = Arrays.asList("id", "subId", "name", "thumbnail", "questionTypeNm", "difyNm", "evlIemScr");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int eamExmNum = MapUtils.getIntValue(paramData,"eamExmNum"); // 출제 문항수
            int eamGdExmMun = MapUtils.getIntValue(paramData,"eamGdExmMun"); // 상
            int eamAvUpExmMun = MapUtils.getIntValue(paramData,"eamAvUpExmMun"); // 중상
            int eamAvExmMun = MapUtils.getIntValue(paramData,"eamAvExmMun"); // 중
            int eamAvLwExmMun = MapUtils.getIntValue(paramData,"eamAvLwExmMun"); // 중하
            int eamBdExmMun = MapUtils.getIntValue(paramData,"eamBdExmMun"); // 하
            int difyExmNum = eamGdExmMun + eamAvUpExmMun + eamAvExmMun + eamAvLwExmMun + eamBdExmMun; // 난이도 문항수
            if(eamExmNum != difyExmNum) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", String.format("출제 문항수와 난이도 문항수가 다릅니다: %s != %s", eamExmNum, difyExmNum));
                return resultMap;
            }
            Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

            // eamScp 값들 중에서 studyMap_1에 해당하는 값들만 필터링
            List eamScpList = (List) procParamData.get("eamScp");
            if (eamScpList != null && !eamScpList.isEmpty()) {
                List<Long> studyMap1MetaIds = tchEvalMapper.findStudyMap1MetaIds(eamScpList);
                procParamData.put("eamScp", studyMap1MetaIds);
                eamScpList = studyMap1MetaIds;
            }

            List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
            Set<String> selectedArticleIds = new HashSet<>();
            Set<String> selectedStudyMap_1 = new HashSet<>();

            //List eamScpList = (List) procParamData.get("eamScp");
            int eamScpSize = eamScpList.size();

            Object[][] difyArr = {
                    {"MD05", "하", eamBdExmMun},
                    {"MD04", "중하", eamAvLwExmMun},
                    {"MD03", "중", eamAvExmMun},
                    {"MD02", "중상", eamAvUpExmMun},
                    {"MD01", "상", eamGdExmMun}
            };

            StringBuffer sb = new StringBuffer();
            Boolean articleCntCheck = true;
            for (Object[] difyObj : difyArr) {
                int difyLimit = (int) difyObj[2];
                if(difyLimit <= 0) continue;

                procParamData.put("difyCode", difyObj[0]);
                procParamData.put("difyLimit", difyLimit);
                procParamData.put("excludeIds", selectedArticleIds); // 이미 선택된 ID 전달
                procParamData.put("excludeStudyMaps", selectedStudyMap_1); // 이미 선택된 지식요인 전달

                List<Map> evalAutoQstnExtr = tchEvalMapper.findEvalAutoQstnExtr(procParamData);

                if(evalAutoQstnExtr.size() != difyLimit) {
                    articleCntCheck = false;
                    sb.append(difyObj[1]+":").append(difyLimit-evalAutoQstnExtr.size()).append(",");
                    //throw new AidtException(String.format("난이도(%s) 문항 개수가 부족합니다.: %s < %s",difyObj[0],difyLimit,evalAutoQstnExtr.size()));
                }

                // 선택된 ID, 지식요인 추가
                if (CollectionUtils.isNotEmpty(evalAutoQstnExtr)) {
                    for (Map item : evalAutoQstnExtr) {
                        selectedArticleIds.add(MapUtils.getString(item, "id"));
                        selectedStudyMap_1.add(MapUtils.getString(item, "studymap1"));

                        if (eamScpSize <= selectedStudyMap_1.size()) {
                            selectedStudyMap_1.clear();
                        }
                    }
                }

                articleList.addAll(AidtCommonUtil.filterToList(articleInfoItem, evalAutoQstnExtr));
            }

            if (!articleCntCheck) {
                String cntString = sb.toString();
                if (cntString.endsWith(",")) {
                    cntString = cntString.substring(0, cntString.length() - 1);
                }
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
                return resultMap;
            }

            /// 평가인 경우 배점 설정
            // 배점은 총점 100점 기준 출제문항수로 균등배분하며, 마지막 문항에 잔여점수 합산한다.
            int articleCnt = articleList.size();
            if(articleCnt > 0) {
                int scr = (int) (100 / articleCnt);
                articleList.forEach(s -> {
                    s.put("evlIemScr", scr);
                });
                int lastScr = scr + (100 - (scr * articleCnt));
                articleList.get(articleCnt - 1).put("evlIemScr", lastScr);
            }

            resultMap.put("articleList", articleList);
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "조건에 맞는 모듈이 존재하지 않습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }

    public Object modifyEvalSaveByMagicWand(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        Map<String, Object> setsInsertParamMap = new HashMap<>();
        int resultEvl = 0;

        //평가가 설정미완료인지 설정완료이고 예정상태인지 확인한다.
        //   설정미완료: tmpr_strg_at = 'Y' 인 경우
        //   설정완료이고 예정상태: tmpr_strg_at = 'N' 이고 evl_stts_cd = 1 인 경우

        Map<String, Object> evlInfoMap = tchEvalMapper.findEvlInfo(paramData);
        if(MapUtils.isEmpty(evlInfoMap)) {
            returnMap.put("evlId", paramData.get("evlId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패: 평가 정보가 존재하지 않습니다.");
            return returnMap;
        }

//        if (MapUtils.getIntValue(evlInfoMap, "evlSttsCd", 0) == 2) {
//            returnMap.put("evlId", paramData.get("evlId"));
//            returnMap.put("resultOk", false);
//            returnMap.put("resultMsg", "진행중인 평가는 편집할 수 없습니다.");
//            return returnMap;
//        }

        //1. 설정미완료: tmpr_strg_at = 'Y' 인 경우
        if("Y".equals(MapUtils.getString(evlInfoMap,"tmprStrgAt"))) {
            //evl_info 의 sets_id 업데이트한다.
            resultEvl = tchEvalMapper.modifyTchEvalSaveSetsId(paramData);
        }
        //2. 설정완료이고 예정상태: tmpr_strg_at = 'N' 이고 예정, 진행인 경우
        else if("N".equals(MapUtils.getString(evlInfoMap,"tmprStrgAt")) && ("1".equals(MapUtils.getString(evlInfoMap,"evlSttsCd")) || "2".equals(MapUtils.getString(evlInfoMap,"evlSttsCd")))) {
            Map<String,Object> setsHistInfo = new HashMap<>();
            setsHistInfo.put("parentSetsId",evlInfoMap.get("setsId"));
            setsHistInfo.put("claId",evlInfoMap.get("claId"));
            setsHistInfo.put("trgtId",evlInfoMap.get("id"));
            setsHistInfo.put("trgtSeCd","3");
            setsHistInfo.put("sets",paramData.get("setsId"));
            setsHistInfo.put("userId",evlInfoMap.get("wrterId"));

            setsHistInfo.put("setsUpdatedAt", "2".equals(String.valueOf(evlInfoMap.get("evlSttsCd"))) ? "Y" : "N");

            int resultset = tchEvalMapper.insertSetHist(setsHistInfo);
            //evl_info 의 sets_id 업데이트한다.
            resultEvl = tchEvalMapper.modifyTchEvalSaveSetsId(paramData);

            //evl_iem_info, evl_result_info, evl_result_detail 삭제
            int result1 = tchEvalMapper.deleteTchEvalDeleteEvalResultDetail(paramData);
            int result2 = tchEvalMapper.deleteTchEvalDeleteEvalResultInfo(paramData);
            int result3 = tchEvalMapper.deleteTchEvalDeleteEvalIemInfo(paramData);

            //모듈(아티클) 사용 횟수 정보(module_use_cnt) 테이블에 등록/수정
            evlInfoMap.put("setsId", MapUtils.getString(paramData,"setsId"));
            tchEvalMapper.increaseModuleUseCnt(evlInfoMap);

            //evl_iem_info 생성
            int resultIem = tchEvalMapper.createTchEvalIemCreate(evlInfoMap);
            log.info("resultIem:{}", resultIem);

            //evl_result_info 생성
            int resultERI = tchEvalMapper.createTchEvalSaveERI(paramData);
            log.info("resultERI:{}", resultERI);

            //evl_result_detail 생성
            int resultCountCreateERD = tchEvalMapper.createTchEvalSaveERD(paramData);
            log.info("resultCountCreateERD:{}", resultCountCreateERD);


            //bbsSvAt = 'Y' 일때 처리하는 로직 수행
            if ( "Y".equals(MapUtils.getString(evlInfoMap,"bbsSvAt")) ) {
                //isnert sets tables

                //세트지 삭제
                int resultRmv2 = tchEvalMapper.removeTchEvalSaveSAM(evlInfoMap);
                log.info("resultRmv2:{}", resultRmv2);
                int resultRmv3 = tchEvalMapper.removeTchEvalSaveSKM(evlInfoMap);
                log.info("resultRmv3:{}", resultRmv3);
                int resultRmv4 = tchEvalMapper.removeTchEvalSaveSMM(evlInfoMap);
                log.info("resultRmv4:{}", resultRmv4);
                int resultRmv5 = tchEvalMapper.removeTchEvalSaveSummary(evlInfoMap);
                log.info("resultRmv5:{}", resultRmv5);
                int resultRmv1 = tchEvalMapper.removeTchEvalSaveSets(evlInfoMap);
                log.info("resultRmv1:{}", resultRmv1);


                //세트지 생성
                //기존 bbsSetsId 값으로 생성해야한다. (2024.05.28)
                setsInsertParamMap.put("createdByBbsSetsId", "Y");
                setsInsertParamMap.put("evlId", paramData.get("evlId"));
                setsInsertParamMap.put("bbsSetsId", evlInfoMap.get("bbsSetsId"));
                setsInsertParamMap.put("oldSetsId", evlInfoMap.get("setsId"));

                int resultBbs1 = tchEvalMapper.createTchEvalSaveSets(setsInsertParamMap);
                log.info("resultBbs1:{}", resultBbs1);

                int resultBbs2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("resultBbs2:{}", resultBbs2);

                int resultBbs3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("resultBbs3:{}", resultBbs3);

                int resultBbs4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("resultBbs4:{}", resultBbs4);

                int resultBbs5 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("resultBbs5:{}", resultBbs5);

                //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 업데이트가 불필요함. (2024.05.28)
                //setsInsertParamMap.put("evlId", paramData.get("evlId"));
                //int resultBbs6 =  tchEvalMapper.modifyTchEvalSaveBbsSetId(setsInsertParamMap);
                //log.info("resultBbs6:{}", resultBbs6);
            }

            //evl_info 의 문항수 업데이트
            int result4 =  tchEvalMapper.modifyTchEvalSaveEEN(paramData);
            log.info("result4:{}", result4);


            tchEvalMapper.updateEvalAt(setsHistInfo);

            //해당 평가ID에 대한 자기/동료평가(이)가 존재하는지 확인한다.
            //자기동료평가설정정보(slf_per_evl_set_info) sets_id 업데이트 ( 불필요함 ) - 작성하지 않는다.
            //평가의 자료실저장여부(bbsSvAt)가 = 'Y' 이면 자기동료평가세트지매핑(slf_per_sets_mapng)에 등록한다.

            //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 자기/동료평가 등록이 불필요함. (2024.05.28)
            /*
            Map<String, Object> slfPerParamMap = new HashMap<>();
            //자기평가 설정정보 조회
            slfPerParamMap.put("gbCd", 3);   //1:교과자료, 2:과제, 3:평가
            slfPerParamMap.put("slfPerEvlSetInfo", 1);
            slfPerParamMap.put("evlId", paramData.get("evlId"));

            var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(slfPerParamMap);

            //동료평가 설정정보 조회
            slfPerParamMap.put("slfPerEvlSetInfo", 2);
            var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(slfPerParamMap);

            //평가의 자료실저장여부(bbsSvAt)가 = 'Y' 이면
            if ( "Y".equals(MapUtils.getString(evlInfoMap,"bbsSvAt")) ) {

                //자기평가
                if (!ObjectUtils.isEmpty(selInfoIdMap) && !selInfoIdMap.isEmpty()) {
                    setsInsertParamMap.put("slfPerEvlSetId", selInfoIdMap.get("id"));
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }

                //동료평가
                if (!ObjectUtils.isEmpty(perInfoIdMap) && !perInfoIdMap.isEmpty()) {
                    setsInsertParamMap.put("slfPerEvlSetId", perInfoIdMap.get("id"));
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }
            }
            */
        }

        if (resultEvl > 0) {
            returnMap.put("evlId", paramData.get("evlId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("evlId", paramData.get("evlId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * 출제된 평가, 과제를 세트지 정보로 조회하는 method
     * @param paramData
     *  matrialType : 과제 1 / 평가 2
     *  textbkId : 교과서 id
     *  userId : 선생님 id
     *  claId : 학급 id
     *  setsId : 과제 또는 평가 세트지 id
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findTchEvalTaskInfo(Map<String, Object> paramData) throws Exception {
        int matrialType = MapUtils.getInteger(paramData, "matrialType", 0);

        Map<String,Object> result = new HashMap<>();


        if (matrialType == 1) {   /* 과제 */
            result = tchEvalMapper.findTchTaskInfoBySet(paramData);
        } else if (matrialType == 2) {     /* 평가 */
            result = tchEvalMapper.findTchEvalInfoBySet(paramData);
        } else {
            result = tchEvalMapper.findTchEvalTaskInfo(paramData);
        }

        if (result == null) {
            result = new HashMap<>();
        }

        Map<String,Object> setsFindParam = new HashMap<>();
        setsFindParam.put("trgtSeCd", Integer.valueOf(1).equals(matrialType) ? 2 : 3);
        setsFindParam.put("claId", paramData.get("claId"));
        setsFindParam.put("originSetsId", paramData.get("setsId"));

        Map<String,Object> resultSetsHist = tchEvalMapper.findSetsHist(setsFindParam);
        if(resultSetsHist != null){
            result.put("id",resultSetsHist.get("trgtId"));
            result.put("gbCd",Integer.valueOf(1).equals(matrialType) ? 2 : 1);
        }

        return result;
    }

    public Object createTchEvalCreateForTextbk(List<Map<String, Object>> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        for (Map<String, Object> paramMap : paramData) {

            /* [S] 로그인을 연속으로 할 경우 중복 등록의 우려가 있어 로직 추가 */
            Map<String, Object> dupChkMap = tchEvalMapper.findTchEvalInfoBySet(paramMap);
            // 이미 있을 경우 continue
            if (MapUtils.isNotEmpty(dupChkMap)) {
                continue;
            }
            /* [E] 로그인을 연속으로 할 경우 중복 등록의 우려가 있어 로직 추가 */

            //입력 값
            Map<String, Object> createMap = ObjectUtils.clone(paramMap);

            //기본 설정 값
            createMap.put("eamMth", 3); //3(직접출제)
            createMap.put("eamTrget", 1); //1(공통문항출제)
            //createMap.put("eamExmNum", n); //셋트지의 setsummary 항목갯수 //쿼리에서 select count
            createMap.put("pdSetAt", "N"); // 기본) 수업중 평가

            // 2024-07-29 수업외 평가 인지 체크
            // - 추후 수업외 평가도 처리할 겨우 아래 주석을 풀어주면 됨.
            /*
            if(paramMap.containsKey("pdEvlStDt") && paramMap.containsKey("pdEvlEdDt")) {
                String pdEvlStDt = MapUtils.getString(paramMap, "pdEvlStDt");
                String pdEvlEdDt = MapUtils.getString(paramMap, "pdEvlEdDt");
                if(pdEvlStDt != null && pdEvlEdDt != null) {
                    createMap.put("pdSetAt", "Y");
                }
            }*/

            if (ObjectUtils.isNotEmpty(MapUtils.getString(createMap,"timTime"))) {
                createMap.put("timStAt", "Y"); // 타이머 시/분/초(timTime) 값이 존재하면 Y로 설정
            }
            createMap.put("rwdSetAt", "Y");
            createMap.put("evlStdrSet", 3); // 3 (점수)
            createMap.put("evlSttsCd", 1);

            int isSetSummaryExist = tchEvalMapper.findSetSummaryForEval(createMap);
            log.info("isSetSummaryExist:{}", isSetSummaryExist);

            if (isSetSummaryExist > 0) {
                int createEvlInfoCount = tchEvalMapper.createTchEvalCreateForTextbk_evlInfo(createMap);
                log.info("createEvlInfoCount:{}", createEvlInfoCount);

                int createEvlIemInfoCount = tchEvalMapper.createTchEvalCreateForTextbk_evlIemInfo(createMap);
                log.info("createEvlIemInfoCount:{}", createEvlIemInfoCount);

                int createEvlResultInfoCount = tchEvalMapper.createTchEvalCreateForTextbk_evlResultInfo(createMap);
                log.info("createEvlResultInfoCount:{}", createEvlResultInfoCount);

                int createEvlResultDetailCount = tchEvalMapper.createTchEvalCreateForTextbk_evlResultDetail(createMap);
                log.info("createEvlResultDetailCount:{}", createEvlResultDetailCount);
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "setSummary 데이터가 없어 평가 생성 실패");
            }
        }

        return returnMap;
    }

    public Object modifyTchEvalPeriodChange(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "저장실패");

        // 선택된 평가조회 (상태값, 수업 중, 수업 외 구분을 위한 기간설정여부)
        LinkedHashMap evlInfoMap = tchEvalMapper.getEvlInfoById(paramData);
        String pdSetAt = MapUtils.getString(evlInfoMap, "pd_set_at");
        int sttsCd = MapUtils.getIntValue(evlInfoMap, "evl_stts_cd", -1);
        int paramSttsCd = MapUtils.getIntValue(paramData, "evlSttsCd", -2);

        // 선택된 평가의 상태값이 요청 파라미터의 상태값과 동일하지 않은 경우 변경 불가
        if (sttsCd != paramSttsCd) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "진행중인 평가의 응시 기간은 수정할 수 없습니다.");
            return returnMap;
        }

        // 진행중(evlSttsCd:2) 인 경우 수업외 평가(기간설정여부 pdSetAt:Y) 만 변경 가능
        if (paramSttsCd == 2 && !"Y".equals(pdSetAt)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "진행 중인 경우 수업외 평가만 변경 가능 합니다.");
            return returnMap;
        }

        int modifyCnt = tchEvalMapper.modifyTchEvalPeriodChange(paramData);
        log.info("result1:{}", modifyCnt);

        // 진행중(evlSttsCd:2) 인 경우
        // 학생의 상태가 진행 중 일 경우 (where result_info.eak_stts_cd = 2)
        // result_info의 응시 종료일(eak_ed_dt) 값도 같이 변경
        if (paramSttsCd == 2) {
            int modifyResultInfoCnt = tchEvalMapper.modifyTchEvalResultInfo(paramData);
            log.info("result1:{}", modifyResultInfoCnt);
        }

        if (modifyCnt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        }

        return returnMap;
    }

    public Object findTchEvalStatusList(Map<String, Object> paramData) throws Exception {
        List<String> currentEvalListItem = Arrays.asList("id", "evlNm", "targetCnt", "submitCnt","evlSttsCd","evlSttsCd","evlSttsNm","evlPrgDt","evlCpDt"
                , "rptOthbcAt","rptOthbcDt","manualCnt","extraInfo","applScrAt","modifyHistAt");
        List<String> reqGradeEvalListItem = Arrays.asList("id", "evlNm", "eamTrget","evlSttsCd","evlSttsCd","evlSttsNm","evlPrgDt","evlCpDt"
                , "rptOthbcAt","rptOthbcDt","submitCnt","manualCnt","extraInfo","applScrAt","modifyHistAt");

        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        List<LinkedHashMap<Object, Object>> currentEvalList = AidtCommonUtil.filterToList(currentEvalListItem, tchEvalMapper.findTchEvalStatusList_currentEvalList(paramData));
        List<LinkedHashMap<Object, Object>> reqGradeEvalList = AidtCommonUtil.filterToList(reqGradeEvalListItem, tchEvalMapper.findTchEvalStatusList_reqGradeEvalListItem(paramData));

        returnMap.put("currentEvalList", currentEvalList);
        returnMap.put("reqGradeEvalList", reqGradeEvalList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findEvalSubmStatus(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("id", "evlNm","evlSttsCd","evlPrgDtHm","evlPrgDt","evlCpDt","targetCnt","submitCnt");
        List<String> stntListItem = Arrays.asList("id", "eriId", "mamoymId", "submAt", "submDt", "num", "flnm", "actvtnAt");

        resultMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findEvalSubmStatus_ei(paramData));
        List<LinkedHashMap<Object, Object>> stntListList = AidtCommonUtil.filterToList(stntListItem, tchEvalMapper.findEvalSubmStatus_eri(paramData));

        resultMap.put("stntListItem", stntListList);

        return resultMap;
    }

}
