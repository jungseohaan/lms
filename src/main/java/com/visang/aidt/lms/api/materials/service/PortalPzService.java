package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.assessment.service.TchEvalService;
import com.visang.aidt.lms.api.homework.service.TchHomewkService;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.materials.mapper.PortalPzMapper;
import com.visang.aidt.lms.api.repository.UserRepository;
import com.visang.aidt.lms.api.repository.entity.User;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalPzService {

    private final PortalPzMapper portalPzMapper;

    private final UserRepository userRepository;

    @Value("${app.lcmsapi.url}")
    public String appLcmsapiUrl;
    @Value("${app.lcmsapi.task-evl-search-path}")
    public String appLcmsapiTaskEvlSearchPath;
    @Value("${app.lcmsapi.deployServerCode}")
    public String deployServerCode;
    @Value("${app.lcmsapi.deployServerCodeMulti}")
    public String deployServerCodeMulti;/*민간존 운영(비바샘)의 경우에는 컨텐츠 배포를 여러 서버 다중 배포가 일어남*/
    private final AidtWebClientSender aidtWebClientSender;
    private final TchEvalService tchEvalService;
    private final TchHomewkService tchHomewkService;

    @Transactional(readOnly = true)
    public Map<String, Object> getClassInfo(Map<String, Object> paramData) throws Exception{
        return portalPzMapper.getClassInfo(paramData);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getClassInfoByClassCode(Map<String, Object> paramData) throws Exception{
        return portalPzMapper.getClassInfoByClassCode(paramData);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getClassInfoByLectureCode(Map<String, Object> paramData) throws Exception{
        return portalPzMapper.getClassInfoByLectureCode(paramData);
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> findLcmsTextbookList(Map<String, Object> data) throws Exception{
        return portalPzMapper.findLcmsTextbookList(data);
    }

    @Transactional
    public Object tcTextbookList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map<String, Object>> textbookList = new ArrayList<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");
        returnMap.put("textbookList", textbookList);

        String claId = (String) paramData.getOrDefault("claId", "");
        String userId = (String) paramData.getOrDefault("userId", "");
        String partnerId = (String) paramData.getOrDefault("partnerId", "");

        Map<String, Object> data = new HashMap<>();
        data.put("wrterId", userId);
        data.put("claId", claId);
        data.put("partnerId", partnerId);
        // 선생님 교과서 선택 여부
        String areadyTextbookYn = "N";

        //사용자정보 조회
        User user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.T.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        //공공기관 파트너 정보 조회
        Map<String, Object> ptnInfo = portalPzMapper.getPtnInfo(data);
        if (ptnInfo == null) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - PtnInfo No exists");
            return returnMap;
        }
        //교과과정 세팅
        data.put("curriSchool", ptnInfo.getOrDefault("curriSchool", ""));
        data.put("curriGrade", ptnInfo.getOrDefault("curriGrade", ""));
        data.put("curriSubject", ptnInfo.getOrDefault("curriSubject", ""));
        data.put("curriSemester", ptnInfo.getOrDefault("curriSemester", ""));
        data.put("deployServerCode", deployServerCode);

        // 세팅된 교과서 조회
        Map<String, Object> tcTextbookInfo = this.getTcTextbookInfo(data);
        if (tcTextbookInfo == null) {

            /*한 서버에 여러 컨텐츠 배포하는 경우*/
            boolean isMultiServer = false;
            if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
                List<String> deployServerCodeList = new ArrayList<>();
                for (String serverCode : deployServerCodeMulti.split(",")) {
                    serverCode = serverCode.replaceAll("\\s+", "");
                    if (StringUtils.isEmpty(serverCode)) {
                        continue;
                    }
                    deployServerCodeList.add(serverCode);
                }
                /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                    향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
                if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                    data.put("deployServerCodeList", deployServerCodeList);
                    isMultiServer = true;
                }
            }

            List<Map<String, Object>> cmsTextbookList = portalPzMapper.findLcmsTextbookList(data);
            if (isMultiServer) {
                data.remove("deployServerCodeList");
            }
            //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
            if (cmsTextbookList.size() == 1) {
                try {
                    Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);
                    data.put("version", cmsTextbookInfo.get("version"));
                    this.createTextBook(cmsTextbookInfo, data);

                    Map<String, Object> textBook = new HashMap<>();
                    textBook.put("textbkId", cmsTextbookInfo.getOrDefault("textbkId", -1));
                    textBook.put("textbkIdxId", cmsTextbookInfo.getOrDefault("textbkIdxId", -1));
                    textBook.put("textbkCrltnId", cmsTextbookInfo.getOrDefault("textbkCrltnId", -1));
                    textBook.put("textbkNm", cmsTextbookInfo.getOrDefault("textbkNm", ""));

                    //Thumnail 이미지 현재는 CMS 에서 세팅되고 있지 않아서, 추후 CMS 개발 완료 시 변경 필요.
                    setTestThumbnail(textBook);

                    textbookList.add(textBook);
                    returnMap.put("textbookList", textbookList);
                    areadyTextbookYn = "Y";
                    paramData.put("textbkId", textBook.get("textbkId"));
                    paramData.put("saveTcTaskEvl", true);
                } catch (IndexOutOfBoundsException e) {
                    log.error("saveTcTextbook - CMS textbook list is empty: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - No textbook data found in CMS");
                    return returnMap;
                } catch (NullPointerException e) {
                    log.error("saveTcTextbook - Null pointer error in textbook creation: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Missing required data for textbook creation");
                    return returnMap;
                } catch (IllegalArgumentException e) {
                    log.error("saveTcTextbook - Invalid argument error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Invalid parameters for textbook creation");
                    return returnMap;
                } catch (DataAccessException e) {
                    log.error("saveTcTextbook - Database access error in createTextBook: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Database operation failed during textbook creation");
                    return returnMap;
                } catch (SQLException e) {
                    log.error("saveTcTextbook - SQL error in createTextBook: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Database query failed during textbook creation");
                    return returnMap;
                } catch (UnsupportedOperationException e) {
                    log.error("saveTcTextbook - Unsupported operation error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Cannot modify parameter data");
                    return returnMap;
                } catch (ClassCastException e) {
                    log.error("saveTcTextbook - Type casting error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Invalid data type in textbook information");
                    return returnMap;
                } catch (Exception e) {
                    log.error("saveTcTextbook - Unexpected error in textbook creation: {}", CustomLokiLog.errorLog(e));
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Unexpected error occurred during textbook creation");
                    return returnMap;
                }
            } else {
                Map<Integer, Boolean> textbookCheckMap = new HashMap<>();
                for (Map<String, Object> cmsTextbook : cmsTextbookList) {
                    int textbkId = MapUtils.getInteger(cmsTextbook, "textbkId", -1);
                    /*한 서버에 여러 컨텐츠 배포하는 경우*/
                    if (isMultiServer) {
                        // 교과서ID 가 -1 일 경우 쓰레기 데이터로 continue
                        if (textbkId == -1) {
                            continue;
                        }
                        // 비바샘 운영과 같은 경우 한 서버에 다중 배포가 일어나기 때문에 같은 교과서가 겹칠 우려가 있어 방어코딩
                        Boolean isExists = MapUtils.getBoolean(textbookCheckMap, textbkId, false);
                        if (isExists) {
                            continue;
                        }
                        textbookCheckMap.put(textbkId, true);
                    }
                    Map<String, Object> textBook = new HashMap<>();
                    Map<String, Object> previewThumbnail = new HashMap<>();
                    textBook.put("textbkId", textbkId);
                    textBook.put("textbkIdxId", cmsTextbook.getOrDefault("textbkIdxId", -1));
                    textBook.put("textbkCrltnId", cmsTextbook.getOrDefault("textbkCrltnId", -1));
                    textBook.put("textbkNm", cmsTextbook.getOrDefault("textbkNm", ""));
                    //Thumnail 이미지 현재는 CMS 에서 세팅되고 있지 않아서, 추후 CMS 개발 완료 시 변경 필요.
                    setTestThumbnail(textBook);
                    textbookList.add(textBook);
                }
                returnMap.put("textbookList", textbookList);
            }
        }
        // tc textbook 데이터가 있는 경우
        else {

            // 교과서 version up에 따른 교사 데이터 추가 세팅 (테스트 완료 이후 주석 해제)
            //saveCurriAndTabFromVersionUp(tcTextbookInfo);
            // 교과서 데이터가 null이 아닐 경우 선택한 상황
            areadyTextbookYn = "Y";

            Map<String, Object> textBook = new HashMap<>();
            textBook.put("textbkId", tcTextbookInfo.getOrDefault("textbkId", -1));
            paramData.put("textbkId", textBook.get("textbkId"));
            paramData.put("saveTcTaskEvl", true);
            //this.saveTcTaskEvl(paramData); // transaction 바깥에서 실행 - 이미 선택했을 경우 default 과제/평가 세트지를 출제한다 (이미 출제된 세트지는 pass 하는 로직 있음)

            textBook.put("textbkIdxId", tcTextbookInfo.getOrDefault("textbkIdxId", -1));
            textBook.put("textbkCrltnId", tcTextbookInfo.getOrDefault("textbkCrltnId", -1));
            textBook.put("textbkNm", tcTextbookInfo.getOrDefault("textbkNm", ""));

            //Thumnail 이미지 현재는 CMS 에서 세팅되고 있지 않아서, 추후 CMS 개발 완료 시 변경 필요.
            setTestThumbnail(textBook);

            textbookList.add(textBook);
        }

        returnMap.put("areadyTextbookYn", areadyTextbookYn);
        returnMap.put("textbookList", textbookList);
        return returnMap;
    }

    /**
     * 버젼 비교를 통해 추가된 목차, 탭 정보 등록처리
     * 사이드 이펙트 우려로 특정 계정 한정 하드코딩 테스트 진행 필요
     *
     * @param tcTextbookInfo : PortalPzMapper.xml getTcTextbookInfo return 값
     * @return
     * @throws Exception
     */
    public Object saveCurriAndTabFromVersionUp(Map<String, Object> tcTextbookInfo) throws Exception {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        String emptyProccessYn = MapUtils.getString(tcTextbookInfo, "emptyProccessYn", "N");
        String wrterId = MapUtils.getString(tcTextbookInfo, "wrterId");
        String claId = MapUtils.getString(tcTextbookInfo, "claId");
        String smteCd = MapUtils.getString(tcTextbookInfo, "smteCd");
        int textbkId = MapUtils.getInteger(tcTextbookInfo, "textbkId", 0);
        int textbkIdxId = MapUtils.getInteger(tcTextbookInfo, "textbkIdxId", 0);

        Map<String, Object> versionParamMap = new HashMap<>();
        versionParamMap.put("wrterId", wrterId);
        versionParamMap.put("claId", claId);
        versionParamMap.put("smteCd", smteCd);
        versionParamMap.put("textbkId", textbkId);
        versionParamMap.put("textbkIdxId", textbkIdxId);

        versionParamMap.put("tcTextbookId", tcTextbookInfo.get("id"));
        int currentVersion = portalPzMapper.selectCurrentTextbookVersion(versionParamMap);
        int version = MapUtils.getInteger(tcTextbookInfo, "version", 0);
        // 현재 버젼 정보가 존재하며 배포된 버젼이 현재 버젼과 동일하거나 낮을 경우는 return
        if (currentVersion > 0 && currentVersion >= version) {
            resultMap.put("resultMsg", "현재 버젼 정보가 존재하며 배포된 버젼이 현재 버젼과 동일하거나 낮습니다");
            return resultMap;
        }

        // 배포되어 있는 목차, 탭 정보 조회 시 조건 처리 (버젼이 없는 최초의 경우에는 순수 차집합 연산)
        versionParamMap.put("currentVersion", currentVersion);

        Map<String, Object> textbookindexMap = portalPzMapper.selectTextbookIndex(versionParamMap);
        if (MapUtils.isEmpty(textbookindexMap)) {
            resultMap.put("resultMsg", "목차 정보가 없습니다.");
            return resultMap;
        }
        String textbkIdxNm = MapUtils.getString(textbookindexMap, "textbkIdxNm");
        String brandId = MapUtils.getString(textbookindexMap, "brandId");

        /*  현재 버젼 정보가 없을 경우 최초 차집합 추출하여 버젼 차이나는 만큼 등록 처리 한다
            차집합 처리 후 현재 버젼 정보 tc_textbook_version 테이블에 등록 */
        List<Map<String, Object>> tcCurriculumList = portalPzMapper.selectTcCurriculum(versionParamMap);
        // 비바샘의 경우에는 교사 커리큘럼이 아에 없을 수도 있음 (emptyProccessYn 파라미터 처리)
        if (StringUtils.equals(emptyProccessYn, "N") && CollectionUtils.isEmpty(tcCurriculumList)) {
            resultMap.put("resultMsg", "커리큘럼 정보가 없습니다.");
            return resultMap;
        }

        // 현재 교사가 선택한 커리큘럼 객체 세팅
        Map<Integer, Map<String, Object>> tcCurriculumMap = new HashMap<>();
        for (Map<String, Object> map : tcCurriculumList) {
            int key = MapUtils.getInteger(map, "key", 0);
            if (key == 0) {
                continue;
            }
            tcCurriculumMap.put(key, map);
        }
        // 버젼 정보 있을 때와 없을 때에 따라 조건처리 됨
        List<Map<String, Object>> textbookCurriculumList = portalPzMapper.selectTextbookCurriculum(versionParamMap);
        if (CollectionUtils.isEmpty(textbookCurriculumList)) {
            resultMap.put("resultMsg", "커리큘럼 정보가 없습니다.");
            return resultMap;
        }

        // 목차 추가 건
        List<Map<String, Object>> insertCurriList = new ArrayList<>();
        // 정렬 또는 커리큘럼 명 또는 목차명 변경 건
        List<Map<String, Object>> updateCurriList = new ArrayList<>();
        // insert된 목차 제외 탐색할 탭 커리큘럼 key list - selectTextbookTab에 keyList not in 조건 처리
        List<Integer> checkTabTargetKeyList = new ArrayList<>();
        // 배포된 교과서 기준으로 loop 돌며 차집합 추출
        for (Map<String, Object> map : textbookCurriculumList) {
            int key = MapUtils.getInteger(map, "key", 0);
            if (key == 0) {
                continue;
            }
            Map<String, Object> rowMap = tcCurriculumMap.get(key);
            // 현재 row에 없는 경우 insert
            if (rowMap == null) {
                insertCurriList.add(map);
                checkTabTargetKeyList.add(key);
            } else {// 현재 row에 있는 경우 update
                int order = MapUtils.getInteger(map, "order", 0);
                int currentOrder = MapUtils.getInteger(rowMap, "order", 0);
                String text = MapUtils.getString(map, "text");
                String currentText = MapUtils.getString(rowMap, "text");
                String currentTextbkIdxNm = MapUtils.getString(rowMap, "textbkIdxNm");
                // 정렬 또는 커리큘럼 명 또는 목차명이 다를 경우 업데이트
                if ((order > 0 && order != currentOrder)
                        || StringUtils.equals(text, currentText) == false
                        || StringUtils.equals(textbkIdxNm, currentTextbkIdxNm) == false) {
                    updateCurriList.add(map);
                }
            }
        }

        // insert curri 로직 수행
        if (CollectionUtils.isNotEmpty(insertCurriList)) {
            for (Map<String, Object> map : insertCurriList) {
                map.put("wrterId", wrterId);
                map.put("claId", claId);
                map.put("smteCd", smteCd);
                map.put("textbkId", textbkId);
                map.put("textbkIdxId", textbkIdxId);
                map.put("textbkIdxNm", textbkIdxNm);
                map.put("brandId", brandId);
                portalPzMapper.insertTcCurriculumFromVersionCheck(map);
                int tcCurriculumId = MapUtils.getInteger(map, "tcCurriculumId", 0);
                // 정상적으로 insert 되지 않았을 경우 continue
                if (tcCurriculumId == 0) {
                    continue;
                }
                // 신규 목차의 탭은 무조건 insert
                versionParamMap.put("key", map.get("key"));
                List<Map<String, Object>> textbookTabList = portalPzMapper.selectTextbookTab(versionParamMap);
                versionParamMap.remove("key");
                if (CollectionUtils.isEmpty(textbookTabList)) {
                    continue;
                }
                for (Map<String, Object> tabMap : textbookTabList) {
                    tabMap.put("wrterId", wrterId);
                    tabMap.put("claId", claId);
                    tabMap.put("smteCd", smteCd);
                    tabMap.put("textbkId", textbkId);
                    portalPzMapper.insertTcTabInfoFromVersionCheck(tabMap);
                }
            }
        }
        // update curri 로직 수행
        if (CollectionUtils.isNotEmpty(updateCurriList)) {
            for (Map<String, Object> map : updateCurriList) {
                map.put("wrterId", wrterId);
                map.put("claId", claId);
                map.put("smteCd", smteCd);
                map.put("textbkId", textbkId);
                map.put("textbkIdxId", textbkIdxId);
                map.put("textbkIdxNm", textbkIdxNm);
                portalPzMapper.updateTcCurriculum(map);
            }
        }

        versionParamMap.put("version", version); // 갱신된 버젼 처리

        /*  현재 버젼 정보가 없을 경우 최초 차집합 추출하여 버젼 차이나는 만큼 등록 처리 한다
            차집합 처리 후 현재 버젼 정보 tc_textbook_version 테이블에 등록 */
        List<Map<String, Object>> tcTabInfoList = portalPzMapper.selectTcTabInfo(versionParamMap);
        // 비바샘의 경우에는 아에 없을 수도 있음 (emptyProccessYn 파라미터 처리)
        if (StringUtils.equals(emptyProccessYn, "N") && CollectionUtils.isEmpty(tcTabInfoList)) {
            // 버젼 정보 등록 - 커리큘럼 등록 이후 탭 없을 시 버젼 등록 후 return
            portalPzMapper.insertTcTextbookVersion(versionParamMap);
            resultMap.put("resultMsg", "탭 정보가 없습니다 - tcTabInfoList empty");
            return resultMap;
        }

        // 현재 교사가 선택한 탭 객체 세팅
        Map<String, Map<String, Object>> tcTabInfoMap = new HashMap<>();
        for (Map<String, Object> map : tcTabInfoList) {
            String crcul_id = MapUtils.getString(map, "crcul_id");
            String tab_nm = MapUtils.getString(map, "tab_nm");
            if (StringUtils.isEmpty(crcul_id) || StringUtils.isEmpty(tab_nm)) {
                continue;
            }
            // 컬럼 정보는 없지만 차후 업데이트 필요 시 확장성 고려 map 세팅
            tcTabInfoMap.put(crcul_id + "_" + tab_nm, map);
        }

        // 목차 insert 경우는 무조건 모든 tab insert이므로 제외함 (탭이 변경되어도 목차 버젼은 그대로)
        versionParamMap.put("keyList", checkTabTargetKeyList);
        // 버젼 정보 있을 때와 없을 때에 따라 조건처리 됨
        List<Map<String, Object>> textbookTabList = portalPzMapper.selectTextbookTab(versionParamMap);
        // 배포된 탭을 못가져 왔을 경우 이후 로직 수행 없이 return
        if (CollectionUtils.isEmpty(textbookTabList)) {
            // 버젼 정보 등록 - 커리큘럼 등록 이후 탭 없을 시 버젼 등록 후 return
            portalPzMapper.insertTcTextbookVersion(versionParamMap);
            resultMap.put("resultMsg", "탭 정보가 없습니다 - textbookTabList empty");
            return resultMap;
        }

        // 탭 추가 건
        List<Map<String, Object>> insertTabList = new ArrayList<>();
        // 배포된 교과서 기준으로 loop 돌며 차집합 추출
        for (Map<String, Object> map : textbookTabList) {
            String crcul_id = MapUtils.getString(map, "crcul_id");
            String tab_nm = MapUtils.getString(map, "tab_nm");
            if (StringUtils.isEmpty(crcul_id) || StringUtils.isEmpty(tab_nm)) {
                continue;
            }
            Map<String, Object> rowMap = tcTabInfoMap.get(crcul_id + "_" + tab_nm);
            // 현재 row에 없는 경우 insert
            if (rowMap == null) {
                insertTabList.add(map);
            } else {// 현재 row에 있는 경우 update
                // tab은 현재 정책상 update는 없음
            }
        }
        // insert 대상 tab이 없을 경우 이후 로직 수행 없이 return
        if (CollectionUtils.isEmpty(insertTabList)) {
            // 버젼 정보 등록 - 커리큘럼 등록 이후 탭 없을 시 버젼 등록 후 return
            portalPzMapper.insertTcTextbookVersion(versionParamMap);
            resultMap.put("resultMsg", "탭 정보가 없습니다 - insertTabList empty");
            return resultMap;
        }
        // 탭 차집합 insert
        for (Map<String, Object> map : insertTabList) {
            map.put("wrterId", wrterId);
            map.put("claId", claId);
            map.put("smteCd", smteCd);
            map.put("textbkId", textbkId);
            portalPzMapper.insertTcTabInfoFromVersionCheck(map);
        }
        resultMap.put("resultMsg", "등록 완료.");

        // 버젼 정보 등록
        portalPzMapper.insertTcTextbookVersion(versionParamMap);

        return resultMap;
    }

    @Transactional
    public Object saveTcTextbook(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");

        String userId = (String) paramData.getOrDefault("userId", "");
        String claId = (String) paramData.getOrDefault("claId", "");
        int textbkCrltnId = Integer.parseInt(String.valueOf(paramData.getOrDefault("textbkCrltnId", "0")));

        User user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.T.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("wrterId", userId);
        data.put("claId", claId);
        data.put("textbkCrltnId", textbkCrltnId);
        data.put("deployServerCode", deployServerCode);

        /*한 서버에 여러 컨텐츠 배포하는 경우*/
        if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
            List<String> deployServerCodeList = new ArrayList<>();
            for (String serverCode : deployServerCodeMulti.split(",")) {
                serverCode = serverCode.replaceAll("\\s+", "");
                if (StringUtils.isEmpty(serverCode)) {
                    continue;
                }
                deployServerCodeList.add(serverCode);
            }
            /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
            if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                data.put("deployServerCodeList", deployServerCodeList);
            }
        }

        Map<String, Object> cmsTextbookInfo = portalPzMapper.getLcmsTextbookInfo(data);
        if (cmsTextbookInfo == null) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - LcmsTextbookInfo No exists");
            return returnMap;
        }
        data.put("curriSemester", cmsTextbookInfo.getOrDefault("curriSemester", ""));
        data.put("version", cmsTextbookInfo.getOrDefault("version", ""));

        try {
            this.createTextBook(cmsTextbookInfo, data);
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - Null pointer error in createTextBook: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Missing required data for textbook creation");
            return returnMap;
        } catch (IllegalArgumentException e) {
            log.error("saveTcTextbook - Invalid argument error in createTextBook: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Invalid parameters for textbook creation");
            return returnMap;
        } catch (DataAccessException e) {
            log.error("saveTcTextbook - Database access error in createTextBook: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Database operation failed during textbook creation");
            return returnMap;
        } catch (SQLException e) {
            log.error("saveTcTextbook - SQL error in createTextBook: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Database query failed during textbook creation");
            return returnMap;
        } catch (RuntimeException e) {
            log.error("saveTcTextbook - Runtime error in createTextBook: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Runtime error occurred during textbook creation");
            return returnMap;
        } catch (Exception e) {
            log.error("saveTcTextbook - Unexpected error in createTextBook: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Unexpected error occurred during textbook creation");
            return returnMap;
        }

        try {
            paramData.put("textbkId", data.get("textbkId")); // 바깥에서 book key 알수 있도록 세팅
            paramData.put("saveTcTaskEvl", true);
            //this.saveTcTaskEvl(saveTEParamMap); // transaction 바깥에서 실행 - default 과제/평가 세트지를 출제한다 (이미 출제된 세트지는 pass 하는 로직 있음)
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - Null pointer error in paramData setup: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Missing required data for textbook setup");
            return returnMap;
        } catch (UnsupportedOperationException e) {
            log.error("saveTcTextbook - Unsupported operation error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Cannot modify parameter data");
            return returnMap;
        } catch (ClassCastException e) {
            log.error("saveTcTextbook - Type casting error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Invalid data type in textbook information");
            return returnMap;
        } catch (Exception e) {
            log.error("saveTcTextbook - Unexpected error in paramData setup: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Unexpected error occurred during setup");
            return returnMap;
        }
        return returnMap;
    }


    public void createTextBook(Map<String, Object> cmsTextbookInfo, Map<String, Object> data) throws Exception {
        data.put("textbkId", cmsTextbookInfo.get("textbkId"));
        data.put("textbkIdxId", cmsTextbookInfo.get("textbkIdxId"));
        data.put("brandId", cmsTextbookInfo.get("brandId"));

        /*한 서버에 여러 컨텐츠 배포하는 경우*/
        if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
            List<String> deployServerCodeList = new ArrayList<>();
            for (String serverCode : deployServerCodeMulti.split(",")) {
                serverCode = serverCode.replaceAll("\\s+", "");
                if (StringUtils.isEmpty(serverCode)) {
                    continue;
                }
                deployServerCodeList.add(serverCode);
            }
            /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
            if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                data.put("deployServerCodeList", deployServerCodeList);
            }
        }

        //교과서 적재
        portalPzMapper.insertTcTextbook(data);
        // 교과서 버젼업 (이후 버젼업 된 항목 세팅을 위해 히스토리 테이블에 데이터 적재)
        if (StringUtils.isNotEmpty(MapUtils.getString(data, "tcTextbookId"))) {
            try {
                portalPzMapper.insertTcTextbookVersion(data);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // PK, Unique Index 중복
                log.error(CustomLokiLog.errorLog(e));
            } catch (DataIntegrityViolationException e) {
                // NOT NULL 제약 조건, FK 제약 조건 등
                log.error(CustomLokiLog.errorLog(e));
            } catch (BadSqlGrammarException e) {
                // SQL 문법 오류
                log.error(CustomLokiLog.errorLog(e));
            } catch (org.apache.ibatis.exceptions.PersistenceException e) {
                // MyBatis 매퍼 관련 오류
                log.error(CustomLokiLog.errorLog(e));
            } catch (Exception e) {
                // 그 외 모든 오류
                log.error(CustomLokiLog.errorLog(e));
            }
        }
        //커리큘럼 적재
        portalPzMapper.insertTcCurriculum(data);

        //텝 적재
        portalPzMapper.insertTabInfo(data);
    }

    public void createTextBookCurriTab(Map<String, Object> tcTextbookInfo) throws Exception {
        tcTextbookInfo.put("textbkId", tcTextbookInfo.get("textbkId"));
        tcTextbookInfo.put("textbkIdxId", tcTextbookInfo.get("textbkIdxId"));
        tcTextbookInfo.put("brandId", tcTextbookInfo.get("brandId"));

        /*한 서버에 여러 컨텐츠 배포하는 경우*/
        if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
            List<String> deployServerCodeList = new ArrayList<>();
            for (String serverCode : deployServerCodeMulti.split(",")) {
                serverCode = serverCode.replaceAll("\\s+", "");
                if (StringUtils.isEmpty(serverCode)) {
                    continue;
                }
                deployServerCodeList.add(serverCode);
            }
            /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
            if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                tcTextbookInfo.put("deployServerCodeList", deployServerCodeList);
            }
        }

        // 교과서 버젼업 (이후 버젼업 된 항목 세팅을 위해 히스토리 테이블에 데이터 적재)
        if (StringUtils.isNotEmpty(MapUtils.getString(tcTextbookInfo, "tcTextbookId"))) {
            try {
                portalPzMapper.insertTcTextbookVersion(tcTextbookInfo);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // PK, Unique Index 중복
                log.error(CustomLokiLog.errorLog(e));
            } catch (DataIntegrityViolationException e) {
                // NOT NULL 제약 조건, FK 제약 조건 등
                log.error(CustomLokiLog.errorLog(e));
            } catch (BadSqlGrammarException e) {
                // SQL 문법 오류
                log.error(CustomLokiLog.errorLog(e));
            } catch (org.apache.ibatis.exceptions.PersistenceException e) {
                // MyBatis 매퍼 관련 오류
                log.error(CustomLokiLog.errorLog(e));
            } catch (Exception e) {
                // 그 외 모든 오류
                log.error(CustomLokiLog.errorLog(e));
            }
        }

        //커리큘럼 적재
        portalPzMapper.insertTcCurriculum(tcTextbookInfo);

        //텝 적재
        portalPzMapper.insertTabInfo(tcTextbookInfo);
    }

    public void setTestThumbnail(Map<String, Object> textBook) throws Exception {
        Map<String, Object> previewThumbnail = new HashMap<>();
        List<Map<String, Object>> previewThumbnailList = new ArrayList<>();

        textBook.put("textbkThumbnail", "https://con.vsaidt.com/test/server.png");
        previewThumbnail.put("previewThumbnail", "https://con.vsaidt.com/test/server.png");
        previewThumbnailList.add(previewThumbnail);
        previewThumbnailList.add(previewThumbnail);
        previewThumbnailList.add(previewThumbnail);
        textBook.put("previewThumbnailList", previewThumbnailList);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTcTextbookInfo(Map<String, Object> data) throws Exception {
        return portalPzMapper.getTcTextbookInfo(data);
    }


    @Transactional(readOnly = true)
    public Object stTextbookInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        Map<String, Object> textbookInfo = new HashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");
        returnMap.put("textbookInfo", textbookInfo);

        String userId = (String) paramData.getOrDefault("userId", "");

        User user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.S.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);

        Map<String, Object> stdtRegInfo = portalPzMapper.getStdtRegInfo(data);

        String standbyCd = "0";
        long claIdx = -1;
        String claId = "";
        if (stdtRegInfo != null) {
            data.put("claId", stdtRegInfo.getOrDefault("claId", ""));
            data.put("tcId", stdtRegInfo.getOrDefault("tcId", ""));
            claIdx = (long) stdtRegInfo.getOrDefault("claIdx", -1);
            claId = (String) stdtRegInfo.getOrDefault("claId", "");
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

        returnMap.put("claId", claId);
        returnMap.put("claIdx", claIdx);
        returnMap.put("standbyCd", standbyCd);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStTextbookInfo(Map<String, Object> data) throws Exception {
        return portalPzMapper.getStTextbookInfo(data);
    }

    /**
     *
// lcms default 평가, 과제 return json
{"errorMessage":null,"vo":[{"loginUserId":"anonymousUser","id":1,"brand_id":1,"textbook_id":1192,"matrialType":1,"matrialNm":"테스트과제","is_active":true,"is_publicOpen":true,"evlSeCd":null,"set_id":854,"set_name":"[수학_중1_1-1.소인수분해_중단원](854)","timTime":"00:60:00","description":null,"creator_id":71,"creator":"cbstest16","creator_name":"유정희","regdate":"2024-07-05 14:35:39","updater_id":null,"updater":null,"updater_name":null,"updatedate":null,"full_count":2,"open_count":null},{"loginUserId":"anonymousUser","id":2,"brand_id":1,"textbook_id":1192,"matrialType":2,"matrialNm":"형성평가 공통수학","is_active":true,"is_publicOpen":true,"evlSeCd":2,"set_id":3912,"set_name":"(학년별) 진단평가_공통수학2(3912)","timTime":"00:60:00","description":null,"creator_id":71,"creator":"cbstest16","creator_name":"유정희","regdate":"2024-07-05 14:36:18","updater_id":null,"updater":null,"updater_name":null,"updatedate":null,"full_count":2,"open_count":null}]}
     *
     * @param paramData
     *  userId : 교사 id
     *  claId : 학급 id
     *  textbkId : 교과서 id
     * @return
     * @throws Exception
     */
    public Map<String, Object> saveTcTaskEvl(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");

        String wrterId = MapUtils.getString(paramData, "userId");
        String claId = MapUtils.getString(paramData, "claId");
        int textbkId = MapUtils.getInteger(paramData, "textbkId", 0);

        if (StringUtils.isEmpty(wrterId) || StringUtils.isEmpty(claId) || textbkId == 0) {
            log.error("saveTcTaskEvl error - wrterId or claId or textbkId empty");
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "fail");
            return returnMap;
        }

        JSONObject reqParam = new JSONObject();
        reqParam.put("textbook_id", paramData.get("textbkId"));
        reqParam.put("is_active", true);
        reqParam.put("is_publicOpen", true);

        ParamOption option = ParamOption.builder()
                .url(appLcmsapiUrl + appLcmsapiTaskEvlSearchPath)
                .method(HttpMethod.POST)
                .request(reqParam)
                .build();
        ParameterizedTypeReference<ResponseVO> typeReference = new ParameterizedTypeReference<>() {};
        ResponseEntity<ResponseVO> response = aidtWebClientSender.sendWithBlock(option, typeReference);

        List<Map<String, Object>> taskEvlList = null;

        if (HttpStatus.OK == response.getStatusCode()) {
            ResponseVO responseVO = response.getBody();
            if (responseVO.getVo() != null) {
                if (responseVO.getVo() instanceof List) {
                    taskEvlList = (List<Map<String, Object>>) responseVO.getVo();
                } else if (responseVO.getVo() instanceof Map) {
                    taskEvlList = new ArrayList<>();
                    taskEvlList.add((Map<String, Object>) responseVO.getVo());
                }
            }
        }

        if (CollectionUtils.isEmpty(taskEvlList)) {
            log.info("saveTcTaskEvl error - taskEvlList empty");
            return returnMap;
        }

        List<Map<String, Object>> taskParamList = new LinkedList<>();
        List<Map<String, Object>> evlParamList = new LinkedList<>();

        Date todayDt = new Date();
        Date after1YearDt = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDt);
        cal.add(Calendar.YEAR, 1);
        after1YearDt = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String startDate = sdf.format(todayDt);
        String endDate = sdf.format(after1YearDt);

        if (taskEvlList != null && !taskEvlList.isEmpty()) {
            for (Map<String, Object> map : taskEvlList) {
                int matrialType = MapUtils.getInteger(map, "matrialType", 0);
                if (matrialType == 0) {
                    paramData.put("matrialType", 0);
                    continue;
                }
                String setsId = MapUtils.getString(map, "set_id");
                if (StringUtils.isEmpty(setsId)) {
                    paramData.put("setsId", "");
                    continue;
                }
                paramData.put("matrialType", matrialType);
                paramData.put("setsId", setsId);
                Map<String, Object> evalTaskInfoMap = tchEvalService.findTchEvalTaskInfo(paramData);
                // 이미 있을 경우 continue
                if (MapUtils.isNotEmpty(evalTaskInfoMap)) {
                    continue;
                }
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("wrterId", wrterId);
                paramMap.put("claId", claId);
                paramMap.put("textbookId", textbkId);
                paramMap.put("setsId", setsId);
                paramMap.put("pdEvlStDt", startDate);
                paramMap.put("pdEvlEdDt", endDate);
                paramMap.put("timTime", map.get("timTime"));
                // 내부에서 중복 검증 시 처리하기 위한 추가 key value
                paramMap.put("userId", wrterId);
                paramMap.put("textbkId", textbkId);
                // 과제
                if (matrialType == 1) {
                    paramMap.put("taskNm", map.get("matrialNm"));
                    taskParamList.add(paramMap);
                }
                // 평가
                else if (matrialType == 2) {
                    paramMap.put("evlNm", map.get("matrialNm"));
                    paramMap.put("evlSeCd", map.get("evlSeCd"));
                    evlParamList.add(paramMap);
                }
            }
        }else{
            log.warn("taskEvlList가 null이거나 비어있습니다.");
        }

        // 평가 출제
        if (CollectionUtils.isNotEmpty(evlParamList)) {
            Object evlResultData = tchEvalService.createTchEvalCreateForTextbk(evlParamList);
            if (evlResultData == null) {
                log.error("saveTcTaskEvl > createTchEvalCreateForTextbk error - evlResultData null");
            } else {
                boolean isEvlSuccess = MapUtils.getBoolean((Map<String, Object>) evlResultData, "resultOk", false);
                if (isEvlSuccess == false) {
                    log.error("saveTcTaskEvl > createTchEvalCreateForTextbk error - isEvlSuccess false");
                }
            }
        }

        // 숙제 출제
        if (CollectionUtils.isNotEmpty(taskParamList)) {
            Object homeworkResultData = tchHomewkService.createTchHomewkCreateForTextbk(taskParamList);
            if (homeworkResultData == null) {
                log.error("saveTcTaskEvl > createTchHomewkCreateForTextbk error - homeworkResultData null");
            } else {
                boolean isHomeworkSuccess = MapUtils.getBoolean((Map<String, Object>) homeworkResultData, "resultOk", false);
                if (isHomeworkSuccess == false) {
                    log.error("saveTcTaskEvl > createTchHomewkCreateForTextbk error - isHomeworkSuccess fales");
                }
            }
        }

        return returnMap;
    }


    /**
     *
// lcms default 평가, 과제 return json
{"errorMessage":null,"vo":[{"loginUserId":"anonymousUser","id":1,"brand_id":1,"textbook_id":1192,"matrialType":1,"matrialNm":"테스트과제","is_active":true,"is_publicOpen":true,"evlSeCd":null,"set_id":854,"set_name":"[수학_중1_1-1.소인수분해_중단원](854)","timTime":"00:60:00","description":null,"creator_id":71,"creator":"cbstest16","creator_name":"유정희","regdate":"2024-07-05 14:35:39","updater_id":null,"updater":null,"updater_name":null,"updatedate":null,"full_count":2,"open_count":null},{"loginUserId":"anonymousUser","id":2,"brand_id":1,"textbook_id":1192,"matrialType":2,"matrialNm":"형성평가 공통수학","is_active":true,"is_publicOpen":true,"evlSeCd":2,"set_id":3912,"set_name":"(학년별) 진단평가_공통수학2(3912)","timTime":"00:60:00","description":null,"creator_id":71,"creator":"cbstest16","creator_name":"유정희","regdate":"2024-07-05 14:36:18","updater_id":null,"updater":null,"updater_name":null,"updatedate":null,"full_count":2,"open_count":null}]}
     *
     * @param paramData
     *  groupKey : 그룹 key (ex : stress)
     * @return
     * @throws Exception
     */
    public Map<String, Object> saveTcTaskEvlByUserGroup(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");

        List<Map<String, Object>> tcInfoList = portalPzMapper.findTcTextbookListByGroupKey(paramData);
        if (CollectionUtils.isEmpty(tcInfoList)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "saveTcTaskEvl error - taskEvlList empty");
            return returnMap;
        }
        // 먼저 파라미터에서 넘어온 교과서 id 로 처리
        int textbkId = MapUtils.getInteger(paramData, "textbkId", 0);
        // 파라미터가 비어 있으면 선생님 교과서 정보를 기반으로 처리
        if (textbkId == 0) {
            textbkId = MapUtils.getInteger(tcInfoList.get(0), "textbkId", 0);
        }
        if (textbkId == 0) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "saveTcTaskEvl error - textbkId empty");
        }

        JSONObject reqParam = new JSONObject();
        reqParam.put("textbook_id", textbkId);
        reqParam.put("is_active", true);
        reqParam.put("is_publicOpen", true);

        ParamOption option = ParamOption.builder()
                .url(appLcmsapiUrl + appLcmsapiTaskEvlSearchPath)
                .method(HttpMethod.POST)
                .request(reqParam)
                .build();
        ParameterizedTypeReference<ResponseVO> typeReference = new ParameterizedTypeReference<>() {};
        ResponseEntity<ResponseVO> response = aidtWebClientSender.sendWithBlock(option, typeReference);

        List<Map<String, Object>> taskEvlList = null;

        if (HttpStatus.OK == response.getStatusCode()) {
            ResponseVO responseVO = response.getBody();
            if (responseVO.getVo() != null) {
                if (responseVO.getVo() instanceof List) {
                    taskEvlList = (List<Map<String, Object>>) responseVO.getVo();
                } else if (responseVO.getVo() instanceof Map) {
                    taskEvlList = new ArrayList<>();
                    taskEvlList.add((Map<String, Object>) responseVO.getVo());
                }
            }
        }

        if (CollectionUtils.isEmpty(taskEvlList)) {
            log.info("saveTcTaskEvl error - taskEvlList empty");
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "saveTcTaskEvl error - taskEvlList empty");
            return returnMap;
        }

        List<Map<String, Object>> taskParamList = new LinkedList<>();
        List<Map<String, Object>> evlParamList = new LinkedList<>();

        Date todayDt = new Date();
        Date after1YearDt = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDt);
        cal.add(Calendar.YEAR, 1);
        after1YearDt = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String startDate = sdf.format(todayDt);
        String endDate = sdf.format(after1YearDt);

        if (taskEvlList != null && !taskEvlList.isEmpty()) {
            for (Map<String, Object> map : taskEvlList) {
                int matrialType = MapUtils.getInteger(map, "matrialType", 0);
                if (matrialType == 0) {
                    paramData.put("matrialType", 0);
                    continue;
                }
                String setsId = MapUtils.getString(map, "set_id");
                if (StringUtils.isEmpty(setsId)) {
                    paramData.put("setsId", "");
                    continue;
                }
                paramData.put("matrialType", matrialType);
                paramData.put("setsId", setsId);
                paramData.put("textbkId", textbkId);


                // 그룹 키로 조회된 모든 선생님에 평가, 과제 세팅
                for (Map<String, Object> tcInfoMap : tcInfoList) {

                    // paramMap 객체 계정 별 초기화
                    Map<String, Object> paramMap = new HashMap<>();

                    paramMap.put("setsId", setsId);
                    paramMap.put("pdEvlStDt", startDate);
                    paramMap.put("pdEvlEdDt", endDate);
                    paramMap.put("timTime", map.get("timTime"));

                    String wrterId = MapUtils.getString(tcInfoMap, "wrterId", "");
                    String claId = MapUtils.getString(tcInfoMap, "claId", "");
                    paramMap.put("wrterId", wrterId);
                    paramMap.put("claId", claId);
                    paramMap.put("textbookId", textbkId);
                    // 내부에서 중복 검증 시 처리하기 위한 추가 key value
                    paramMap.put("userId", wrterId);
                    paramMap.put("textbkId", textbkId);
                    // 출제 시에 검증 하기 때문에 교차 검증 하지 않음 (부하를 위한 임의 로직이기 때문에 제거 / 실 운영 로직은 교차 검증 함)
                /*Map<String, Object> evalTaskInfoMap = tchEvalService.findTchEvalTaskInfo(paramMap);
                // 이미 있을 경우 continue
                if (MapUtils.isNotEmpty(evalTaskInfoMap)) {
                    continue;
                }*/
                    // 과제
                    if (matrialType == 1) {
                        paramMap.put("taskNm", map.get("matrialNm"));
                        taskParamList.add(paramMap);
                    }
                    // 평가
                    else if (matrialType == 2) {
                        paramMap.put("evlNm", map.get("matrialNm"));
                        paramMap.put("evlSeCd", map.get("evlSeCd"));
                        evlParamList.add(paramMap);
                    }
                }
            }
        }else{
            log.warn("taskEvlList가 null이거나 비어있습니다.");
        }

        // 평가 출제
        if (CollectionUtils.isNotEmpty(evlParamList)) {
            Object evlResultData = tchEvalService.createTchEvalCreateForTextbk(evlParamList);
            if (evlResultData == null) {
                log.error("saveTcTaskEvl > createTchEvalCreateForTextbk error - evlResultData null");
            } else {
                boolean isEvlSuccess = MapUtils.getBoolean((Map<String, Object>) evlResultData, "resultOk", false);
                if (isEvlSuccess == false) {
                    log.error("saveTcTaskEvl > createTchEvalCreateForTextbk error - isEvlSuccess false");
                }
            }
        }

        // 숙제 출제
        if (CollectionUtils.isNotEmpty(taskParamList)) {
            Object homeworkResultData = tchHomewkService.createTchHomewkCreateForTextbk(taskParamList);
            if (homeworkResultData == null) {
                log.error("saveTcTaskEvl > createTchHomewkCreateForTextbk error - homeworkResultData null");
            } else {
                boolean isHomeworkSuccess = MapUtils.getBoolean((Map<String, Object>) homeworkResultData, "resultOk", false);
                if (isHomeworkSuccess == false) {
                    log.error("saveTcTaskEvl > createTchHomewkCreateForTextbk error - isHomeworkSuccess fales");
                }
            }
        }

        return returnMap;
    }

    public Map<String, Object> getTcClaUserInfo(Map<String, Object> data) throws Exception {
        Map<String,Object> userInfo = new HashMap<>();
        userInfo = portalPzMapper.getTcClaUserInfo(data);
        return userInfo;
    }
}