package com.visang.aidt.lms.api.learning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.contents.dto.*;
import com.visang.aidt.lms.api.contents.service.ContentsService;
import com.visang.aidt.lms.api.learning.mapper.AiLearningEngMapper;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.learning.vo.*;
import com.visang.aidt.lms.api.mq.service.AssignmentGaveService;
import com.visang.aidt.lms.api.report.mapper.EvalReportMapper;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.AidtConst;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiLearningEngService {
    private final AssignmentGaveService assignmentGaveService;
    private final AiLearningMapper aiLearningMapper;
    private final AiLearningEngMapper aiLearningEngMapper;
    private final EvalReportMapper evalReportMapper;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private ContentsService contentsService;

    private List<AiArticleVO> selectAiCustomEachLearningArticles(Map<String, Object> paramData, List<Map<String, Object>> paramCountList, List<AiArticleVO> totalArticleList) throws Exception {

        return aiLearningEngMapper.selectAiCustomEachLearningArticles(paramData, paramCountList, totalArticleList);
    }

    private List<AiArticleVO> selectAiCustomEachLearningArticlesInfo(Map<String, Object> paramData) throws Exception {

        return aiLearningEngMapper.selectAiCustomLearningCommonArticlesInfo(paramData);
    }

    private List<AiArticleVO> selectAiCustomLearningArticles(Map<String, Object> paramData) throws Exception {

        return aiLearningEngMapper.selectAiCustomLearningCommonArticles(paramData);
    }

    private List<SetsArticleInfoVO> makeArticleVoList(List<AiArticleVO> aiArticleVOList) throws Exception {
        if (aiArticleVOList.size() == 0) return null;

        List<SetsArticleInfoVO> list = new ArrayList<>();

        for (AiArticleVO _vo: aiArticleVOList) {
            SetsArticleInfoVO articleInfoVO = new SetsArticleInfoVO();
            articleInfoVO.setId(0L);
            articleInfoVO.setArticle_id(_vo.getArticle_id());
            list.add(articleInfoVO);
        }

        log.info("makeArticleVoList : {}", list.get(0).getArticle_id());
        return list;
    }

    private List<SetsArticleMapVO> makeSetsArticleMapVoList(List<AiArticleVO> aiArticleVOList) throws Exception {
        if (aiArticleVOList.size() == 0) return null;

        List<SetsArticleMapVO> list = new ArrayList<>();

        for (AiArticleVO _vo: aiArticleVOList) {
            SetsArticleMapVO articleInfoVO = new SetsArticleMapVO();
            articleInfoVO.setId(0L);
            articleInfoVO.setArticle_id(_vo.getArticle_id());
            list.add(articleInfoVO);
        }
        return list;
    }

    private String createSetsForAiCustomLearning(List<AiArticleVO> totalArticleList, Map<String, Object> innerParam) throws Exception {

        SetsSaveVO setsVo = new SetsSaveVO();
        setsVo.setDescription(String.valueOf(innerParam.get("setNm")));
        setsVo.setIs_active(true);
        setsVo.setIs_publicOpen(true);
        setsVo.setPoints_type("auto");
        setsVo.setName(String.valueOf(innerParam.get("setNm")));
        setsVo.setBrand_id(MapUtils.getLong(innerParam, "brandId", 3L));
        setsVo.setCreator(String.valueOf(innerParam.get("wrterId")));
        List<SetsArticleInfoVO> setsArticleInfoVOList =  makeArticleVoList(totalArticleList);
        setsVo.setArticles(setsArticleInfoVOList);
        SetsSaveRequestVO setsSaveRequestVO = new SetsSaveRequestVO();
        setsSaveRequestVO.setSaveType("insert");
        setsSaveRequestVO.setSets(setsVo);
        setsSaveRequestVO.setArticles(makeSetsArticleMapVoList(totalArticleList));
        // 세트지 생성

        log.error("setsSaveRequestVO : {}", setsSaveRequestVO);

        return contentsService.saveSetInfo(setsSaveRequestVO);
    }

    private SetSummarySaveRequestVO createSetSummary(String newSetId, String creator, List<AiArticleVO> aiArticleVOList) throws Exception {

        SetSummarySaveRequestVO setSummarySaveRequestVO = new SetSummarySaveRequestVO();
        setSummarySaveRequestVO.setSet_id((String) newSetId);
        setSummarySaveRequestVO.setSaveType("insert");
        setSummarySaveRequestVO.setLoginUserId(creator);

        List<SetSummaryVO> setSummaryVOList = new ArrayList<>();
        for (AiArticleVO _vo :aiArticleVOList) {
            SetSummaryVO setSummaryVO = new SetSummaryVO();
            setSummaryVO.setSet_id(String.valueOf(newSetId));
            setSummaryVO.setArticle_id(_vo.getArticle_id());
            setSummaryVO.setSub_id(0L);
            setSummaryVO.setName(_vo.getName());
            setSummaryVO.setThumbnail(_vo.getThumbnail());
            setSummaryVO.setGradingMethod(59L);
            setSummaryVO.setCreator(creator);
            setSummaryVO.setUpdater(creator);
            setSummaryVOList.add(setSummaryVO);
        }

        setSummarySaveRequestVO.setSetSummary(setSummaryVOList);

        contentsService.saveSetSummary(setSummarySaveRequestVO);

        log.info("SetSummary Created.setId:{}", newSetId);

        return setSummarySaveRequestVO;
    }

    private int createTabInfoForAiCustomLearning(Map<String, Object> param) throws Exception {

        log.info("createTabInfoForAiCustomLearning START====");

        TabInfoVO tabInfoVO = TabInfoVO.builder()
                .wrter_id((String)param.get("wrterId"))
                .cla_id((String)param.get("claId"))
                .textbk_id(MapUtils.getInteger(param,"textbkId"))
                .crcul_id(MapUtils.getInteger(param,"crculId"))
                .tab_nm((String)param.get("stdDatNm"))
                .sets_id(((String)param.get("setsId")))
                .expos_at("Y")
                .tab_add_at("Y")
                .rgtr(String.valueOf(param.get("wrterId")))
                .mdfr(String.valueOf(param.get("wrterId")))
                .build();

        int cnt = aiLearningMapper.createTabInfoForAiCustomLearning(tabInfoVO);

        int tabId = Integer.parseInt(String.valueOf(tabInfoVO.getId()));

        log.info("created tabId:{}" , tabId);

        return tabId;
    }

    private int createStdDtaInfoForAiCustomLearning(Map<String, Object> param) throws Exception {
        StdDtaInfoVO stdDtaInfoVO = StdDtaInfoVO.builder()
                .wrter_id((String)param.get("wrterId"))
                .cla_id((String)param.get("claId"))
                .textbk_id(MapUtils.getInteger(param, "textbkId"))
                .crcul_id(MapUtils.getInteger(param, "crculId"))
                .std_dat_nm((String)param.get("stdDatNm"))
                .eam_mth(5) // ai 맞춤학습
                .eam_trget(MapUtils.getInteger(param, "eamTrget"))
                .eam_scp(MapUtils.getString(param, "eamScp"))
                .sets_id(MapUtils.getString(param, "setsId"))
                .textbk_tab_id(MapUtils.getInteger(param, "tabId"))
                .textbk_tab_nm((String)param.get("stdDatNm"))
                .rgtr(MapUtils.getString(param, "wrterId", ""))
                .mdfr(MapUtils.getString(param, "wrterId", ""))
                .build();

        if (MapUtils.getInteger(param, "eamTrget", 0)  == 1) { // 공통문항 일때만 세팅
            stdDtaInfoVO.setSets_id(MapUtils.getString(param, "setsId") );
            stdDtaInfoVO.setEam_exm_num(MapUtils.getInteger(param, "eamExmNum", 0) );
            stdDtaInfoVO.setEam_gd_exm_mun(MapUtils.getInteger(param, "eamGdExmMun", 0) );
            stdDtaInfoVO.setEam_av_exm_mun(MapUtils.getInteger(param, "eamAvExmMun", 0) );
            stdDtaInfoVO.setEam_bd_exm_mun(MapUtils.getInteger(param, "eamBdExmMun", 0) );
        } else {
            stdDtaInfoVO.setEam_exm_num(0);
        }

        int cnt = aiLearningMapper.createStdDtaInfoForAiCustomLearning(stdDtaInfoVO);

        return stdDtaInfoVO.getId();
    }

    private int createStdDtaResultInfoForAiCustomLearning(Map<String, Object> innerParam) throws Exception {
        int cnt = aiLearningEngMapper.createStdDtaResultInfoForAiCustomLearning(innerParam);
        log.info("created std_dta_result_info Cnt:{}", cnt);

        return cnt;
    }

    private int createStdDtaResultInfoForAiCustomLearningEach(Map<String, Object> innerParam) throws Exception {

        int cnt = aiLearningEngMapper.createStdDtaResultInfoForAiCustomLearningEach(innerParam);

        log.info("created std_dta_result_info Cnt:{}", cnt);

        return cnt;
    }

    private int createStdDtaResultDetailForAiCustomLearning(Map<String, Object> innerParam) throws Exception {

        int cnt = aiLearningEngMapper.createStdDtaResultDetailForAiCustomLearning(innerParam);

        log.info("created std_dta_result_detail Cnt:{}", cnt);

        return cnt;

    }

    private Long createTaskInfoForAiCustomLearning(Map<String, Object> param) throws Exception {
        String stDt = String.valueOf(param.getOrDefault("pdEvlStDt", ""));
        String edDt = String.valueOf(param.getOrDefault("pdEvlEdDt", ""));

        // 안전하게 공백 처리
        String modStDt = stDt;
        String modEdDt = edDt;

        // 공백이 있는 경우에만 substring 실행
        if (stDt.contains(" ")) {
            modStDt = stDt.substring(0, stDt.lastIndexOf(" "));
        }

        if (edDt.contains(" ")) {
            modEdDt = edDt.substring(0, edDt.lastIndexOf(" "));
        }

        int taskSttsCd = 1; // 1 예정, 2 진행중

        Date pdEvlStDt = null;
        Date pdEvlEdDt = null;

        try {

            // 날짜 형식에 따라 다른 SimpleDateFormat 사용
            SimpleDateFormat sdfWithTime = new SimpleDateFormat(AidtConst.FORMAT_STRING_YMDHM);
            SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");

            // 날짜 문자열에 시간 포함 여부에 따라 적절한 포맷터 사용
            if (stDt.contains(" ")) {
                pdEvlStDt = sdfWithTime.parse(stDt);
            } else if (!stDt.isEmpty()) {
                pdEvlStDt = sdfDateOnly.parse(stDt);
            }

            if (edDt.contains(" ")) {
                pdEvlEdDt = sdfWithTime.parse(edDt);
            } else if (!edDt.isEmpty()) {
                pdEvlEdDt = sdfDateOnly.parse(edDt);
            }

            if (pdEvlStDt != null && pdEvlStDt.before(new Date())) {
                taskSttsCd = 2; // 시작일시가 지났으면 진행중
            }

        } catch (ParseException e) {
            log.error(CustomLokiLog.errorLog(e));
            //TODO Controller 에서 이미 validate 했음.
        }

        TaskInfoVO taskInfoVO = TaskInfoVO.builder()
                .wrter_id(String.valueOf(param.get("wrterId")))
                .cla_id(String.valueOf(param.get("claId")))
                .textbk_id(Integer.parseInt(String.valueOf(param.get("textbkId"))))
                .task_nm(String.valueOf(param.get("taskNm")))
                .eam_mth(MapUtils.getInteger(param, "eamMth", 5))       // default : ai 맞춤학습(5)
                .eam_trget(Integer.parseInt(String.valueOf(param.get("eamTrget"))))
                .sets_id(MapUtils.getString(param, "setsId"))
                .task_stts_cd(taskSttsCd)
                .task_prg_dt(pdEvlStDt)
                .task_cp_dt(pdEvlEdDt)
                .pd_evl_st_dt(stDt)
                .pd_evl_ed_dt(edDt)
                .eam_exm_num(MapUtils.getInteger(param, "eamExmNum", 0))
                .eam_gd_exm_mun(MapUtils.getInteger(param, "eamGdExmMun", 0))
                .eam_av_up_exm_mun(MapUtils.getInteger(param, "eamAvUpExmMun", 0))
                .eam_av_exm_mun(MapUtils.getInteger(param, "eamAvExmMun", 0))
                .eam_av_lw_exm_mun(MapUtils.getInteger(param, "eamAvLwExmMun", 0))
                .eam_bd_exm_mun(MapUtils.getInteger(param, "eamBdExmMun", 0))
                .rgtr(String.valueOf(param.get("wrterId")))
                .mdfr(String.valueOf(param.get("wrterId")))
                .ai_tut_set_at(MapUtils.getString(param,"aiTutSetAt", "N"))
                .prscr_std_crt_trget_id(MapUtils.getInteger(param, "prscrStdCrtTrgetId", 0))
                .build();

        int cnt = aiLearningMapper.createTaskInfoForAiCustomLearning(taskInfoVO);

        Long taskId = taskInfoVO.getId();

        log.info("created taskId:{}", taskId);

        return taskId;
    }

    private int createTaskResultInfoForAiCustomLearning(Map<String, Object> param, List<AiArticleVO> articleList) throws Exception {

        int cnt = aiLearningEngMapper.createTaskResultInfoForAiCustomLearning(param);
        log.info("created task_result_info count:{}", cnt);

        return cnt;
    }

    private int createTaskResultInfoForAiCustomLearningEach(Map<String, Object> param, List<AiArticleVO> articleList) throws Exception {

        int cnt = aiLearningEngMapper.createTaskResultInfoForAiCustomLearningEach(param);
        log.info("created task_result_info count:{}", cnt);

        return cnt;
    }

    private int createTaskResultDetailForAiCustomLearning(Map<String, Object> param) throws Exception {

        int cnt = aiLearningEngMapper.createTaskResultDetailForAiCustomLearning(param);

        log.info("created task_result_detail count:{}", cnt);

        return cnt;
    }

    public Map<String, Object> createAiCustomLeariningPreview(Map<String, Object> paramData) throws Exception {
        // 날짜 체크
        validateDatePara(paramData);

        Map<String, Object> resultData;

        // tabId 필수, tab_info 생성 API 별도로 사용 (/tch/ai/custom-lrn/restart)
        //tab_id에 대해 ai맞춤 문항이 이미 존재하는지 validate
        String crtAt = aiLearningMapper.getTabInfoCrtAt(paramData);

        if (crtAt == null) throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
        if ("Y".equals(crtAt)) throw new AidtException("이미 맞춤학습 문항이 생성된 탭 입니다. ");

        resultData = switch (MapUtils.getString(paramData, "eamTrget")) {
            case "1" -> { // 공통문항 출제
                log.info("1.공통문항 출제 미리보기 START");
                yield createAiCustomLeariningCommonPreview(paramData);
            }
            case "2" -> { // 개별문항 출제
                log.info("2.개별문항 출제 미리보기 START");
                yield createAiCustomLeariningPersonalPreview(paramData);
            }
            default -> throw new AidtException("eamTrget 값이 정의되지 않았습니다.");
        };

        return resultData;
    }

    public Map<String, Object> createAiCustomLearining(Map<String, Object> paramData) throws Exception {
        // 날짜 체크
        validateDatePara(paramData);

        Map<String, Object> resultData;

        resultData = switch (MapUtils.getString(paramData, "eamTrget")) {
            case "1" -> { // 공통문항 출제
                log.info("1.공통문항 출제 START");
                yield createAiCustomLeariningCommon(paramData);
            }
            case "2" -> { // 개별문항 출제
                log.info("2.개별문항 출제 START");
                yield createAiCustomLeariningPersonal(paramData);
            }
            default -> throw new AidtException("eamTrget 값이 정의되지 않았습니다.");
        };

        return resultData;
    }

    @Transactional
    public Map<String, Object> createAiCustomLeariningCommon(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<AiArticleVO> totalArticleList = aiLearningEngMapper.selectAiCustomLearningCommonArticlesInfo(innerParam);

        int resultCnt = totalArticleList.size();

        // 세트지 생성
        String setsId = "";
        // 생성된 taskId
        Long taskId = null;
        // 생성된 stdDtaId
        int dtaId = 0;

        if (resultCnt > 0) {
            String name = "[AI 맞춤 학습] " + totalArticleList.get(0).getName();
            int metaId = totalArticleList.get(0).getMeta_id();

            innerParam.put("setNm", name);

            setsId = createSetsForAiCustomLearning(totalArticleList, innerParam);
            log.info("created setsId={}",setsId);

            if (ObjectUtils.isNotEmpty(setsId)) {

                //세트지가 만들어 졌으므로, SetSummary, evl_result_info, evl_result_detail 정보를 생성한다.
                log.info("SetSummary 생성");
                createSetSummary(setsId, String.valueOf(innerParam.get("wrterId")), totalArticleList);
                innerParam.put("setsId", setsId);
                innerParam.put("eamScp", metaId);
                innerParam.put("aiTutSetAt", MapUtils.getString(innerParam, "aiTutSetAt", "N"));


                List<String> stntIds = ((ArrayList<Map<String, String>>) innerParam.get("stntInfoList"))
                        .stream().map(it -> it.get("mamoymId")).toList();
                innerParam.put("stntIds", stntIds);


                // 과제, 수업 만들기
                switch (String.valueOf(innerParam.get("lrnMethod"))) {
                    case "1":
                        // 수업중 풀기

                        innerParam.put("stdDatNm", name);
                        //innerParam.put("metaId", metaId);

                        // tab_info 생성X - 받아온 tabId 이용함(20240409), (eng : 20240423)
                        // tab_info 생성
                        //int tabId = createTabInfoForAiCustomLearning(innerParam);
                        int tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);
                        if (tabUpdCnt == 0 ) {
                            throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
                        }

                        //innerParam.put("tabId", tabId);

                        // std_dta_info 생성
                        dtaId = createStdDtaInfoForAiCustomLearning(innerParam);

                        innerParam.put("dtaId", dtaId);

                        // std_dta_result_info 생성
                        int sdriCnt = createStdDtaResultInfoForAiCustomLearning(innerParam);
                        log.info("created std_dta_result_info cnt:{}", sdriCnt);

                        // std_dta_result_detail 생성
                        int sdrdCnt = createStdDtaResultDetailForAiCustomLearning(innerParam);
                        log.info("created std_dta_result_detail cnt:{}", sdrdCnt);

                        // 설정정보 생성
                        innerParam.put("ai_cstmzd_std_mthd_se_cd", 1); // 수업중풀기 1, 과제 2
                        innerParam.put("eamScp", getEamScp(innerParam)); // 출제범위 세팅
                        int configUpdCnt =  aiLearningMapper.createConfigAiCustomLearning(innerParam);
                        log.info("configUpdCnt:{}", configUpdCnt);

                        resultMap.put("dtaId", dtaId);
                        resultMap.put("sdriCnt", sdriCnt);
                        resultMap.put("sdrdCnt", sdrdCnt);
                        resultMap.put("configUpdCnt", configUpdCnt);
                        //resultMap.put("tabId", tabId);

                        break;
                    case "2":
                        // 과제로 내기

                        // tab 정보 수정 (ai_cstmzd_std_crt_at='Y')
                        tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);
                        if (tabUpdCnt == 0 ) {
                            throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
                        }

                        // 탭 이름 가져오기
                        String tabNm = aiLearningEngMapper.findTabNmForAiCustomLearning(innerParam);
                        name = "[" + tabNm + "] " + totalArticleList.get(0).getName();

                        innerParam.put("taskNm", name);
                        // task_info 생성
                        taskId = createTaskInfoForAiCustomLearning(innerParam);

                        innerParam.put("taskId", taskId);

                        // task_result_info 생성
                        int triCnt = createTaskResultInfoForAiCustomLearning(innerParam, totalArticleList);
                        log.info("created task_result_info cnt:{}", triCnt);

                        // task_result_detail 생성
                        int trdCnt = createTaskResultDetailForAiCustomLearning(innerParam);
                        log.info("created task_result_detail cnt:{}", trdCnt);

                        // 설정정보 생성
                        innerParam.put("ai_cstmzd_std_mthd_se_cd", 2); // 수업중풀기 1, 과제 2
                        innerParam.put("eamScp", getEamScp(innerParam)); // 출제범위 세팅
                        int configUpdCnt2 =  aiLearningMapper.createConfigAiCustomLearning(innerParam);
                        log.info("configUpdCnt:{}", configUpdCnt2);

                        resultMap.put("taskId", taskId);

                        // 과제 등록 시 MQ 발송
                        assignmentGaveService.insertBulkTaskMqTrnLog(resultMap);
                        break;
                    default:
                        log.warn("lrnMethod is not define!!!-{}", innerParam.get("lrnMethod"));
                }
            } else {
                log.error("Sets not created.");
            }
        }

//        resultMap.put("resultMsg", msg);
        resultMap.put("setsId", setsId);

//        aiLearningMapper.selectAiCustomLearningArticles(null); // 강제오류 발생 (rollback 테스트)


        return resultMap;
    }

    public Map<String, Object> createAiCustomLeariningCommonPreview(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 난이도별 출제문항수에 따라 생성
        int eamExmNum = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamExmNum", "0"))); // 총문항수
        int cntHigh = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamGdExmMun", "0"))); // 상
        int cntMid = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamAvExmMun", "0"))); // 중
        int cntLow = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamBdExmMun", "0")));  // 하

        // 출제문항수 검증
        if (eamExmNum != (cntHigh + cntMid + cntLow)) {
            throw new AidtException("각 난이도별 출제문항수 합이 총문항수와 다릅니다.");
        }

        // 난이도별 요청한 문제수
        LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
        requestCountMap.put("하", cntLow);
        requestCountMap.put("중", cntMid);
        requestCountMap.put("상", cntHigh);

        // 생성한 문제수
        LinkedHashMap<String, Integer> resopnseCountMap = new LinkedHashMap<>();

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<AiArticleVO> totalArticleList = new ArrayList<>();

        // 난이도별로 맞춤 아티클 추출
        for (Map.Entry<String, Integer> entry: requestCountMap.entrySet()) {
            String diffCode = entry.getKey();
            int cnt = entry.getValue();

            List<AiArticleVO> articleList = new ArrayList<>();
            innerParam.put("difficulty", diffCode);
            innerParam.put("cnt", cnt);

            if (cnt > 0 ) {
                articleList =  selectAiCustomLearningArticles(innerParam);

                if (CollectionUtils.isNotEmpty(articleList)) {
                    totalArticleList.addAll(articleList);
                }
            }

            resopnseCountMap.put(diffCode, articleList.size());
        }

        int resultCnt = totalArticleList.size();

        if (eamExmNum > resultCnt) {
            resultMap.put("resultOk", false);

            StringBuilder sb = new StringBuilder();
            int md01Cnt = MapUtils.getInteger(requestCountMap, "상", 0) - MapUtils.getInteger(resopnseCountMap, "상", 0);
            int md02Cnt = MapUtils.getInteger(requestCountMap, "중", 0) - MapUtils.getInteger(resopnseCountMap, "중", 0);
            int md03Cnt = MapUtils.getInteger(requestCountMap, "하", 0) - MapUtils.getInteger(resopnseCountMap, "하", 0);

            if (md01Cnt > 0) {
                sb.append("상:").append(md01Cnt).append(",");
            }
            if (md02Cnt > 0) {
                sb.append("중:").append(md02Cnt).append(",");
            }
            if (md03Cnt > 0) {
                sb.append("하:").append(md03Cnt).append(",");
            }

            String cntString = sb.toString();
            if (cntString.endsWith(",")) {
                cntString = cntString.substring(0, cntString.length() - 1);
            }

            log.error(cntString);

            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
        } else {
            // 미리보기 정보 세팅
            Map<String, Object> previewMap = getPreviewInfo(totalArticleList);

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
            resultMap.put("previewInfo", previewMap.get("previewInfo"));
            resultMap.put("articleList", previewMap.get("articleList"));
        }

        return resultMap;
    }

    public Map<String, Object> createAiCustomLeariningPersonal(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        // article 생성되지 않은 수
        int getNonArticle = 0;

        List<Map<String, Object>> stntInfoList = objectMapper.convertValue(innerParam.get("stntInfoList"), new TypeReference<>() {});


        // 생성된 taskId
        Long taskId = 0L;
        // 생성된 stdDtaId
        int dtaId = 0;

        if (CollectionUtils.isNotEmpty(stntInfoList)) {
            innerParam.put("tabId", MapUtils.getIntValue(innerParam, "tabId"));

            Map<String, Object> curriculumMap = aiLearningEngMapper.findCurriculumMap(innerParam);
            String name = "[AI 맞춤 학습] ";

            if (ObjectUtils.isNotEmpty(curriculumMap)) {
                name = name + MapUtils.getString(curriculumMap, "text");
            }

            int tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);
            if (tabUpdCnt == 0 ) {
                throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
            }

            innerParam.put("eamScp", getEamScp(innerParam));

            if ("1".equals(MapUtils.getString(innerParam,"lrnMethod", "0"))) {
                innerParam.put("stdDatNm", name);
                dtaId = createStdDtaInfoForAiCustomLearning(innerParam);
                resultMap.put("dtaId", dtaId);

                // 설정정보 생성
                innerParam.put("ai_cstmzd_std_mthd_se_cd", 1); // 수업중풀기 1, 과제 2

            } else {
                String tabNm = aiLearningMapper.findTabNmForAiCustomLearning(innerParam);

                name = "[" + tabNm + "] " + MapUtils.getString(curriculumMap, "text");

                innerParam.put("taskNm", name);
                taskId = createTaskInfoForAiCustomLearning(innerParam);
                resultMap.put("taskId", taskId);
                innerParam.put("taskId", taskId);

                // 설정정보 생성
                innerParam.put("ai_cstmzd_std_mthd_se_cd", 2); // 수업중풀기 1, 과제 2

                // 과제 등록 시 MQ 발송
                assignmentGaveService.insertBulkTaskMqTrnLog(resultMap);
            }
            innerParam.put("setNm", name);

            // 설정정보 생성
            int configId =  createConfigAiCustomLearning(innerParam);
            log.info("configId:{}", configId);
            // 설정 상세 정보 생성
            innerParam.put("configId", configId);
        }

        for (Map<String, Object> stntInfo : stntInfoList) {
            String stntId = MapUtils.getString(stntInfo, "mamoymId");
            String lev = MapUtils.getString(stntInfo, "lev");
            List<String> articles = objectMapper.convertValue(stntInfo.get("articleList"), new TypeReference<>() {});

            Map<String, Object> resultMapEach = new HashMap<>();

            innerParam.put("stntId", stntId);
            innerParam.put("lev", lev);
            innerParam.put("dtaId", dtaId);
            innerParam.put("taskId", taskId);


            resultMapEach.put("stntId", stntId);
            resultMapEach.putAll(this.createAiCustomEachLearining(innerParam, articles));

            resultList.add(resultMapEach);

            if (StringUtils.isEmpty(MapUtils.getString(resultMapEach, "setsId", ""))) {
                getNonArticle++;
            } else {
                int confingCnt = createConfigAiCustomResult(innerParam);
                log.info("confingCnt1:{}", confingCnt);
            }
        }

        if (resultList.size() == getNonArticle ) {
            throw new AidtException("출제할 문항이 없습니다.");
        }

        resultMap.put("stntList", resultList);

        return resultMap;
    }

    public Map<String, Object> createAiCustomLeariningPersonalPreview(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        /* 성취도 영역별 아이디(usd_ach_id) 추출 */
        List<String> cuScrList = aiLearningEngMapper.selectUsdAchId(innerParam);

        /* 연결된 meta 정보가 없을 경우 */
        if (cuScrList.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "AI 맞춤 학습 출제 준비중입니다. 다른 목차를 이용해 주세요.");

            return resultMap;
        }

        /* grammar 혹은 vocabulary가 있을 경우 chkGrammarOrVocabulary를 true */
        boolean chkGrammarOrVocabulary = Optional.of(cuScrList)
                .map(list -> list.stream()
                        .anyMatch(val -> "grammar".equals(val) || "vocabulary".equals(val)))
                .orElse(false);

        List<Map<String, Object>> stntList = aiLearningEngMapper.findUsdAchScrListAll2(innerParam, cuScrList);


        for (Map<String, Object> checkAchScr : stntList) {
            if (ObjectUtils.isEmpty(MapUtils.getInteger(checkAchScr, "usdAchScr"))) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "성취도가 없는 구성원이 존재 합니다.");

                return resultMap;
            }
        }

        // return article 정보 정리
        List<Map<String, Object>> stntArticleList = new ArrayList<>();
        List<Map<String, Object>> stntPreviewInfoList = new ArrayList<>();

        for (Map<String, Object> stntMap : stntList) {
            Map<String, Object> configResultMap;
            String strStdtId = MapUtils.getString(stntMap, "stdtId");

            List<Map<String, Object>> usdAchScrList;
            if (chkGrammarOrVocabulary) {
                usdAchScrList = aiLearningEngMapper.findUsdAchScrListGrammarOrVocabulary(innerParam, strStdtId, cuScrList);
            } else {
                usdAchScrList = aiLearningEngMapper.findUsdAchScrList(innerParam, strStdtId, cuScrList);
            }

            // 성취도별 요청한 문제수
            List<Map<String, Object>> requestCountList = new ArrayList<>();
            Map<String, Object> requestCountMap;

            if (usdAchScrList.size() == 1) {
                requestCountMap = new LinkedHashMap<>();
                requestCountMap.put("usdClsfCd", MapUtils.getString(usdAchScrList.get(0), "usdClsfCd"));
                requestCountMap.put("avgUsdAchScr", MapUtils.getLong(usdAchScrList.get(0), "avgUsdAchScr"));
                requestCountMap.put("avgUsdAchScrNm", MapUtils.getString(usdAchScrList.get(0), "avgUsdAchScrNm"));
                requestCountMap.put("iemSize", 20);
                requestCountList.add(requestCountMap);

                configResultMap = usdAchScrList.get(0);
            } else {
                int iemSize = (int) (20 / (usdAchScrList.size() + 1));
                float avgUsdAchScr = 0f;
                String stdAt = "N";
                String tempStdAt = "";
                for (Map<String, Object> usdAchScrMap : usdAchScrList) {
                    requestCountMap = new LinkedHashMap<>();
                    requestCountMap.put("usdClsfCd", MapUtils.getString(usdAchScrMap, "usdClsfCd"));
                    requestCountMap.put("avgUsdAchScr", MapUtils.getLong(usdAchScrMap, "avgUsdAchScr"));
                    requestCountMap.put("avgUsdAchScrNm", MapUtils.getString(usdAchScrMap, "avgUsdAchScrNm"));
                    requestCountMap.put("iemSize", iemSize);
                    requestCountList.add(requestCountMap);

                    avgUsdAchScr = avgUsdAchScr + MapUtils.getFloat(usdAchScrMap, "avgUsdAchScr");

                    tempStdAt = MapUtils.getString(usdAchScrMap, "stdAt", "N");

                    if ("N".equals(stdAt)) {
                        stdAt = tempStdAt;
                    }
                }
                requestCountMap = new LinkedHashMap<>();
                requestCountMap.put("usdClsfCd", "avg");
                requestCountMap.put("stdtId", MapUtils.getString(usdAchScrList.get(0), "stdtId"));
                avgUsdAchScr = avgUsdAchScr / (usdAchScrList.size());
                requestCountMap.put("avgUsdAchScr", avgUsdAchScr);
                if (avgUsdAchScr >= 80) {
                    requestCountMap.put("avgUsdAchScrNm", "상");
                } else if (avgUsdAchScr >= 50) {
                    requestCountMap.put("avgUsdAchScrNm", "중");
                } else {
                    requestCountMap.put("avgUsdAchScrNm", "하");
                }
                requestCountMap.put("iemSize", 20 - (iemSize * usdAchScrList.size()));
                requestCountList.add(requestCountMap);

                configResultMap = requestCountMap;

                requestCountList.sort(
                        Comparator.comparing((Map<String, Object> map) -> MapUtils.getFloat(map, "avgUsdAchScr"))
                );
            }

            Map<String, Object> resultMapEach = new HashMap<>();
            innerParam.put("stntId", strStdtId);

            resultMapEach.put("stntId", strStdtId);

            List<AiArticleVO> totalArticleList = new ArrayList<>();

            // 난이도별로 맞춤 아티클 추출
            for (Map<String, Object> paramCountMap : requestCountList) {
                List<AiArticleVO> articleList;

                innerParam.putAll(paramCountMap);
                innerParam.put("stntId", strStdtId);
                articleList = selectAiCustomEachLearningArticles(innerParam, requestCountList, totalArticleList);
                totalArticleList.addAll(articleList);
            }

            if (CollectionUtils.isNotEmpty(totalArticleList)) {
                // 미리보기 정보 세팅
                Map<String, Object> previewMap = getPreviewInfo(totalArticleList);

                stntPreviewInfoList.add(Map.of(strStdtId, previewMap.get("previewInfo")));
                stntArticleList.add(Map.of(strStdtId, previewMap.get("articleList")));
            } else {
                // 한명이라도 출제 실패 시 모두 실패
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "출제 문항이 없는 구성원이 존재 합니다.");

                return resultMap;
            }


        }

        resultMap.put("previewInfo", stntPreviewInfoList);
        resultMap.put("articleList", stntArticleList);

        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        return resultMap;
    }

    public Map<String, Object> createAiCustomEachLearining(Map<String, Object> paramData, List<String> articles) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<AiArticleVO> articleInfoList;

        innerParam.put("articleList", articles);

        articleInfoList =  selectAiCustomEachLearningArticlesInfo(innerParam);

        List<AiArticleVO> totalArticleList = new ArrayList<>(articleInfoList);

        int resultCnt = totalArticleList.size();

        // 세트지 생성
        String setsId = "";
        // 생성된 taskId
        Long taskId = null;
        // 생성된 stdDtaId
        int dtaId = 0;

        if (resultCnt > 0) {
            int metaId = totalArticleList.get(0).getMeta_id();
            innerParam.put("eamExmNum", resultCnt);

            setsId = createSetsForAiCustomLearning(totalArticleList, innerParam);
            log.info("created setsId={}",setsId);
            if (ObjectUtils.isNotEmpty(setsId)) {

                //세트지가 만들어 졌으므로, SetSummary, evl_result_info, evl_result_detail 정보를 생성한다.
                log.info("SetSummary 생성");
                createSetSummary(setsId, String.valueOf(innerParam.get("wrterId")), totalArticleList);
                innerParam.put("setsId", setsId);

                // 과제, 수업 만들기
                switch (String.valueOf(paramData.get("lrnMethod"))) {
                    case "1":
                        // 수업중 풀기
                        //innerParam.put("stdDatNm", name);
                        innerParam.put("metaId", metaId);

                        // tab_info 생성 //다른문제의 경우 매개변수로 받아서 사용
                        innerParam.put("tabId", MapUtils.getInteger(paramData, "tabId"));

                        // std_dta_info 생성  //다른문제의 경우 학생 별로 반복 하기 전에 생성
                        dtaId = MapUtils.getInteger(innerParam, "dtaId");
                        innerParam.put("dtaId", dtaId);

                        // std_dta_result_info 생성
                        int sdriCnt = createStdDtaResultInfoForAiCustomLearningEach(innerParam);

                        resultMap.put("resultId", MapUtils.getInteger(innerParam, "id"));

                        // std_dta_result_detail 생성
                        int sdrdCnt = createStdDtaResultDetailForAiCustomLearning(innerParam);

                        break;
                    case "2":
                        // 과제로 내기
                        taskId = MapUtils.getLong(innerParam, "taskId");
                        innerParam.put("taskId", taskId);

                        // task_result_info 생성
                        int triCnt = createTaskResultInfoForAiCustomLearningEach(innerParam, totalArticleList);
                        log.info("created task_result_info cnt:{}", triCnt);

                        resultMap.put("resultId", MapUtils.getInteger(innerParam, "id"));

                        // task_result_detail 생성
                        int trdCnt = createTaskResultDetailForAiCustomLearning(innerParam);
                        log.info("created task_result_detail cnt:{}", trdCnt);
                        break;
                    default:
                        log.warn("lrnMethod is not define!!!-{}", paramData.get("lrnMethod"));
                }
            } else {
                log.error("Sets not created.");
            }
        }
        resultMap.put("setsId", setsId);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> findTargetEvlList(Map<String, Object> paramData) throws Exception {
        return aiLearningEngMapper.findTargetEvlList(paramData);
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> findTargetTaskList(Map<String, Object> paramData) throws Exception {
        return aiLearningEngMapper.findTargetTaskList(paramData);
    }

    private Map<String, Object> getOldSetsMap(String type, int id) throws Exception {
        Map<String, Object> oldSetsMap = null;
        if ("eval".equals(type)) {
            oldSetsMap = aiLearningMapper.getSetInfoByEvlId(id);
        }
        if ("task".equals(type)) {
            oldSetsMap = aiLearningMapper.getSetInfoByTaskId(id);
        }
        return oldSetsMap;
    }

    private Map<String, Object> createSetsForAiLearning(List<AiArticleVO> aiArticleVOList, Map<String, Object> oldSetsMap) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 추가할 세트지 정보를 생성한다.
            if (oldSetsMap != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                SetsSaveVO newSetsVo = objectMapper.convertValue(oldSetsMap, SetsSaveVO.class);

                newSetsVo.clean();
                newSetsVo.setId(null);
                newSetsVo.setDescription("처방학습");
                newSetsVo.setIs_active(true);
                newSetsVo.setIs_publicOpen(true);
                newSetsVo.setPoints_type("auto");

                // meta 정보 조회 및 세팅
                List<Map> oldMetaMapList =  aiLearningMapper.getMetaListBySetId((String)oldSetsMap.get("id"));

                //새로이 add할 메타맵정보 만들기
                List<MetaVO> newMetaList = new ArrayList<>();
                for (Map<String, Object> oldMetaMap: oldMetaMapList) {
                    MetaVO metaVO = objectMapper.convertValue(oldMetaMap, MetaVO.class);
                    newMetaList.add(metaVO);
                }
                newSetsVo.set_meta(newMetaList);
                newSetsVo.setArticles(this.makeArticleVoList(aiArticleVOList));

                SetsSaveRequestVO setsSaveRequestVO = new SetsSaveRequestVO();
                setsSaveRequestVO.setSaveType("insert");
                setsSaveRequestVO.setSets(newSetsVo);
                setsSaveRequestVO.setArticles(this.makeSetsArticleMapVoList(aiArticleVOList));
                // 세트지 생성
                String setsId = contentsService.saveSetInfo(setsSaveRequestVO);

                resultMap.put("isSuccess", true);
                resultMap.put("setsId", setsId); // 신규 생성된 세트지 id
                resultMap.put("errMsg", "");
            }
            else {
                resultMap.put("isSuccess", false);
                resultMap.put("setsId", null);
                resultMap.put("errMsg", "세트지 정보를 찾을 수 없습니다.");
            }
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("isSuccess", false);
            resultMap.put("setsId", null);
            resultMap.put("errMsg", "세트지 생성 에러");
        }

        return resultMap;
    }

    private Long createEvlResultInfo(String newSetId, long evlId, String mamoymId, String creator) throws Exception {
        Map<String, Object> paramData = new HashMap<>();

        paramData.put("evlId", evlId);
        paramData.put("setId", newSetId);
        paramData.put("mamoymId", mamoymId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createEvlResultInfo(paramData);

        return AidtCommonUtil.getLongValueFromObject(paramData.get("id"));
    }

    private int createEvlResultDetail(String setId, Long evlResultId, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("evlResultId", evlResultId);
        paramData.put("setId", setId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createEvlResultDetail(paramData);
        log.info("createEvlResultDetail.cnt:{}",cnt);

        return cnt;
    }

    private int updateAfterCreateEvlInfo(int evlId, String prscrStdCrtAt, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();

        paramData.put("evlId", evlId);
        paramData.put("prscrStdCrtAt", prscrStdCrtAt);
        paramData.put("creator", creator);

        log.debug("{}-prscrStdCrtAt:{}", evlId, prscrStdCrtAt);

        return aiLearningMapper.updateAfterCreateEvlInfo(paramData);

    }

    private Long createTaskResultInfo(String newSetsId, long taskId, String mamoymId, String creator) throws Exception {
        Map<String, Object> paramData = new HashMap<>();

        paramData.put("taskId", taskId);
        paramData.put("setsId", newSetsId);
        paramData.put("mamoymId", mamoymId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createTaskResultInfo(paramData);

        return AidtCommonUtil.getLongValueFromObject(paramData.get("id"));
    }

    private int createTaskResultDetail(String setId, Long taskResultId, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("taskResultId", taskResultId);
        paramData.put("setsId", setId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createTaskResultDetail(paramData);
        log.info("createTaskResultDetail.cnt:{}",cnt);

        return cnt;
    }

    private int updateAfterCreateTaskInfo(int taskId, String prscrStdCrtAt, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();

        paramData.put("taskId", taskId);
        paramData.put("prscrStdCrtAt", prscrStdCrtAt);
        paramData.put("creator", creator);

        log.debug("{}-prscrStdCrtAt:{}", taskId, prscrStdCrtAt);

        return aiLearningMapper.updateAfterCreateTaskInfo(paramData);

    }

    private String getMeanVo(AiArticleVO faultVo) throws Exception {
        return Stream.of(
                        faultVo.getStudyMap1(),
                        faultVo.getDifficulty(),
                        faultVo.getEvaluationArea(),
                        faultVo.getEvaluationArea3(),
                        faultVo.getContentsItem()
                )
                .map(value -> value != null && value > 0 ? String.valueOf(value) : "0")
                .collect(Collectors.joining("_"));
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> findAutoCreateAiLearningEvl(Map<String, Object> paramData) throws Exception {

        log.info("evlId:{} START==============================", paramData.get("evlId"));

        // temporary table 을 만들면 빠르고, 더 간단하게 로직을 만들 수 있겠지만, db계정에 create 권한이 없을 것 같아 자바로직으로 진행함.

        // 1. 어제 (00시 ~ 24시) 사이에 완료된 평가 중에서
        //    - 제출여부=Y, 채점여부=Y, errata=2(오답), mrk_ty=1(자동채점) 인 article 정보 조회
        // evl_id 는 이 앞에서 조회하여 evl_id 별로 호출 했으므로, 날짜 조건은 뺀다.
        List<AiArticleVO> targetArticleList = aiLearningEngMapper.findAutoCreateAiLearningEvlStep1(paramData);

        // 2. 학생별로 loop 돌면서, 처방학습 article 목록을 반환
        // 대상학생 조회
        List<String> mamoymIdList = targetArticleList.stream()
                .map(AiArticleVO::getMamoym_id)
                .distinct().toList();

        Map<String, Object> innerParam = new HashMap<>();

        // 모든 처리를 끝내고 반환할 AI처방 article 목록
        List<AiArticleListByStntVO> resultList = new ArrayList<>();

        for (String mamoymId : mamoymIdList) {
            log.debug("mamoymId:{} -------------------------------", mamoymId);

            List<AiArticleVO> innerResultList = new ArrayList<>(); // 학생별 추가할 AI처방 article목록

            // 학생별 오답 article 목록만 필터링
            List<AiArticleVO> userArticleList = targetArticleList
                    .stream().filter(a -> mamoymId.equals(a.getMamoym_id()))
                    .toList();

            List<Long> studyMap1List = userArticleList.stream()
                    .map(AiArticleVO::getStudyMap1)
                    .distinct().toList();

            List<Long> studyMap2List = userArticleList.stream()
                    .map(AiArticleVO::getStudyMap2)
                    .distinct().toList();

            // 추가조건 (wrter_id,cla_id,textbk_id) 추가
            innerParam.putAll(paramData);
            // 학생별 학습이력을 조회한다.
            innerParam.put("mamoymId", mamoymId);
            innerParam.put("studyMap1List", studyMap1List);
            innerParam.put("studyMap2List", studyMap2List);
            List<Map> hisArticleList = aiLearningEngMapper.findAutoCreateAiLearningEvlStep2(innerParam); // 학습이력이 있는 article 정보

            // 학습완료 하여 제외시킬 articleId 목록
            List<String> removeIdList = hisArticleList.stream()
                    .map(m -> (String) m.get("article_id"))
                    .distinct().toList();

            // 새로 추가되어 제외시켜야 할 articleId 목록
            List<String> addedIdList = new ArrayList<>();

            innerParam.clear();
            innerParam.put("removeIdList", removeIdList);
            innerParam.put("addedIdList", addedIdList);
            for (AiArticleVO faultVo : userArticleList) { // 오답 article 목록
                innerParam.put("gubun", this.getMeanVo(faultVo)); // studyMap1 ~ difficulty 까지 "_" 로 합친 구분값
                innerParam.put("studyMap1", faultVo.getStudyMap1());
                innerParam.put("studyMap2", faultVo.getStudyMap2());
                innerParam.put("studyMap3", faultVo.getStudyMap3());
                innerParam.put("studyMap_1", faultVo.getStudyMap_1());
                innerParam.put("studyMap_2", faultVo.getStudyMap_2());
                innerParam.put("difficulty", faultVo.getDifficulty());
                AiArticleVO matchArticleVo = aiLearningEngMapper.findAutoCreateAiLearningEvlStep3(innerParam); // 매칭되는 article 1건만 조회

                if (matchArticleVo != null) {
                    matchArticleVo.setMamoym_id(mamoymId);
                    matchArticleVo.setEvl_id(Long.valueOf(String.valueOf(paramData.get("evlId"))));
                    innerResultList.add(matchArticleVo); // 학생별 추천 아티클 목록 add

                    addedIdList.add(matchArticleVo.getArticle_id()); // 결과에 추가되어 제외시켜야할 article
                    log.debug("matched {}: {}-{}", mamoymId, faultVo.getArticle_id(), matchArticleVo.getArticle_id());
                } else {
                    // 매칭되는 article 이 없을때.
                    log.warn("matchArticle is null!! : {}-{}", mamoymId, faultVo.getArticle_id());

                    // 학습 완료한 history 에서 가져와서 넣는다.(articleType=21 문항, articleCategory=61 비교과)
                    Optional<Map> foundMap = hisArticleList.stream()
                            .filter(map -> !addedIdList.contains(map.get("article_id")))
                            .filter(map -> Objects.equals(map.get("studyMap1"), faultVo.getStudyMap1()))
                            .filter(map -> Objects.equals(map.get("studyMap2"), faultVo.getStudyMap2()))
                            .filter(map -> map.get("articleType") != null && (Integer) map.get("articleType") == 21)
                            .filter(map -> map.get("articleCategory") != null && (Integer) map.get("articleCategory") == 61)
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.isEmpty()
                                            ? Optional.<Map>empty()
                                            : Optional.of(list.get(new SecureRandom().nextInt(list.size())))
                            ));


                    if (foundMap.isPresent()) {
                        // history 에서 뒤져서 있으면
                        Map addMap = foundMap.get();

                        AiArticleVO _matchHistVo = AiArticleVO.builder()
                                .mamoym_id(mamoymId)
                                .textbook_id(AidtCommonUtil.getLongValueFromObject(addMap.get("textbook_id")))
                                .evl_id(AidtCommonUtil.getLongValueFromObject(paramData.get("evlId")))
                                .article_id(addMap.get("article_id").toString())
                                .studyMap1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap1")))
                                .difficulty(AidtCommonUtil.getLongValueFromObject(addMap.get("difficulty")))
                                .evaluationArea(AidtCommonUtil.getLongValueFromObject(addMap.get("evaluationArea")))
                                .evaluationArea3(AidtCommonUtil.getLongValueFromObject(addMap.get("evaluationArea3")))
                                .contentsItem(AidtCommonUtil.getLongValueFromObject(addMap.get("contentsItem")))
                                .articleCategory(AidtCommonUtil.getLongValueFromObject(addMap.get("articleCategory")))
                                .gubun((String) addMap.get("gubun"))
                                .build();

                        innerResultList.add(_matchHistVo); // 학생별 추천 아티클 목록 add

                        addedIdList.add(addMap.get("article_id").toString()); // 추가된 목록에 id add
                    } else {
                        // history 에도 없어서, 최종 추가 실패
                        log.warn("finally not found! : {}-{}", mamoymId, faultVo.getArticle_id());
                    }
                }
            }

            AiArticleListByStntVO aiArticleListByStntVO = AiArticleListByStntVO.builder()
                    .mamoymId(mamoymId)
                    .evlId(AidtCommonUtil.getLongValueFromObject(paramData.get("evlId")))
                    .articleList(innerResultList)
                    .build();

            resultList.add(aiArticleListByStntVO); // return list 에 add

        } // for (String mamoymId : mamoymIdList) END

        log.debug("resultList.size={}", resultList.size());
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evlId", paramData.get("evlId"));
        returnMap.put("stntArticleList", resultList);
        return returnMap;
    }


    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> findAutoCreateAiLearningEvlWithDiagnostic(
            Map<String, Object> paramData) throws Exception {

        log.info("evlId:{} START==============================", paramData.get("evlId"));

        // temporary table 을 만들면 빠르고, 더 간단하게 로직을 만들 수 있겠지만, db계정에 create 권한이 없을 것 같아 자바로직으로 진행함.

        // 1. 어제 (00시 ~ 24시) 사이에 완료된 평가 중에서
        //    - 제출여부=Y, 채점여부=Y, errata=2(오답), mrk_ty=1(자동채점) 인 article 정보 조회
        // evl_id 는 이 앞에서 조회하여 evl_id 별로 호출 했으므로, 날짜 조건은 뺀다.
        List<AiArticleVO> targetArticleList = aiLearningEngMapper.findAutoCreateAiLearningEvlStep1(paramData);

        // 2. 학생별로 loop 돌면서, 처방학습 article 목록을 반환
        // 대상학생 조회
        List<String> mamoymIdList = targetArticleList.stream()
                .map(AiArticleVO::getMamoym_id)
                .distinct().toList();

        Map<String, Object> innerParam = new HashMap<>();

        // 모든 처리를 끝내고 반환할 AI처방 article 목록
        List<AiArticleListByStntVO> resultList = new ArrayList<>();

        for (String mamoymId : mamoymIdList) {
            log.debug("mamoymId:{} -------------------------------", mamoymId);

            List<AiArticleVO> innerResultList = new ArrayList<>(); // 학생별 추가할 AI처방 article목록

            // 학생별 오답 article 목록만 필터링
            List<AiArticleVO> userArticleList = targetArticleList
                    .stream().filter(a -> mamoymId.equals(a.getMamoym_id()))
                    .toList();

            // 추가조건 (wrter_id,cla_id,textbk_id) 추가
            innerParam.putAll(paramData);
            // 학생별 학습이력을 조회한다.
            innerParam.put("mamoymId", mamoymId);
            List<Map> hisArticleList = aiLearningEngMapper.findAutoCreateAiLearningEvlStep2(innerParam); // 학습이력이 있는 article 정보

            // 학습완료 하여 제외시킬 articleId 목록
            List<String> removeIdList = hisArticleList.stream()
                    .map(m -> (String) m.get("article_id"))
                    .distinct().toList();

            // 새로 추가되어 제외시켜야 할 articleId 목록
            List<String> addedIdList = new ArrayList<>();

            innerParam.clear();
            innerParam.put("removeIdList", removeIdList);
            innerParam.put("addedIdList", addedIdList);
            for (AiArticleVO faultVo : userArticleList) { // 오답 article 목록
                Map<String, Object> innerParamSet = Map.of(
                        "articleId",        faultVo.getArticle_id(),
                        "subId",            faultVo.getSub_id(),
                        "wrterId",          MapUtils.getString(paramData, "wrterId", ""),
                        "claId",            MapUtils.getString(paramData, "claId", ""),
                        "textbkId",         faultVo.getTextbook_id(),
                        "mamoymId",         faultVo.getMamoym_id(),
                        "removeIdList",     removeIdList,
                        "addedIdList",      addedIdList,
                        "limitNum", 1
                );



                Map<String, Object> resultData =
                        evalReportMapper.findAutoCreateAiLearningEvlWithDiagnosticAnotherQuestion(innerParamSet);

                AiArticleVO matchArticleVo = new AiArticleVO();

                if (ObjectUtils.isNotEmpty(resultData)) {
                    // 1. 아티클(유사,쌍둥이) 맵(article_article_map)에서 조회
                    matchArticleVo.setArticle_id(MapUtils.getString(resultData, "articleId", ""));
                    matchArticleVo.setStudyMap1(MapUtils.getLong(resultData, "studyMap1", 0L));
                    matchArticleVo.setDifficulty(MapUtils.getLong(resultData, "difficulty", 0L));
                    matchArticleVo.setEvaluationArea(MapUtils.getLong(resultData, "evaluationArea", 0L));
                    matchArticleVo.setEvaluationArea3(MapUtils.getLong(resultData, "evaluationArea3", 0L));
                    matchArticleVo.setContentsItem(MapUtils.getLong(resultData, "contentsItem", 0L));
                    matchArticleVo.setGubun(MapUtils.getString(resultData, "gubun", ""));
                    matchArticleVo.setThumbnail(MapUtils.getString(resultData, "thumbnail", ""));
                    matchArticleVo.setName(MapUtils.getString(resultData, "name", ""));
                } else {
                    // 2. 기존 로직 실행
                    Map<String, Object> basicData =
                            evalReportMapper.findAutoCreateAiLearningEvlWithDiagnosticAnotherQuestionIfNull(innerParamSet);

                    if (ObjectUtils.isNotEmpty(basicData)) {
                        matchArticleVo.setArticle_id(MapUtils.getString(basicData, "articleId", ""));
                        matchArticleVo.setStudyMap1(MapUtils.getLong(basicData, "studyMap1", 0L));
                        matchArticleVo.setDifficulty(MapUtils.getLong(basicData, "difficulty", 0L));
                        matchArticleVo.setEvaluationArea(MapUtils.getLong(basicData, "evaluationArea", 0L));
                        matchArticleVo.setEvaluationArea3(MapUtils.getLong(basicData, "evaluationArea3", 0L));
                        matchArticleVo.setContentsItem(MapUtils.getLong(basicData, "contentsItem", 0L));
                        matchArticleVo.setGubun(MapUtils.getString(basicData, "gubun", ""));
                        matchArticleVo.setThumbnail(MapUtils.getString(basicData, "thumbnail", ""));
                        matchArticleVo.setName(MapUtils.getString(basicData, "name", ""));
                    } else {
                        log.error("not found articleId : {}", innerParamSet);
                    }
                }

                if (StringUtils.isNotEmpty(matchArticleVo.getArticle_id())) {
                    matchArticleVo.setMamoym_id(mamoymId);
                    matchArticleVo.setEvl_id(Long.valueOf(String.valueOf(paramData.get("evlId"))));
                    innerResultList.add(matchArticleVo); // 학생별 추천 아티클 목록 add

                    addedIdList.add(matchArticleVo.getArticle_id()); // 결과에 추가되어 제외시켜야할 article
                    log.debug("matched {}: {}-{}", mamoymId, faultVo.getArticle_id(), matchArticleVo.getArticle_id());
                } else {
                    log.error("not found articleId : {}", innerParamSet);
                }
            }

            if (!innerResultList.isEmpty()) {
                AiArticleListByStntVO aiArticleListByStntVO = AiArticleListByStntVO.builder()
                        .mamoymId(mamoymId)
                        .evlId(AidtCommonUtil.getLongValueFromObject(paramData.get("evlId")))
                        .articleList(innerResultList)
                        .build();

                resultList.add(aiArticleListByStntVO); // return list 에 add
            } else {
                log.error("not found articleIds : {}", mamoymId);
            }

        } // for (String mamoymId : mamoymIdList) END

        log.debug("resultList.size={}", resultList.size());
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evlId", paramData.get("evlId"));
        returnMap.put("stntArticleList", resultList);
        return returnMap;
    }

    public Map<String, Object> createAiLearningBatchEvlEng(Map<String, Object> paramData) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchEvl START-------------------------------");

        int setCount = 0; // 세트지 생성건수
        int idCount = 0; // 대상 평가id 건수
        int articleCount = 0; // 추출된 총 article 갯수
        int stntCnt = 0; // 대상 학생 수
        int successCnt = 0; // 성공하여 세트지 생성완료된 개수
        int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

        List<Map<String, Object>> evlList = this.findTargetEvlList(paramData);

        if (CollectionUtils.isNotEmpty(evlList)) {
            for (Map<String,Object> evlInfo : evlList) {
                int evlId = MapUtils.getIntValue(evlInfo, "id");
                int evlSetCount = 0; // 평가지당 생성된 세트지 건수

                log.info("{}.evlId:{}", ++idCount, evlId);

                Map<String, Object> oldSetsMap = this.getOldSetsMap("eval", evlId); // 참고할 세트지
                String creator = String.valueOf(oldSetsMap.get("creator")); // 세트지와 join 한 evl_info 에서 가져온 작성자id

                Map<String, Object> innerParam = new HashMap<>();
                innerParam.put("evlId", evlId);

                // 추가조건 (wrter_id,cla_id,textbk_id) 추가
                innerParam.putAll(evlInfo);
                innerParam.remove("id");

                // 출제할 아티클을 추출
                Map<String, Object> articleMap = (Map<String, Object>) this.findAutoCreateAiLearningEvl(innerParam);

                // 출제할 학생별 아티클이 있음.
                if (articleMap.get("stntArticleList") != null && !((List)articleMap.get("stntArticleList")).isEmpty()) {

                    List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

                    // 학생의 아티클이 있을때만 eval 생성 및 진행
                    if (CollectionUtils.isNotEmpty(stntArticleList)) {
                        // evl_info 생성
                        int newEvlInfoCnt = aiLearningMapper.insertEvlInfo(innerParam);

                        long  newId = AidtCommonUtil.getLongValueFromObject(innerParam.get("id")); // 생성된 evlId
                        log.info("newId:{}", newId);

                        // 학생별
                        for (AiArticleListByStntVO _stntArticleVo: stntArticleList) {

                            String mamoymId = _stntArticleVo.getMamoymId();
                            List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                            // 학생이 가진 article 목록으로 세트지 생성
                            Map<String, Object> createResultMap = this.createSetsForAiLearning(aiArticleVOList, oldSetsMap);

                            boolean isSuccess = (boolean) createResultMap.get("isSuccess");
                            String newSetId = (String)createResultMap.getOrDefault("setsId", "") ;
                            String errMsg = String.valueOf(createResultMap.get("errMsg"));


                            log.info("isSucces:{}", isSuccess); // 생성시 오류가 없었다는 뜻. 실제 세트지가 생성된 것과는 무관
                            log.info("setsId:{}", newSetId); // 생성된 setId : null or 0 이 아니면 정상적으로 생성되었다는 뜻
                            log.info("errMsg:{}", errMsg);

                            // 세트지 생성에 성공했으면 count++
                            if (isSuccess && ObjectUtils.isNotEmpty(newSetId)) {
                                log.info("Sets Created:{}", newSetId);
                                setCount ++;
                                evlSetCount ++;

                                //세트지가 만들어 졌으므로, SetSummary, evl_result_info, evl_result_detail 정보를 생성한다.
                                log.info("SetSummary 생성-{}", evlId);
                                SetSummarySaveRequestVO setSummarySaveRequestVO = this.createSetSummary(newSetId, creator, aiArticleVOList);

                                log.info("evl_result_info 생성-{}", evlId);
                                Long newEvlResultId = null;
                                newEvlResultId = this.createEvlResultInfo(newSetId, newId,  mamoymId, creator);
                                log.info("newEvlResultId:{}", newEvlResultId);

                                log.info("evl_result_detail 생성-{}", evlId);
                                int detailCnt = this.createEvlResultDetail(newSetId, newEvlResultId, creator);
                                log.info("evl_result_detail.cnt:{}", detailCnt);
                            }
                            else {
                                log.warn("Fail Sets Creation:{}", evlId);
                            }
                            stntCnt ++; // 세트지 성공여부에 관계없이 학생수 증가
                            articleCount =+ _stntArticleVo.getArticleList().size(); // 세트지 성공여부에 관계없이 아티클수 증가
                        }

                    }


                }

                // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
                String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때
                if (evlSetCount > 0) {
                    prscrStdCrtAt = "Y"; // 뭔가 생성 됐을때. 모든 학생 및 set지를 다 판단 할 수는 없음.
                    successCnt ++;
                } else {
                    failCnt ++;
                }

                int updateCnt = this.updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchEvl END------------------------------- : {}", (endTime - startTime));

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("duration", endTime - startTime);
        returnMap.put("setCount", setCount);
        returnMap.put("idCount", idCount);
        returnMap.put("articleCount", articleCount);
        returnMap.put("stntCnt", stntCnt);
        returnMap.put("successCnt", successCnt);
        returnMap.put("failCnt", failCnt);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findAutoCreateAiLearningTask(Map<String, Object> paramData) throws Exception {

        log.info("taskId:{} START==============================", paramData.get("taskId"));

        // temporary table 을 만들면 빠르고, 더 간단하게 로직을 만들 수 있겠지만, db계정에 create 권한이 없을 것 같아 자바로직으로 진행함.

        // 1. 어제 (00시 ~ 24시) 사이에 완료된 평가 중에서
        //    - 제출여부=Y, 채점여부=Y, errata=2(오답), mrk_ty=1(자동채점) 인 article 정보 조회
        // evl_id 는 이 앞에서 조회하여 evl_id 별로 호출 했으므로, 날짜 조건은 뺀다.
        List<AiArticleVO> targetArticleList =  aiLearningEngMapper.findAutoCreateAiLearningTaskStep1(paramData);

        // 2. 학생별로 loop 돌면서, 처방학습 article 목록을 반환
        // 대상학생 조회
        List<String> mamoymIdList = targetArticleList.stream()
                .map(a -> a.getMamoym_id())
                .distinct().toList();

        Map<String, Object> innerParam = new HashMap<>();

        // 모든 처리를 끝내고 반환할 AI처방 article 목록
        List<AiArticleListByStntVO> resultList = new ArrayList<>();

        for (String mamoymId : mamoymIdList) {
            log.debug("mamoymId:{} -------------------------------", mamoymId);

            List<AiArticleVO> innerResultList = new ArrayList<>(); // 학생별 추가할 AI처방 article목록

            // 학생별 오답 article 목록만 필터링
            List<AiArticleVO> userArticleList = targetArticleList
                    .stream().filter(a -> mamoymId.equals(a.getMamoym_id()))
                    .collect(Collectors.toList());

            // 추가조건 (wrter_id,cla_id,textbk_id) 추가
            innerParam.putAll(paramData);
            // 학생별 학습이력을 조회한다.
            innerParam.put("mamoymId", mamoymId);
            List<Map> hisArticleList =  aiLearningEngMapper.findAutoCreateAiLearningEvlStep2(innerParam); // 학습이력이 있는 article 정보 (evl 과 공통사용)

            // 학습완료 하여 제외시킬 articleId 목록
            List<String>  removeIdList = hisArticleList.stream()
                    .map(m -> (String)m.get("article_id") )
                    .distinct().toList();

            // 새로 추가되어 제외시켜야 할 articleId 목록
            List<String> addedIdList = new ArrayList<>();

            innerParam.clear();
            innerParam.put("removeIdList", removeIdList);
            innerParam.put("addedIdList", addedIdList);
            for (AiArticleVO faultVo : userArticleList) { // 오답 article 목록
//                innerParam.put("gubun", faultVo.getGubun()); // studyMap1 ~ difficulty 까지 "_" 로 합친 구분값
                innerParam.put("studyMap1", faultVo.getStudyMap1());
                innerParam.put("studyMap2", faultVo.getStudyMap2());
                innerParam.put("studyMap3", faultVo.getStudyMap3());
                innerParam.put("studyMap_1", faultVo.getStudyMap_1());
                innerParam.put("studyMap_2", faultVo.getStudyMap_2());
                innerParam.put("difficulty", faultVo.getDifficulty());
                AiArticleVO matchArticleVo = aiLearningEngMapper.findAutoCreateAiLearningEvlStep3(innerParam); // 매칭되는 article 1건만 조회 (evl 과 공통사용)

                if (matchArticleVo != null) {
                    matchArticleVo.setMamoym_id(mamoymId);
                    matchArticleVo.setTask_id(Long.valueOf(String.valueOf(paramData.get("taskId"))));
                    innerResultList.add(matchArticleVo); // 학생별 추천 아티클 목록 add

                    addedIdList.add(matchArticleVo.getArticle_id().toString()); // 결과에 추가되어 제외시켜야할 article
                    log.debug("matched {}: {}-{}", mamoymId, faultVo.getArticle_id(), matchArticleVo.getArticle_id());
                }
                else {
                    // 매칭되는 article 이 없을때.
                    log.warn("matchArticle is null!! : {}-{}", mamoymId, faultVo.getArticle_id());

                    // 학습 완료한 history 에서 가져와서 넣는다.(articleType=21 문항, articleCategory=61 비교과)
                    Optional<Map> foundMap = hisArticleList.stream()
                            .filter(map -> !addedIdList.contains(map.get("article_id")))
                            .filter(map -> map.get("articleType") != null && (Integer)map.get("articleType") == 21)
                            .filter(map -> map.get("articleCategory") != null && (Integer)map.get("articleCategory") == 61)
                            .findFirst();


                    if (foundMap.isPresent()) {
                        // history 에서 뒤져서 있으면
                        Map addMap = foundMap.get();

                        AiArticleVO _matchHistVo =  AiArticleVO.builder()
                                .mamoym_id(mamoymId)
                                .textbook_id(AidtCommonUtil.getLongValueFromObject(addMap.get("textbook_id")))
                                .task_id(AidtCommonUtil.getLongValueFromObject(paramData.get("taskId")))
                                .article_id(addMap.get("article_id").toString())
                                .studyMap1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap1")))
                                .difficulty(AidtCommonUtil.getLongValueFromObject(addMap.get("difficulty")))
                                .evaluationArea(AidtCommonUtil.getLongValueFromObject(addMap.get("evaluationArea")))
                                .evaluationArea3(AidtCommonUtil.getLongValueFromObject(addMap.get("evaluationArea3")))
                                .contentsItem(AidtCommonUtil.getLongValueFromObject(addMap.get("contentsItem")))
                                .articleCategory(AidtCommonUtil.getLongValueFromObject(addMap.get("articleCategory")))
                                .gubun((String)addMap.get("gubun"))
                                .build();

                        innerResultList.add(_matchHistVo); // 학생별 추천 아티클 목록 add

                        addedIdList.add((String) addMap.get("article_id")); // 추가된 목록에 id add
                    } else {
                        // history 에도 없어서, 최종 추가 실패
                        log.warn("finally not found! : {}-{}", mamoymId, faultVo.getArticle_id());
                    }
                }
            }

            AiArticleListByStntVO aiArticleListByStntVO = AiArticleListByStntVO.builder()
                    .mamoymId(mamoymId)
                    .taskId(AidtCommonUtil.getLongValueFromObject(paramData.get("taskId")))
                    .articleList(innerResultList)
                    .build();

            resultList.add(aiArticleListByStntVO); // return list 에 add

        } // for (String mamoymId : mamoymIdList) END

        log.debug("resultList.size={}", resultList.size());
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskId", paramData.get("taskId"));
        returnMap.put("stntArticleList", resultList);
        return returnMap;
    }

    public Map<String, Object> createAiLearningBatchTaskEng(Map<String, Object> paramData) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchTask START-------------------------------");

        int setCount = 0; // 세트지 생성건수
        int idCount = 0; // 대상 평가id 건수
        int articleCount = 0; // 추출된 총 article 갯수
        int stntCnt = 0; // 대상 학생 수
        int successCnt = 0; // 성공하여 세트지 생성완료된 개수
        int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

        List<Map<String, Object>> taskList = this.findTargetTaskList(paramData);

        if (CollectionUtils.isNotEmpty(taskList)) {
            for (Map<String,Object> taskInfo : taskList) {
                int taskId = MapUtils.getIntValue(taskInfo, "id");
                int taskSetCount = 0; // 과제 당 생성된 세트지 건수

                log.info("{}.taskId:{}", ++idCount, taskId);

                Map<String, Object> oldSetsMap = getOldSetsMap("task", taskId); // 참고할 세트지
                String creator = String.valueOf(oldSetsMap.get("creator")); // 세트지와 join 한 evl_info 에서 가져온 작성자id

                Map<String, Object> innerParam = new HashMap<>();
                innerParam.put("taskId", taskId);

                // 추가조건 (wrter_id,cla_id,textbk_id) 추가
                innerParam.putAll(taskInfo);
                innerParam.remove("id");

                // 출제할 아티클을 추출
                Map<String, Object> articleMap = (Map<String, Object>) this.findAutoCreateAiLearningTask(innerParam);

                // 출제할 학생별 아티클이 있음.
                if (articleMap.get("stntArticleList") != null && !((List)articleMap.get("stntArticleList")).isEmpty()) {

                    List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

                    // 아티클이 있을때만 task 생성
                    if (CollectionUtils.isNotEmpty(stntArticleList)) {

                        // task_info 생성
                        int newTaskInfoCnt = aiLearningMapper.insertTaskInfo(innerParam);

                        long newId = AidtCommonUtil.getLongValueFromObject(innerParam.get("id")); // 생성된 evlId
                        log.info("newId:{}", newId);

                        // 학생별
                        for (AiArticleListByStntVO _stntArticleVo : stntArticleList) {

                            String mamoymId = _stntArticleVo.getMamoymId();
                            List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                            // 학생이 가진 article 목록으로 세트지 생성
                            Map<String, Object> createResultMap = createSetsForAiLearning(aiArticleVOList, oldSetsMap);

                            boolean isSuccess = (boolean) createResultMap.get("isSuccess");
                            String newSetId = (String) createResultMap.getOrDefault("setsId", "");
                            String errMsg = String.valueOf(createResultMap.get("errMsg"));


                            log.info("isSucces:{}", isSuccess); // 생성시 오류가 없었다는 뜻. 실제 세트지가 생성된 것과는 무관
                            log.info("setsId:{}", newSetId); // 생성된 setId : null or 0 이 아니면 정상적으로 생성되었다는 뜻
                            log.info("errMsg:{}", errMsg);

                            // 세트지 생성에 성공했으면 count++
                            if (isSuccess && ObjectUtils.isNotEmpty(newSetId)) {
                                log.info("Sets Created:{}", newSetId);
                                setCount++;
                                taskSetCount++;

                                //세트지가 만들어 졌으므로, SetSummary, task_result_info, task_result_detail 정보를 생성한다.
                                log.info("SetSummary 생성-{}", taskId);
                                SetSummarySaveRequestVO setSummarySaveRequestVO = createSetSummary(newSetId, creator, aiArticleVOList);

                                log.info("task_result_info 생성-{}", taskId);
                                Long newTaskResultId = null;
                                newTaskResultId = this.createTaskResultInfo(newSetId, newId, mamoymId, creator);
                                log.info("newTaskResultId:{}", newTaskResultId);

                                log.info("task_result_detail 생성-{}", taskId);
                                int detailCnt = this.createTaskResultDetail(newSetId, newTaskResultId, creator);
                                log.info("task_result_detail.cnt:{}", detailCnt);
                            } else {
                                log.warn("Fail Sets Creation:{}", taskId);
                            }
                            stntCnt++; // 세트지 성공여부에 관계없이 학생수 증가
                            articleCount = +_stntArticleVo.getArticleList().size(); // 세트지 성공여부에 관계없이 아티클수 증가
                        }
                    }
                }

                // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
                String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때
                if (taskSetCount > 0) {
                    prscrStdCrtAt = "Y"; // 뭔가 생성 됐을때. 모든 학생 및 set지를 다 판단 할 수는 없음.
                    successCnt ++;
                } else {
                    failCnt ++;
                }

                int updateCnt = this.updateAfterCreateTaskInfo(taskId, prscrStdCrtAt, creator);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchTask END------------------------------- : {}", (endTime - startTime));

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("duration", endTime - startTime);
        returnMap.put("setCount", setCount);
        returnMap.put("idCount", idCount);
        returnMap.put("articleCount", articleCount);
        returnMap.put("stntCnt", stntCnt);
        returnMap.put("successCnt", successCnt);
        returnMap.put("failCnt", failCnt);

        return returnMap;

    }

    private Object getEamScp(Map<String, Object> innerParam) throws Exception {
        return aiLearningEngMapper.getEamScp(innerParam);
    }

    private int createConfigAiCustomLearning(Map<String, Object> innerParam) throws Exception {
         innerParam.put("eamExmNum", MapUtils.getIntValue(innerParam, "eamExmNum", 0)); // db 컬럼 속성이 not null 이라서.. 세팅해야 함.
         innerParam.put("eamGdExmMun", MapUtils.getIntValue(innerParam, "eamGdExmMun", 0));
         innerParam.put("eamAvExmMun", MapUtils.getIntValue(innerParam, "eamAvExmMun", 0));
         innerParam.put("eamBdExmMun", MapUtils.getIntValue(innerParam, "eamBdExmMun", 0));
         innerParam.put("aiTutSetAt", MapUtils.getString(innerParam, "aiTutSetAt", "N"));
         int cnt =  aiLearningMapper.createConfigAiCustomLearning(innerParam);

         return MapUtils.getInteger(innerParam, "id", 0);

    }

    private int createConfigAiCustomResult(Map<String, Object> paramData) throws Exception {
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);
        int iLev;

        innerParam.put("stdt_id", MapUtils.getString(innerParam,"stntId"));

        iLev = switch (MapUtils.getString(innerParam, "lev")) {
            case "gd" -> 1;
            case "av" -> 2;
            case "lw" -> 3;
            default -> 0;
        };

        innerParam.put("gd_av_bd_group_cd", iLev); // 상중하 레벨정보

        return aiLearningMapper.createConfigAiCustomResult(innerParam);
    }

    private void validateDatePara(Map<String, Object> paramData) throws Exception {

        String stDt = String.valueOf(paramData.getOrDefault("pdEvlStDt", ""));
        String edDt = String.valueOf(paramData.getOrDefault("pdEvlEdDt", ""));

        if (StringUtils.equals("2", String.valueOf(paramData.get("lrnMethod")))) { // 과제
            if (StringUtils.isBlank(stDt)) {
                throw new AidtException("시작일자가 지정되어 있지 않습니다.");
            }

            if (!isValidDateFormat(AidtConst.FORMAT_STRING_YMDHM, stDt)) {
                throw new AidtException("올바른 날짜형식이 아닙니다. \n(ex. 2024-12-31 15:00 과 같은 형식으로 입력해 주세요.)");
            }

            // 종료일자가 있으면 정합성판단, 없으면 계산해서 세팅한다. (+1일 23:59)
            if (!StringUtils.isBlank(edDt)) {
                // 문자열이 "yyyy-MM-dd HH:mm" 형식인지 확인
                if (!isValidDateFormat(AidtConst.FORMAT_STRING_YMDHM, edDt)) {
                    throw new AidtException("올바른 날짜형식이 아닙니다. \n(ex. 2024-12-31 15:00 과 같은 형식으로 입력해 주세요.)");
                }
            }
            else {

                // 값이 없으므로, 시작일자 +1일 23:59 로 세팅
                SimpleDateFormat dateFormat = new SimpleDateFormat(AidtConst.FORMAT_STRING_YMDHM);
                try {
                    Date date = dateFormat.parse(stDt); // 시작일시

                    // 다음 날의 23:59 값을 설정
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날로 이동
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);

                    // 설정한 값을 Date로 변환
                    Date dt2 = calendar.getTime();

                    // 계산한 마감 일시를 String 으로 다시 변환해서 param map 에 세팅한다.
                    paramData.put("pdEvlEdDt", dateFormat.format(dt2));
                } catch (ParseException e) {
                    log.error(CustomLokiLog.errorLog(e));
                    //TODO no action 이미 사전에 validate 했음.
                }
            }
        }
    }

    private boolean isValidDateFormat(String fmt, String dt) throws Exception {

        // "yyyy-MM-dd HH:mm"
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);

        try {
            Date date = dateFormat.parse(dt);
            return true;
        } catch (ParseException p) {
            log.error(CustomLokiLog.errorLog(p));
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> tchAiCustomLrnCreateEngTargetList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        /* 성취도 영역별 아이디(usd_ach_id) 추출 */
        List<String> cuScrList = aiLearningEngMapper.selectUsdAchId(paramData);

        /* 연결된 meta 정보가 없을 경우 */
        if (cuScrList.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "출제 가능한 문제가 없습니다.");
            return resultMap;
        }
        paramData.put("curriUnit2Val", cuScrList);
        //성취도 존재 여부 및 기출제횟수 조회
        List<Map<String, Object>> stntList = aiLearningEngMapper.findAiCustomLrnCreateEngTargetList(paramData);
        String strStdtId = "";
        String strFlnm = "";
        String lev = "";
        List<Map<String, Object>> targetStntList = new ArrayList<>();
        List<Map<String, Object>> nonTargetStntList = new ArrayList<>();
        Map<String, Object> stntMap = null;

        for (Map<String, Object> m : stntList) {
            strStdtId = MapUtils.getString(m, "stdtId", "");
            strFlnm = MapUtils.getString(m, "flnm", "");
            lev = MapUtils.getString(m, "lev", "");
            stntMap = new HashMap<>();
            stntMap.put("stntId", strStdtId);
            stntMap.put("flmn", strFlnm);
            stntMap.put("lev", lev);

            if (ObjectUtils.isEmpty(MapUtils.getInteger(m, "usdAchScr"))) {
                nonTargetStntList.add(stntMap);
            } else {
                stntMap.put("pastQustCnt", MapUtils.getInteger(m, "cnt", 0));
                targetStntList.add(stntMap);
            }
        }

        resultMap.put("resultOk", true);
        resultMap.put("TargetStntList", targetStntList);
        resultMap.put("NonTargetStntList", nonTargetStntList);

        return resultMap;
    }

    public Map<String, Object> createAiPrscrEvlToTask(Map<String, Object> paramData) throws Exception {
        /* 평가 완료 후 처방 과제로 생성 : task_info의 prscr_std_crt_trget_id(참조한 task_id)에 evl_id가 들어갈 예정 */
        /* 기존 task_id를 참조하던 처방 학습 문제의 경우 오류가 발생할 수 있음 */
        Map<String, Object> returnMap = new HashMap<>();
        long startTime = System.currentTimeMillis();

        // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
        String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때

        log.debug("createAiLearningBatchEvl START-------------------------------");

        int setCount = 0; // 세트지 생성건수
        int idCount = 0; // 대상 평가 id 건수
        int articleCount = 0; // 추출된 총 article 갯수
        int stntCnt = 0; // 대상 학생 수
        int successCnt = 0; // 성공하여 세트지 생성완료된 개수
        int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

        Map<String, Object> evlInfo = aiLearningEngMapper.findTargetWrongEvlList(paramData);

        if (MapUtils.isEmpty(evlInfo)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "evl_id 에 해당하는 Evl 정보가 없습니다.");

            return returnMap;
        }

        int evlId = MapUtils.getIntValue(evlInfo, "id");
        int evlSetCount = 0; // 평가지당 생성된 task 세트지 건수


        Map<String, Object> oldSetsMap = this.getOldSetsMap("eval", evlId); // 참고할 세트지

        if (MapUtils.isEmpty(oldSetsMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "참고할 세트지 정보가 없습니다.");

            String creator = MapUtils.getString(evlInfo, "wrterId", "");

            this.updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);

            return returnMap;
        }

        String creator = String.valueOf(oldSetsMap.get("creator")); // 세트지와 join 한 evl_info 에서 가져온 작성자id

        Map<String, Object> innerParam = new HashMap<>();
        innerParam.put("taskNm", "[처방학습] " + evlInfo.get("evlNm"));
        innerParam.put("lrnMethod", paramData.get("lrnMethod"));
        innerParam.put("eamTrget", paramData.get("eamTrget"));
        innerParam.put("evlId", evlId);
        innerParam.put("brandId", Long.valueOf(MapUtils.getInteger(oldSetsMap, "brand_id", 0)));
        innerParam.put("evlSeCd", MapUtils.getInteger(evlInfo, "evlSeCd", 0));  // 평가구분 : 1: 진단평가, 2: 형성평가, 3: 총괄평가, 4: 수행평가

        innerParam.putAll(evlInfo);
        innerParam.remove("id");
        innerParam.put("pdEvlStDt", evlInfo.get("pdEvlStDt"));      // 시작일 (평가 종료일)
        innerParam.put("pdEvlEdDt", evlInfo.get("pdEvlEdDt"));      // 종료일 (평가 종료일 7일 후)
        innerParam.put("eamMth", 4);                                // 출제방법 : AI 처방 학습

        // 출제할 아티클을 추출
        Map<String, Object> articleMap;

        if (MapUtils.getLong(innerParam, "brandId", 0L) == 1) {
            // 수학의 경우 다른문제 풀기 로직을 이용해 문제 출제
            articleMap = this.findAutoCreateAiLearningEvlWithDiagnostic(innerParam);
        } else {
            articleMap = this.findAutoCreateAiLearningEvl(innerParam);
        }

        log.info("articleMap : {}", articleMap);

        if (MapUtils.isEmpty(articleMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "출제할 문항이 없습니다.");

            this.updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);

            return returnMap;
        }

        List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

        try {
            // 학생의 아티클이 있을때만 task 생성 및 진행
            if (CollectionUtils.isNotEmpty(stntArticleList)) {
                // task_info 생성
                innerParam.put("textbkId", innerParam.get("textbookId"));
                innerParam.put("prscrStdCrtTrgetId", innerParam.get("evlId"));

                long newTaskId = createTaskInfoForAiCustomLearning(innerParam);

                innerParam.put("taskId", newTaskId);

                log.info("newId:{}", newTaskId);

                // 학생별
                for (AiArticleListByStntVO _stntArticleVo : stntArticleList) {

                    String mamoymId = _stntArticleVo.getMamoymId();
                    innerParam.put("stntId", mamoymId);

                    List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                    if (aiArticleVOList.isEmpty()) continue;

                    // 학생이 가진 article 목록으로 세트지 생성
                    String newSetsId = createSetsForAiCustomLearning(aiArticleVOList, oldSetsMap);

                    innerParam.put("setsId", newSetsId);

                    log.info("setsId:{}", newSetsId); // 생성된 setId : null or 0 이 아니면 정상적으로 생성되었다는 뜻

                    // 세트지 생성에 성공했으면 count++
                    if (ObjectUtils.isNotEmpty(newSetsId)) {
                        log.info("Sets Created:{}", newSetsId);
                        setCount++;
                        evlSetCount++;

                        //세트지가 만들어 졌으므로, SetSummary, task_result_info, task_result_detail 정보를 생성한다.
                        log.info("SetSummary 생성-{}", evlId);
                        SetSummarySaveRequestVO setSummarySaveRequestVO = createSetSummary(newSetsId, creator, aiArticleVOList);


                        log.info("task_result_info 생성-{}", evlId);
                        int newTaskResultId;
                        newTaskResultId = createTaskResultInfoForAiCustomLearningEach(innerParam, aiArticleVOList);
                        log.info("newTaskResultId:{}", newTaskResultId);

                        log.info("task_result_detail 생성-{}", evlId);
                        int detailCnt = createTaskResultDetailForAiCustomLearning(innerParam);
                        log.info("task_result_detail.cnt:{}", detailCnt);
                    } else {
                        log.warn("Fail Sets Creation:{}", evlId);
                    }
                    stntCnt++; // 세트지 성공여부에 관계없이 학생수 증가
                    articleCount = +_stntArticleVo.getArticleList().size(); // 세트지 성공여부에 관계없이 아티클수 증가
                }
            }
        } catch (Exception e) {
            log.error("처방학습 출제 중 오류 발생");
            log.error(e.getMessage());

            this.updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);
        }


        if (evlSetCount > 0) {
            prscrStdCrtAt = "Y"; // 뭔가 생성 됐을때. 모든 학생 및 set지를 다 판단 할 수는 없음.
            successCnt++;
        } else {
            failCnt++;
        }

        int updateCnt = this.updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);

        long endTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchEvl END------------------------------- : {}", (endTime - startTime));

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "처방 과제 출제가 완료되었습니다.");
        returnMap.put("duration", endTime - startTime);
        returnMap.put("setCount", setCount);
        returnMap.put("idCount", idCount);
        returnMap.put("articleCount", articleCount);
        returnMap.put("stntCnt", stntCnt);
        returnMap.put("successCnt", successCnt);
        returnMap.put("failCnt", failCnt);

        return returnMap;
    }

    public Map<String, Object> selectAiPrscrEvlToTask(Map<String, Object> paramData) throws Exception {
        /* 평가 완료 후 처방 과제로 생성 : task_info의 prscr_std_crt_trget_id(참조한 task_id)에 evl_id가 들어갈 예정 */
        /* 기존 task_id를 참조하던 처방 학습 문제의 경우 오류가 발생할 수 있음 */
        Map<String, Object> returnMap = new HashMap<>();
        long startTime = System.currentTimeMillis();

        // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
        String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때

        log.debug("createAiLearningBatchEvl START-------------------------------");

        int setCount = 0; // 세트지 생성건수
        int idCount = 0; // 대상 평가 id 건수
        int articleCount = 0; // 추출된 총 article 갯수
        int stntCnt = 0; // 대상 학생 수
        int successCnt = 0; // 성공하여 세트지 생성완료된 개수
        int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

        Map<String, Object> evlInfo = aiLearningEngMapper.findTargetWrongEvlListTest(paramData);

        if (MapUtils.isEmpty(evlInfo)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "evl_id 에 해당하는 Evl 정보가 없습니다.");

            return returnMap;
        } else {
            returnMap.put("evlInfo", evlInfo);
        }

        int evlId = MapUtils.getIntValue(evlInfo, "id");

        Map<String, Object> oldSetsMap = this.getOldSetsMap("eval", evlId); // 참고할 세트지

        if (MapUtils.isEmpty(oldSetsMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "참고할 세트지 정보가 없습니다.");

            return returnMap;
        } else {
            returnMap.put("oldSetsMap", oldSetsMap);
        }


        Map<String, Object> innerParam = new HashMap<>();
        innerParam.put("taskNm", "[처방학습] " + evlInfo.get("evlNm"));
        innerParam.put("lrnMethod", paramData.get("lrnMethod"));
        innerParam.put("eamTrget", paramData.get("eamTrget"));
        innerParam.put("evlId", evlId);
        innerParam.put("brandId", Long.valueOf(MapUtils.getInteger(oldSetsMap, "brand_id", 0)));
        innerParam.put("evlSeCd", MapUtils.getInteger(evlInfo, "evlSeCd", 0));  // 평가구분 : 1: 진단평가, 2: 형성평가, 3: 총괄평가, 4: 수행평가

        innerParam.putAll(evlInfo);
        innerParam.remove("id");
        innerParam.put("pdEvlStDt", evlInfo.get("pdEvlStDt"));      // 시작일 (평가 종료일)
        innerParam.put("pdEvlEdDt", evlInfo.get("pdEvlEdDt"));      // 종료일 (평가 종료일 7일 후)
        innerParam.put("eamMth", 4);                                // 출제방법 : AI 처방 학습

        // 출제할 아티클을 추출
        Map<String, Object> articleMap;

        returnMap.put("innerParam", innerParam);

        if (MapUtils.getLong(innerParam, "brandId", 0L) == 1) {
            // 수학의 경우 다른문제 풀기 로직을 이용해 문제 출제
            returnMap.put("type", "another");
            articleMap = this.findAutoCreateAiLearningEvlWithDiagnostic(innerParam);
        } else {
            returnMap.put("type", "normal");
            articleMap = this.findAutoCreateAiLearningEvl(innerParam);
        }

        if (MapUtils.isEmpty(articleMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "출제할 문항이 없습니다.");

            return returnMap;
        }

        List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

        // 학생의 아티클이 있을때만 task 생성 및 진행
        if (CollectionUtils.isNotEmpty(stntArticleList)) {
            // task_info 생성
            innerParam.put("textbkId", innerParam.get("textbookId"));
            innerParam.put("prscrStdCrtTrgetId", innerParam.get("evlId"));

            // 학생별
            for (AiArticleListByStntVO _stntArticleVo : stntArticleList) {
                String mamoymId = _stntArticleVo.getMamoymId();
                innerParam.put("stntId", mamoymId);

                List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                log.info("aiArticleVOList:{}", aiArticleVOList);

                if (aiArticleVOList.isEmpty()) continue;

                returnMap.put("mamoymId", mamoymId);
                returnMap.put("articleMap", articleMap);
            }
        }

        long endTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchEvl END------------------------------- : {}", (endTime - startTime));

        return returnMap;
    }

    /**
     * @summary article의 미리보기 화면 구성을 위한 정보 세팅
     * @param articleVOList 세팅 articleList
     */
    private Map<String, Object> getPreviewInfo(List<AiArticleVO> articleVOList) {
        Map<String, Object> returnMap = new HashMap<>();

        // 미리보기 정보 세팅
        List<String> articleIdList = articleVOList.stream()
                .map(aiArticleVO -> String.valueOf(aiArticleVO.getArticle_id()))
                .toList();

        returnMap.put("previewInfo", aiLearningMapper.getArticlePreviewInfo(articleIdList));
        returnMap.put("articleList", articleIdList);

        return returnMap;
    }
}
