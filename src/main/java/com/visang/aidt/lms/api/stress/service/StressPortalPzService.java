package com.visang.aidt.lms.api.stress.service;

import com.visang.aidt.lms.api.assessment.service.TchEvalService;
import com.visang.aidt.lms.api.homework.service.TchHomewkService;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.stress.mapper.StressPortalPzMapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StressPortalPzService {

    private final StressPortalPzMapper portalPzMapper;

    private final UserRepository userRepository;

    @Value("${app.lcmsapi.url}")
    public String appLcmsapiUrl;
    @Value("${app.lcmsapi.task-evl-search-path}")
    public String appLcmsapiTaskEvlSearchPath;
    @Value("${app.lcmsapi.deployServerCode}")
    public String deployServerCode;
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
            List<Map<String, Object>> cmsTextbookList = portalPzMapper.findLcmsTextbookList(data);
            //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
            if (cmsTextbookList.size() == 1) {
                try {
                    Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);

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
                } catch (NullPointerException e) {
                    log.error("createTextBook - NullPointerException:", e);
                    CustomLokiLog.errorLog(e);
                    throw e;
                } catch (IllegalArgumentException e) {
                    log.error("createTextBook - IllegalArgumentException:", e);
                    CustomLokiLog.errorLog(e);
                    throw e;
                } catch (IndexOutOfBoundsException e) {
                    log.error("createTextBook - IndexOutOfBoundsException:", e);
                    CustomLokiLog.errorLog(e);
                    throw e;
                } catch (RuntimeException e) {
                    log.error("createTextBook - RuntimeException:", e);
                    CustomLokiLog.errorLog(e);
                    throw e;
                } catch (Exception e) {
                    log.error(CustomLokiLog.errorLog(e));
                    throw e;
                }
            } else {
                for (Map<String, Object> cmsTextbook : cmsTextbookList) {
                    Map<String, Object> textBook = new HashMap<>();
                    Map<String, Object> previewThumbnail = new HashMap<>();
                    textBook.put("textbkId", cmsTextbook.getOrDefault("textbkId", -1));
                    textBook.put("textbkIdxId", cmsTextbook.getOrDefault("textbkIdxId", -1));
                    textBook.put("textbkCrltnId", cmsTextbook.getOrDefault("textbkCrltnId", -1));
                    textBook.put("textbkNm", cmsTextbook.getOrDefault("textbkNm", ""));

                    //Thumnail 이미지 현재는 CMS 에서 세팅되고 있지 않아서, 추후 CMS 개발 완료 시 변경 필요.
                    setTestThumbnail(textBook);

                    textbookList.add(textBook);
                }
                returnMap.put("textbookList", textbookList);
            }
        } else {
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
        Map<String, Object> cmsTextbookInfo = portalPzMapper.getLcmsTextbookInfo(data);
        if (cmsTextbookInfo == null) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - LcmsTextbookInfo No exists");
            return returnMap;
        }
        data.put("curriSemester", cmsTextbookInfo.getOrDefault("curriSemester", ""));

        try {
            this.createTextBook(cmsTextbookInfo, data);
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - createTextBook NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("saveTcTextbook - createTextBook IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (RuntimeException e) {
            log.error("saveTcTextbook - createTextBook RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            throw e;
        }

        try {
            paramData.put("textbkId", data.get("textbkId")); // 바깥에서 book key 알수 있도록 세팅
            paramData.put("saveTcTaskEvl", true);
            //this.saveTcTaskEvl(saveTEParamMap); // transaction 바깥에서 실행 - default 과제/평가 세트지를 출제한다 (이미 출제된 세트지는 pass 하는 로직 있음)
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - saveTcTaskEvl NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("saveTcTextbook - saveTcTaskEvl IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (RuntimeException e) {
            log.error("saveTcTextbook - saveTcTaskEvl RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            throw e;
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            throw e;
        }
        return returnMap;
    }


    public void createTextBook(Map<String, Object> cmsTextbookInfo, Map<String, Object> data) throws Exception {
        data.put("textbkId", cmsTextbookInfo.get("textbkId"));
        data.put("textbkIdxId", cmsTextbookInfo.get("textbkIdxId"));
        data.put("brandId", cmsTextbookInfo.get("brandId"));
        //교과서 적재
        portalPzMapper.insertTcTextbook(data);

        //커리큘럼 적재
        portalPzMapper.insertTcCurriculum(data);

        //텝 적재
        portalPzMapper.insertTabInfo(data);
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
}