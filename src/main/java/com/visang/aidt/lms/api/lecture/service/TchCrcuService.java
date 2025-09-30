package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.lecture.mapper.TchCrcuMapper;
import com.visang.aidt.lms.api.lecture.mapper.TchCrcuTabMapper;
import com.visang.aidt.lms.api.materials.mapper.TchLesnRscMapper;
import com.visang.aidt.lms.api.repository.TcCurriculumRepository;
import com.visang.aidt.lms.api.repository.TcLastlessonRepository;
import com.visang.aidt.lms.api.repository.TextbookRepository;
import com.visang.aidt.lms.api.repository.dto.TabInfoDTO;
import com.visang.aidt.lms.api.repository.dto.TcCurriculumDTO;
import com.visang.aidt.lms.api.repository.dto.TcLastlessonDTO;
import com.visang.aidt.lms.api.repository.entity.TcCurriculumEntity;
import com.visang.aidt.lms.api.repository.entity.TcLastlessonEntity;
import com.visang.aidt.lms.api.repository.entity.TextbookEntity;
import com.visang.aidt.lms.api.textbook.service.TextbookService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class TchCrcuService {
    //private final TchCrcuTabService tchCrcuTabService;

    private final TcCurriculumRepository tcCurriculumRepository;

    //private final SetsRepository setsRepository;

    private final TextbookRepository textbookRepository;

    private final TcLastlessonRepository tcLastlessonRepository;

    private final TchCrcuMapper tchCrcuMapper;

    private final TchCrcuTabMapper tchCrcuTabMapper;

    private final TchLesnRscMapper tchLesnRscMapper;

    private final TextbookService textbookService;

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

        TextbookEntity textbook = textbookRepository.findByTextbookIndexId(
                tcCurriculumDTO.getTextbkIdxId()).orElseThrow(() -> new IllegalArgumentException("textbook doesn't exist"));

        // 2024-04-03
        // 해당 교과서 ID값을 web_textbook_id 컬럼에 갖고 있는 ebook 교과서에서 pdf_url값을 구함.
        TextbookEntity ebook = textbookRepository.findTop1ByWebTextbookIdAndIsActiveAndIsDeleted(
                textbook.getId(), true, false).orElse(null);

        // 교과서 기본정보 저장
        rtnMap.put("textbkId", textbook.getId());
        rtnMap.put("textbkName", textbook.getName());
        rtnMap.put("ebkId", ObjectUtils.isEmpty(ebook) ? 0L : ebook.getId());
        rtnMap.put("pdfUrl", ObjectUtils.isEmpty(ebook) ? "" : ebook.getPdfUrl());

        // 마지막 수업위치 조회
        Long lastPosition = 0L;
        TcLastlessonEntity lastlessonEntity
                = tcLastlessonRepository.findByClaIdAndTextbkIdAndTextbkIdxId(
                 tcCurriculumDTO.getClaId(), tcCurriculumDTO.getTextbkId(), tcCurriculumDTO.getTextbkIdxId()).orElse(null);

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

        List<Map<String, Object>> curriList = tchCrcuMapper.selectCurriculumList(paramData);
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
            if (key.equals(lastPosition)) {
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
        String firstCurriUnit1 = null;
        for (Map<String, Object> map : curriList) {
            // order culumn 추가
            map.put("order", no++);
            // 정렬을 위해 추가했던 key 제거
            map.remove("curriCd");
            map.remove("curriOrder");
            TcCurriculumDTO curriDto = TcCurriculumDTO.mapToDto(map);
            curriDtoList.add(curriDto);
            if (firstCurriUnit1 != null) {
                continue;
            }
            firstCurriUnit1 = MapUtils.getString(map, "curriUnit1");
        }
        rtnMap.put("firstCurriUnit1", firstCurriUnit1);

        // 충돌 확인 필요(2025.03.06)
        /*String textbkIdValue = (String) paramData.get("textbkId");
        paramData.put("textbookId", textbkIdValue);

        // 교과서 커리큘럼 학습맵 조회
        List<Map<String, Object>> resultData = textbookService.getTextbookCrcuListByMeta(paramData);*/

        rtnMap.put("curriculumList", curriDtoList);
        // 충돌 확인 필요(2025.03.06)
        /*rtnMap.put("curriculumStudyMapList", resultData);*/

        /* 게임 관련 추가 파라미터는 school, subject, grade만 전달 - leejh16 SOP-294 */
        // 게임에 필요한 커리큘럼 학습맵 key
        Map<String, Object> gameStudyMapId;

        // 게임에 필요한 현재 교과서의 레벨 정보 전달
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("textbkId", textbook.getId());
        List<Map<String, Object>> levelMetaList = tchCrcuMapper.getLevelMetaListFromTextbook(paramMap);
        if (CollectionUtils.isNotEmpty(levelMetaList)) {
            String textbookName = null;
            String curriSchool = null;
            String curriSubject = null;
            String curriGrade = null;
            /*String curriSemester = null;
            String gamelevel = null;*/
            for (Map<String, Object> map : levelMetaList) {
                if (textbookName == null) {
                    textbookName = MapUtils.getString(map, "textbookName");
                }
                String metaCode = MapUtils.getString(map, "code");
                String metaName = MapUtils.getString(map, "name");
                if (StringUtils.equalsIgnoreCase(metaName, "curriSchool")) {
                    curriSchool = metaCode;
                } else if (StringUtils.equalsIgnoreCase(metaName, "curriSubject")) {
                    if (StringUtils.equalsIgnoreCase(metaCode, "english")) {
                        curriSubject = "engl";
                    } else if (StringUtils.equalsIgnoreCase(metaCode, "mathematics")) {
                        curriSubject = "math";
                    } else {
                        curriSubject = metaCode;
                    }
                } else if (StringUtils.equalsIgnoreCase(metaName, "curriGrade")) {
                    curriGrade = metaCode;
                }
                /*else if (StringUtils.equalsIgnoreCase(metaName, "curriSemester")) {
                    curriSemester = metaCode;
                }*/
            }

            /*// 중등일 경우
            if (StringUtils.equalsIgnoreCase(curriSchool, "middle")) {
                gamelevel = "grade01";
            }
            // 고등일 경우
            else {
                // [22개정] 공통영어1, [22개정] 공통수학1 같이 1로 끝나면
                if (StringUtils.endsWith(textbookName, "1")) {
                    gamelevel = "common1";
                }
                // [22개정] 공통영어2, [22개정] 공통수학2 같이 2로 끝나면
                else if (StringUtils.endsWith(textbookName, "2")) {
                    gamelevel = "common2";
                }
            }*/

            // 게임에서 필요한 school, subject, grade 이 세개만 전달 하도록 함
            rtnMap.put("school", curriSchool);
            rtnMap.put("subject", curriSubject);
            rtnMap.put("grade", curriGrade);
            /*rtnMap.put("semester", curriSemester);
            rtnMap.put("gamelevel", gamelevel);*/
        }

        /*gameStudyMapId는 필요 없음*/
        /*if (!ObjectUtils.isEmpty(lastlessonEntity)) {


            // 기존 학습맵 로직 - 학습 이력이 있을 경우 : 마지막 학습의 studyMap1, studyMap2 값 추출하여 세팅
            //gameStudyMapId = tchCrcuMapper.getLastLessonCurriculum(paramData);


            // 이정훈 수정 학습맵 로직
            // 학습맵 없을 경우 상위 학습맵 탐색 - 비상교육 이정훈
            paramData.put("lastLessonId", lastlessonEntity.getId());
            paramData.put("lastLessonTextbookIndexId", lastlessonEntity.getTextbkIdxId());
            gameStudyMapId = tchCrcuMapper.getLastLessonCurriculum2(paramData);


            if (MapUtils.isEmpty(gameStudyMapId)) gameStudyMapId = tchCrcuMapper.getLastLessonCurriculumMap1(paramData);
        } else {
            // 학습 이력이 없을 경우 : 제일 처음 학습의 studyMap1, studyMap2 값 추출하여 세팅
            gameStudyMapId = tchCrcuMapper.getFirstCurriculum(paramData);
        }

        if (MapUtils.isEmpty(gameStudyMapId)) gameStudyMapId = Map.of("metaId", 0);

        rtnMap.put("gameStudyMap", gameStudyMapId.get("metaId"));*/

        return rtnMap;
    }

    /**
     * (커리큘럼).커리큘럼 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public Map<String, Object> findCrcuList(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();

        TcCurriculumDTO tcCurriculumDTO = TcCurriculumDTO.mapToDto(paramData);

        TextbookEntity textbook = textbookRepository.findByTextbookIndexId(
                tcCurriculumDTO.getTextbkIdxId()).orElseThrow(() -> new IllegalArgumentException("textbook doesn't exist"));

        // 교과서 기본정보 저장
        rtnMap.put("textbkId", textbook.getId());
        rtnMap.put("textbkName", textbook.getName());
        rtnMap.put("pdfUrl", textbook.getPdfUrl());

        // 마지막 수업위치 조회
        Long lastPosition = 0L;
        TcLastlessonEntity lastlessonEntity
                = tcLastlessonRepository.findByClaIdAndTextbkIdAndTextbkIdxId(
                        tcCurriculumDTO.getClaId(),tcCurriculumDTO.getTextbkId(),tcCurriculumDTO.getTextbkIdxId()).orElse(null);

        if(!ObjectUtils.isEmpty(lastlessonEntity)) {
            lastPosition = lastlessonEntity.getCrculId();
        }
        AtomicLong lastPositionCurriId = new AtomicLong(lastPosition);

        List<TcCurriculumDTO> curriDtoList = new ArrayList<>();

        List<Map<String,Object>> curriList = tchCrcuMapper.findCrcuList(paramData);
        CollectionUtils.emptyIfNull(curriList)
                .stream()
                .forEach(curriculumInfo -> {
                    TcCurriculumDTO curriDto = TcCurriculumDTO.mapToDto(curriculumInfo);
                    // 마지막 수업위치와 동일하면 true
                    if(curriDto.getKey() == lastPositionCurriId.get()) {
                        curriDto.setLastPosition(true);
                    }

                    curriDtoList.add(curriDto);
                });

        rtnMap.put("curriculumList", curriDtoList);

        return rtnMap;
    }

    /**
     * (커리큘럼).커리큘럼 차시별 질문 개수 및 질문 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    /*
    public Map<String, Object> findCrcuQuestList(Map<String, Object> paramData) {
        return new JSONObject("""
            { "list": [
                {"id": 1, "item": "dummy-1"},
                {"id": 2, "item": "dummy-2"},
                {"id": 3, "item": "dummy-3"},
            ]}
        """).toMap();
    }*/

    /**
     * (차시).마지막 수업한 차시정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findCrcuLastPosition(Map<String, Object> paramData) throws Exception {
        TcLastlessonDTO lastlessonDTO = TcLastlessonDTO.mapToDto(paramData);

        TcLastlessonEntity lastlessonEntity
                = tcLastlessonRepository.findByClaIdAndTextbkIdAndTextbkIdxId(
                        lastlessonDTO.getClaId(),lastlessonDTO.getTextbkId(),lastlessonDTO.getTextbkIdxId()).orElse(null);

        if(ObjectUtils.isEmpty(lastlessonEntity)) {
            return new HashMap<>();
        }

        return TcLastlessonDTO.entityToMap(lastlessonEntity);
    }

    /**
     * (차시).마지막 수업한 차시정보 기록(수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(transactionManager = "transactionManager")
    public Map<String, Object> modifyCrcuLastPosition(Map<String, Object> paramData) throws Exception {
        TcLastlessonDTO tcLastlessonDTO = TcLastlessonDTO.mapToDto(paramData);

        TcLastlessonEntity entity = TcLastlessonDTO.mapToEntity(paramData);
        entity.setRgtr(entity.getUserId());
        entity.setMdfr(entity.getUserId());
        entity.setRegDt(new Date());
        entity.setMdfyDt(new Date());

        // 데이터 존재 유무
        TcLastlessonEntity existEntity = tcLastlessonRepository.findByClaIdAndTextbkIdAndTextbkIdxId(
                entity.getClaId(),entity.getTextbkId(),entity.getTextbkIdxId()).orElse(null);

        if(!ObjectUtils.isEmpty(existEntity)) {
            entity.setId(existEntity.getId());
        }
        // 등록,수정
        entity = tcLastlessonRepository.save(entity);

        //tc_lastlesson_crcul
        int modifyCnt = tchCrcuTabMapper.modifyTcLastlessonCrcul(paramData);
        if (modifyCnt == 0) {
            tchCrcuTabMapper.createTcLastlessonCrcul(paramData);
        }

        return TcLastlessonDTO.entityToMap(entity);
    }

    /**
     * (차시).차시 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findCrcuInfo(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();

        // 파라미터 목록
        String[] requiredParams = {"userId", "textbkId", "textbkIdxId", "claId", "crculId"};

        // 파라미터 유효성 검사
        for (String paramName : requiredParams) {
            Map<String, Object> validation = validateParam(paramData, paramName);
            if (validation != null) {
                return validation;
            }
        }

        TcCurriculumDTO tcCurriculumDTO = TcCurriculumDTO.mapToDto(paramData);
        Optional<TcCurriculumEntity> curriculumOptional = tcCurriculumRepository
            .findAllByClaIdAndTextbkIdAndTextbkIdxIdAndKey(tcCurriculumDTO.getClaId(), tcCurriculumDTO.getTextbkId(), tcCurriculumDTO.getTextbkIdxId(), tcCurriculumDTO.getCrculId());

        // 조회 건 없을 때 반환
        if (curriculumOptional.isEmpty()) {
            rtnMap.put("message", "해당 커리큘럼을 찾을 수 없습니다.");
            return rtnMap;
        }

        TcCurriculumEntity textbookCurriculum = curriculumOptional.get();

        //AtomicBoolean isFirst = new AtomicBoolean(true);
        //Map<String, Object> searchParam = new HashMap<>();

        // 해당 교과서 ID값을 web_textbook_id 컬럼에 갖고 있는 ebook 교과서에서 pdf_url값을 구함.
        TextbookEntity ebook = textbookRepository.findTop1ByWebTextbookIdAndIsActiveAndIsDeleted(
                MapUtils.getLong(paramData, "textbkId"), true, false).orElse(null);

        // 탭 목록 조회
        List<Map<String,Object>> tabInfoList = tchCrcuTabMapper.findCrcuTabList(paramData);
        List<Object> setsIdList = new ArrayList<>();

        TcCurriculumDTO textbookCurriculumDTO = TcCurriculumDTO.toDTO(textbookCurriculum);
        textbookCurriculumDTO.setEbkId(ObjectUtils.isEmpty(ebook) ? 0L : ebook.getId());
        textbookCurriculumDTO.setPdfUrl(ObjectUtils.isEmpty(ebook) ? "" : ebook.getPdfUrl());
        textbookCurriculumDTO.setTextbookTabList(
            CollectionUtils.emptyIfNull(tabInfoList)
                    .stream()
                    .peek(tabInfo -> {
                        if(tabInfo.get("setsId") != null) {
                            setsIdList.add(tabInfo.get("setsId"));
                        }
                    })
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

        // UI/UX 개선건 (2024-11-08 추가)
        // 셋트지별 모듈유형별 갯수 정보 설정
        if(!setsIdList.isEmpty()) {
            List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);
            // 모듈유형정보
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            CollectionUtils.emptyIfNull(textbookCurriculumDTO.getTextbookTabList())
                .stream()
                .filter(tabInfo -> StringUtils.isNotBlank(tabInfo.getSetsId()))
                .forEach(tabInfo -> {
                    // 모듈유형정보
                    tabInfo.setArticleTypeList(
                        CollectionUtils.emptyIfNull(articleTypeList)
                            .stream()
                            .filter(r -> StringUtils.equals(tabInfo.getSetsId(),MapUtils.getString(r,"setsId")))
                            .map(r -> {
                                return AidtCommonUtil.filterToMap(articleTypeItem, r);
                            }).toList()
                    );
                });
        }


        // 현재 활동하기 위치 찾는 부분
        List<Map> activeInfoList = tchCrcuMapper.selectActiveInfoList(paramData);

        String activeInfoYn = activeInfoList.size() > 0 ? "Y" : "N";

        rtnMap.put("curriculumInfo", textbookCurriculumDTO);
        rtnMap.put("activeInfoYn", activeInfoYn);
        rtnMap.put("activeInfoList", activeInfoList);

        return rtnMap;
    }

    /**
     * (차시).탭별 모듈 목록 조회 or (차시).탭의 모듈 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findCrcuTabMdulList(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "list": [
                {"id": 1, "item": "dummy-1"},
                {"id": 2, "item": "dummy-2"},
                {"id": 3, "item": "dummy-3"},
            ]}
        """).toMap();
    }

    /**
     * (차시).다음 차시 key값 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findCrcuNextInfo(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (모드).설정된 모드 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findCrcuMode(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (모드).모드 설정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> modityCrcuMode(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    public Object createTchCrcuClassifyReg(Map<String, Object> paramData) throws Exception {
         var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuMapper.createTchCrcuClassifyReg(paramData);

        if (result1 > 0) {
            log.info("커리큘럼 ID : {}", MapUtils.getInteger(paramData, "currId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("curriculumInfo", tchCrcuMapper.findCurriculumInfo(paramData));
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
            returnMap.put("curriculumInfo", "");
        }
        return returnMap;
    }

    public Object modifyTchCrcuClassifyMod(Map<String, Object> paramData) throws Exception {

        int result1 = tchCrcuMapper.modifyTchCrcuClassifyMod(paramData);
        var returnMap = new LinkedHashMap<>();

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("curriculumInfo", tchCrcuMapper.findCurriculumInfo(paramData));
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
            returnMap.put("curriculumInfo", "");
        }
        return returnMap;
    }

    public Object deleteTchCrcuClassifyDel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        try {
            int result1 = tchCrcuMapper.deleteTchCrcuClassifyDel(paramData);
            log.info("result1:{}", result1);

        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
            return returnMap;
        }
        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "성공");
        return returnMap;
    }

    /**
     * 교과과정 커리큘럼 ID에 대한 교사 커리큘럼 ID 조회
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object getTchRedirectCrcuInfo(Map<String, Object> paramData) throws Exception {
        return tchCrcuMapper.findTchRedirectCrcuInfo(paramData);
    }

    /*
    * 파라미터 key, value 검사
    * */
    private Map<String, Object> validateParam(Map<String, Object> paramData, String paramName) {
        Map<String, Object> errorMap = new HashMap<>();

        if (!paramData.containsKey(paramName)) {
            errorMap.put("message", paramName + " 키값이 누락되었습니다.");
            return errorMap;
        }

        if (paramData.get(paramName) == null || paramData.get(paramName).toString().trim().isEmpty()) {
            errorMap.put("message", paramName + " 값이 비어있습니다.");
            return errorMap;
        }

        return null;
    }
}
