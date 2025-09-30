package com.visang.aidt.lms.api.materials.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.visang.aidt.lms.api.materials.mapper.PortalMapper;
import com.visang.aidt.lms.api.repository.UserRepository;
import com.visang.aidt.lms.api.repository.entity.User;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalService {

    private final PortalMapper portalMapper;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Object findSchoolList(Map<String, Object> paramData) throws Exception{
        var returnMap = new LinkedHashMap<>();
        returnMap.put("list", portalMapper.findSchoolList(paramData));
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findGradeList(Map<String, Object> paramData) throws Exception{
        var returnMap = new LinkedHashMap<>();
        returnMap.put("list", portalMapper.findGradeList(paramData));
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findClassList(Map<String, Object> paramData) throws Exception{
        var returnMap = new LinkedHashMap<>();
        returnMap.put("list", portalMapper.findClassList(paramData));
        return returnMap;
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
        String semester = (String) paramData.getOrDefault("semester", "");
        String subject = (String) paramData.getOrDefault("subject", "");

        User user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.T.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("wrterId", userId);
        data.put("claId", claId);
        data.put("smteCd", semester);

        // 세팅된 교과서 조회
        Map<String, Object> tcTextbookInfo = this.getTcTextbookInfo(data);
        // 선생님 교과서 선택 여부
        String areadyTextbookYn = "N";

        if (tcTextbookInfo == null) {
            //교사 학급정보 조회
            Map<String, Object> tcClaInfo =  portalMapper.getTcClaInfo(data);
            if (tcClaInfo == null) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "Error - TcClaInfo No exists");
                return returnMap;
            }

            //LCMS 교과서 목록 조회
            data.put("curriSchool", tcClaInfo.getOrDefault("curriSchool", ""));
            data.put("curriGrade", tcClaInfo.getOrDefault("curriGrade", ""));
            data.put("curriSubject", subject);
            data.put("curriSemester", semester);
            List<Map<String, Object>> cmsTextbookList = portalMapper.findLcmsTextbookList(data);

            //LCMS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
            if (cmsTextbookList.size() == 1) {
                try {
                    Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);
                    String jsonStr = MapUtils.getString(cmsTextbookInfo, "data");
                    JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
                    JsonObject textbookIndex = jsonObject.getAsJsonObject("textbookIndex");
                    JsonArray textbookCurriculumList =  textbookIndex.getAsJsonArray("textbookCurriculumList");

                    data.put("textbkId",jsonObject.get("id").getAsInt());
                    data.put("textbkIdxId",jsonObject.get("textbookIndex_id").getAsInt());
                    portalMapper.insertTcTextbook(data);

                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>(){}.getType();
                    for (JsonElement textbookCurriculumEle : textbookCurriculumList) {
                        JsonObject textbookCurriculum = textbookCurriculumEle.getAsJsonObject();
                        Map<String, Object> curriculumMap = new HashMap<>();
                        curriculumMap = gson.fromJson(textbookCurriculum, type);
                        curriculumMap.put("wrterId", userId);
                        curriculumMap.put("claId", claId);
                        curriculumMap.put("smteCd", semester);
                        curriculumMap.put("textbkId", jsonObject.get("id").getAsInt());
                        curriculumMap.put("textbkIdxId", jsonObject.get("textbookIndex_id").getAsInt());
                        curriculumMap.put("brandId", jsonObject.get("brand_id").getAsInt());
                        JsonElement curriUnitListElement = textbookCurriculum.get("curriUnitList");
                        if (curriUnitListElement != null && !curriUnitListElement.isJsonNull()) {
                            String curriUnitListStr = curriUnitListElement.getAsString();
                            String curriUnitListCleaned = curriUnitListStr.replaceAll("[\\[\\]\"]", "");
                            String[] curriUnitArray = curriUnitListCleaned.split(",");
                            int i = 1;
                            for (String unit : curriUnitArray) {
                                if (!StringUtils.isEmpty(unit)) {
                                    curriculumMap.put("curriUnit"+i, unit.trim());
                                }
                                i++;
                            }
                        }
                        portalMapper.insertTcCurriculum(curriculumMap);

                        JsonArray textbookTabList = textbookCurriculum.getAsJsonArray("textbookTab");
                        if (textbookTabList != null) {
                            List<Map<String, Object>> textbookTabMapList = new LinkedList<>();
                            for (JsonElement textbookTabEle : textbookTabList) {
                                JsonObject textbookTab = textbookTabEle.getAsJsonObject();
                                Map<String, Object> textbookTabMap = new HashMap<>();
                                textbookTabMap = gson.fromJson(textbookTab, type);
                                textbookTabMap.put("wrterId", userId);
                                textbookTabMap.put("claId", claId);
                                textbookTabMap.put("smteCd", semester);
                                textbookTabMap.put("textbkId", jsonObject.get("id").getAsInt());
                                String expos_at = "N";
                                boolean is_publicOpen = textbookTab.get("is_publicOpen").getAsBoolean();
                                if (is_publicOpen) {
                                    expos_at = "Y";
                                }
                                JsonElement accessLevelEle =  textbookTab.get("accessLevel");
                                if (accessLevelEle != null && !accessLevelEle.isJsonNull()) {
                                    int accessLevel = accessLevelEle.getAsInt();
                                    if (accessLevel == 12) {
                                        expos_at = "Y";
                                    } else if (accessLevel == 1) {
                                        expos_at = "N";
                                    }
                                }
                                textbookTabMap.put("expos_at", expos_at);
                                textbookTabMapList.add(textbookTabMap);
                            }
                            // tab auto_increment id 로 정렬
                            Collections.sort(textbookTabMapList, new Comparator<Map<String, Object>>() {
                                @Override
                                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                                    int orderNo1_1 = MapUtils.getInteger(map1, "id", 0);
                                    int orderNo1_2 = MapUtils.getInteger(map2, "id", 0);
                                    if (orderNo1_1 < orderNo1_2) {
                                        return -1;
                                    } else if (orderNo1_1 > orderNo1_2) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                            // tabSeq 계산해서 insert
                            int tabSeq = 1;
                            for (Map<String, Object> textbookTabMap : textbookTabMapList) {
                                textbookTabMap.put("tabSeq", tabSeq++);
                                portalMapper.insertTabInfo(textbookTabMap);
                            }
                        }
                    }

                    Map<String, Object> textBook = new HashMap<>();
                    Map<String, Object> previewThumbnail = new HashMap<>();
                    textBook.put("textbkId", cmsTextbookInfo.getOrDefault("textbkId", -1));
                    textBook.put("textbkIdxId", cmsTextbookInfo.getOrDefault("textbkIdxId", -1));
                    textBook.put("textbkCrltnId", cmsTextbookInfo.getOrDefault("textbkCrltnId", -1));
                    textBook.put("textbkNm", cmsTextbookInfo.getOrDefault("textbkNm", ""));

                    //Thumnail 이미지 현재는 CMS 에서 세팅되고 있지 않아서, 추후 CMS 개발 완료 시 변경 필요.
                    setTestThumbnail(textBook);

                    textbookList.add(textBook);
                    returnMap.put("textbookList", textbookList);
                    areadyTextbookYn = "Y";
                } catch (IndexOutOfBoundsException e) {
                    log.error("saveTcTextbook - CMS textbook list is empty: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - No textbook data found in CMS");
                    return returnMap;
                } catch (JsonSyntaxException e) {
                    log.error("saveTcTextbook - JSON parsing error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Invalid JSON format in CMS textbook data");
                    return returnMap;
                } catch (IllegalStateException e) {
                    log.error("saveTcTextbook - JSON structure error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Invalid JSON structure in textbook data");
                    return returnMap;
                } catch (NumberFormatException e) {
                    log.error("saveTcTextbook - Number format error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Invalid number format in textbook data");
                    return returnMap;
                } catch (NullPointerException e) {
                    log.error("saveTcTextbook - Null pointer error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Missing required data in textbook information");
                    return returnMap;
                } catch (DataAccessException e) {
                    log.error("saveTcTextbook - Database access error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Database operation failed");
                    return returnMap;
                } catch (SQLException e) {
                    log.error("saveTcTextbook - SQL error: {}", e.getMessage());
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Database query failed");
                    return returnMap;
                } catch (Exception e) {
                    log.error("saveTcTextbook - Unexpected error: {}", CustomLokiLog.errorLog(e));
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Error - Unexpected error occurred");
                    return returnMap;
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

    @Transactional
    public Object saveTcTextbook(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "success");

        String userId = (String) paramData.getOrDefault("userId", "");
        String claId = (String) paramData.getOrDefault("claId", "");
        String semester = (String) paramData.getOrDefault("semester", "");

        int textbkCrltnId =  Integer.parseInt(String.valueOf(paramData.getOrDefault("textbkCrltnId", "0")));

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
        Map<String, Object> lcmsTextbookInfo = portalMapper.getLcmsTextbookInfo(data);
        if (lcmsTextbookInfo == null) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - LcmsTextbookInfo No exists");
            return returnMap;
        }

        try {
            String jsonStr = MapUtils.getString(lcmsTextbookInfo, "data");
            JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
            JsonObject textbookIndex = jsonObject.getAsJsonObject("textbookIndex");
            JsonArray textbookCurriculumList =  textbookIndex.getAsJsonArray("textbookCurriculumList");

            data.put("textbkId",jsonObject.get("id").getAsInt());
            data.put("textbkIdxId",jsonObject.get("textbookIndex_id").getAsInt());
            data.put("smteCd", semester);
            portalMapper.insertTcTextbook(data);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            for (JsonElement textbookCurriculumEle : textbookCurriculumList) {
                JsonObject textbookCurriculum = textbookCurriculumEle.getAsJsonObject();
                Map<String, Object> curriculumMap = new HashMap<>();
                curriculumMap = gson.fromJson(textbookCurriculum, type);
                curriculumMap.put("wrterId", userId);
                curriculumMap.put("claId" ,claId);
                curriculumMap.put("smteCd", semester);
                curriculumMap.put("textbkId",jsonObject.get("id").getAsInt());
                curriculumMap.put("textbkIdxId",jsonObject.get("textbookIndex_id").getAsInt());
                curriculumMap.put("brandId", jsonObject.get("brand_id").getAsInt());
                JsonElement curriUnitListElement = textbookCurriculum.get("curriUnitList");
                if (curriUnitListElement != null && !curriUnitListElement.isJsonNull()) {
                    String curriUnitListStr = curriUnitListElement.getAsString();
                    String curriUnitListCleaned = curriUnitListStr.replaceAll("[\\[\\]\"]", "");
                    String[] curriUnitArray = curriUnitListCleaned.split(",");
                    int i = 1;
                    for (String unit : curriUnitArray) {
                        if (!StringUtils.isEmpty(unit)) {
                            curriculumMap.put("curriUnit"+i, unit.trim());
                        }
                        i++;
                    }
                }
                portalMapper.insertTcCurriculum(curriculumMap);

                JsonArray textbookTabList = textbookCurriculum.getAsJsonArray("textbookTab");
                if (textbookTabList != null) {
                    // 정렬 용 객체
                    List<Map<String, Object>> textbookTabMapList = new LinkedList<>();
                    for (JsonElement textbookTabEle : textbookTabList) {
                        JsonObject textbookTab = textbookTabEle.getAsJsonObject();
                        Map<String, Object> textbookTabMap = new HashMap<>();
                        textbookTabMap = gson.fromJson(textbookTab, type);
                        textbookTabMap.put("wrterId", userId);
                        textbookTabMap.put("claId", claId);
                        textbookTabMap.put("smteCd", semester);
                        textbookTabMap.put("textbkId", jsonObject.get("id").getAsInt());
                        String expos_at = "N";
                        boolean is_publicOpen = textbookTab.get("is_publicOpen").getAsBoolean();
                        if (is_publicOpen) {
                            expos_at = "Y";
                        }
                        JsonElement accessLevelEle =  textbookTab.get("accessLevel");
                        if (accessLevelEle != null && !accessLevelEle.isJsonNull()) {
                            int accessLevel = accessLevelEle.getAsInt();
                            if (accessLevel == 12) {
                                expos_at = "Y";
                            } else if (accessLevel == 1) {
                                expos_at = "N";
                            }
                        }
                        textbookTabMap.put("expos_at", expos_at);
                        textbookTabMapList.add(textbookTabMap);
                    }
                    // tab auto_increment id 로 정렬
                    Collections.sort(textbookTabMapList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                            int orderNo1_1 = MapUtils.getInteger(map1, "id", 0);
                            int orderNo1_2 = MapUtils.getInteger(map2, "id", 0);
                            if (orderNo1_1 < orderNo1_2) {
                                return -1;
                            } else if (orderNo1_1 > orderNo1_2) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    // tabSeq 계산해서 insert
                    int tabSeq = 1;
                    for (Map<String, Object> textbookTabMap : textbookTabMapList) {
                        textbookTabMap.put("tabSeq", tabSeq++);
                        portalMapper.insertTabInfo(textbookTabMap);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            log.error("saveTcTextbook - JSON parsing error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Invalid JSON format in textbook data");
            return returnMap;
        } catch (IllegalStateException e) {
            log.error("saveTcTextbook - JSON structure error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Invalid JSON structure");
            return returnMap;
        } catch (NumberFormatException e) {
            log.error("saveTcTextbook - Number format error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Invalid number format in textbook data");
            return returnMap;
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - Null pointer error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Missing required data in textbook information");
            return returnMap;
        } catch (DataAccessException e) {
            log.error("saveTcTextbook - Database access error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Database operation failed");
            return returnMap;
        } catch (SQLException e) {
            log.error("saveTcTextbook - SQL error: {}", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Database query failed");
            return returnMap;
        } catch (Exception e) {
            log.error("saveTcTextbook - Unexpected error: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - Unexpected error occurred");
            return returnMap;
        }
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

        User user = userRepository.findByUserId(userId);
        if (user == null || !user.getUserSeCd().equals(UserDiv.S.getCode())) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "Error - User No exists");
            return returnMap;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        Map<String, Object> stdtRegInfo = portalMapper.getStdtRegInfo(data);

        String standbyCd = "0";
        if (stdtRegInfo != null) {
            data.put("claId", stdtRegInfo.getOrDefault("claId", ""));
            data.put("tcId", stdtRegInfo.getOrDefault("tcId", ""));
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
        returnMap.put("standbyCd", standbyCd);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStTextbookInfo(Map<String, Object> data) throws Exception {
        return portalMapper.getStTextbookInfo(data);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTcTextbookInfo(Map<String, Object> data) throws Exception {
        return portalMapper.getTcTextbookInfo(data);
    }

    public Map<String, Object> getTcClaUserInfo(Map<String, Object> data) throws Exception {
        Map<String,Object> userInfo = new HashMap<>();
        userInfo = portalMapper.getTcClaUserInfo(data);
        return userInfo;
    }

}