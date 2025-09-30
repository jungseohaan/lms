package com.visang.aidt.lms.api.bookmark.service;

import com.visang.aidt.lms.api.bookmark.mapper.TchBmkMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchBmkService {
    private final TchBmkMapper tchBmkMapper;

    /**
     * (북마크).북마크 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findBkmkList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //bkmkList
        List<String> findBkmkForm = Arrays.asList("bkmkId"
                                                ,"claId"
                                                ,"textbkId"
                                                ,"ebkId"
                                                ,"tabId"
                                                ,"tabNm"
                                                ,"moduleId"
                                                ,"subId"
                                                ,"moduleNm"
                                                ,"articleCategory"
                                                ,"articleType"
                                                ,"crculId"
                                                ,"cocnrAt"
                                                ,"text"
                                                ,"unitNm"
                                                ,"shared"
                                                ,"currClorNum"
                                                ,"pdfUrl"
                                                ,"page"
                                                ,"unitNum");

        if (MapUtils.getInteger(paramData, "scrnSeCd") == 1) {
            findBkmkForm = Arrays.asList("bkmkId", "claId", "textbkId","ebkId", "tabId", "tabNm", "moduleId", "subId","moduleNm", "articleCategory", "articleType", "crculId", "cocnrAt", "text","unitNm", "shared", "currClorNum");

        } else if (MapUtils.getInteger(paramData, "scrnSeCd") == 2) {
            findBkmkForm = Arrays.asList("bkmkId", "claId", "textbkId","ebkId", "tabId", "tabNm", "moduleId", "subId", "ebkId", "crculId", "pdfUrl", "page", "unitNum", "cocnrAt", "shared", "currClorNum");

        } else if (MapUtils.getInteger(paramData, "scrnSeCd") == 3) {
            paramData.put("scrnSeCd" , null);
        }


        //kbmkTagList
        List<String> findKbmkTagForm = Arrays.asList("bkmkTagMapngId", "textbkId", "tagId", "tagNm", "clorNum", "bassTagAt", "bkmkId");

        //bkmkList 조회
        List<LinkedHashMap<Object, Object>> findBkmkListList = AidtCommonUtil.filterToList(findBkmkForm, tchBmkMapper.findBkmkList(paramData));
/*
        List<Integer> bkmkIdList = new ArrayList<>();
        for (LinkedHashMap<Object, Object> BkmkMaptemp : findBkmkListList) {
            bkmkIdList.add(MapUtils.getInteger(BkmkMaptemp, "bkmkId"));
        }

        paramData.put("bkmkId", bkmkIdList);
 */
        List<LinkedHashMap<Object, Object>> findBkmkResultList = null;
        if (ObjectUtils.isNotEmpty(findBkmkListList)) {
            List<LinkedHashMap<Object, Object>> kbmkTagList = AidtCommonUtil.filterToList(findKbmkTagForm, tchBmkMapper.findKbmkTagList(findBkmkListList, paramData));
            findBkmkResultList = CollectionUtils.emptyIfNull(findBkmkListList).stream()
                .map(s -> {
                    List<LinkedHashMap<Object, Object>> tagList = CollectionUtils.emptyIfNull(kbmkTagList).stream()
                        .filter(t -> StringUtils.equals(MapUtils.getString(s,"bkmkId"), MapUtils.getString(t,"bkmkId")))
                        .map(t -> {
                            t.remove("bkmkId");
                            t.remove("bkmkTagMapngId");
                            return t;
                        }).toList();
                    s.put("kbmkTagList", tagList);
                    return s;
                }).toList();
        }

        paramData.remove("bkmkId");
        returnMap.putAll(paramData);

        returnMap.put("bkmkList", findBkmkResultList);
        return returnMap;
    }

    /**
     * (북마크).북마크 공유하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Object createShareBmk(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> bkmkId = (List<Map<String, Object>>) paramData.get("bkmkId");

        var returnMap = new LinkedHashMap<>();
        returnMap.put("userId", paramData.get("userId"));
        returnMap.put("bkmkId", bkmkId);
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "실패");

        Map<String, Object> userInfo = tchBmkMapper.findUserInfo(paramData);

        String userSeCd = MapUtils.getString(userInfo, "userSeCd");


        String cmnTagName = "";
        boolean bool_s = (null != userSeCd && ("S").equals(userSeCd));
        boolean bool_t = (null != userSeCd && ("T").equals(userSeCd));
        boolean result1 = bool_s || bool_t;
        boolean result2 = ! (bool_s || bool_t);

        if ( !(bool_s || bool_t) ) {
            throw new AidtException("Not Found USER_SE_CODE :: Check USER ID");
        }

        int modifyRslt = 0;
        Map<String, Object> tempParam = new HashMap<>();


        for(int j=0; j<2; j++) {
            if (j == 0) {
                cmnTagName = "학생 공유";
            } else {
                cmnTagName = "선생님 공유";
            }

            tempParam.put("cmnTagName"  , cmnTagName             );

            for(int i=0; i<bkmkId.size(); i++) {
                tempParam.put("bkmkId"      , bkmkId.get(i)          );
                tempParam.put("userId"      , paramData.get("userId"));

                modifyRslt = tchBmkMapper.modifyBkmkCocnrAt(tempParam); // 1 : update bkmk_info

                //success
                if(modifyRslt > 0) {
                    returnMap.put("resultOK", true);
                    returnMap.put("resultMsg", "성공");

                    Map<String, Object> tagCountMap = tchBmkMapper.createShareBmk_TagCount(tempParam);
                    Integer tagCount = MapUtils.getInteger(tagCountMap, "cnt");

                    if (tagCount < 1) {
                        Map<String, Object> shareBassTagIdMap = tchBmkMapper.createShareBmk_BassTagId(tempParam);

                        if (ObjectUtils.isEmpty(shareBassTagIdMap)) {
                            int insertTagInfoRslt = tchBmkMapper.insertTagInfo(tempParam); // insert tag_info
                            log.info("insertTagInfoRslt:{}", insertTagInfoRslt);
                        } else {
                            Integer shareBassTagId = MapUtils.getInteger(shareBassTagIdMap, "id");
                            tempParam.put("id", shareBassTagId);
                        }

                        tempParam.put("tagId", tempParam.get("id"));
                        int insertBkmkTagMapngRslt = tchBmkMapper.insertBkmkTagMapng(tempParam);
                        log.info("insertBkmkTagMapngRslt:{}", insertBkmkTagMapngRslt);
                    }
                }
            }
        }

        return returnMap;
    }


    public Object createShareBmkClear(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("userId", paramData.get("userId"));
        returnMap.put("bkmkId", paramData.get("bkmkId"));
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "실패");

        int modifyRslt = tchBmkMapper.modifyBkmkClearCocnrAt(paramData); // 1 : update bkmk_info
        int insertBkmkTagMapngRslt = tchBmkMapper.deleteBkmkTagMapngByTagId(paramData);

        // if (modifyRslt > 0  && insertBkmkTagMapngRslt >0) {
        if (modifyRslt > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        }
        else {
            throw new AidtException("북마크공유취소 프로세스 실패했습니다. 관리자에 문의 바랍니다.");
        }

        return returnMap;
    }

    /**
     * (북마크).북마크 설정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object insertBkmkInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)/* && MapUtils.getIntValue(paramData, "scrnSeCd", 0) == 1 */) {
            paramData.put("subId", 0);
        }

        int insertBkmkInfoRslt = tchBmkMapper.insertBkmkInfo(paramData);

        if (insertBkmkInfoRslt > 0) {
            returnMap.put("bkmkId", MapUtils.getInteger(paramData ,"id"));
            returnMap.put("userId", paramData.get("userId"));
                    returnMap.put("resultOK", true); //성공
                    returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("bkmkId", "");
            returnMap.put("userId", paramData.get("userId"));
            returnMap.put("resultOK", false); //성공
            returnMap.put("resultMsg", "실패");
        }

        paramData.remove("id");

        return returnMap;
    }


    /**
     * (북마크).북마크 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object tchMdulBmkDelete(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //List<Map<String, Object>> bkmkIdList = (List<Map<String, Object>>) paramData.get("bkmkId");

//        paramData.put("bkmkIdList", bkmkIdList);
        //int result1 = tchBmkMapper.deleteTagInfo(paramData);
        //log.info("result1:{}", result1);

        int result2 = tchBmkMapper.deleteBkmkTagMapng(paramData);
        log.info("result2:{}", result2);

        int result3 = tchBmkMapper.deleteBkmkInfo(paramData);
        log.info("result3:{}", result3);

        returnMap.put("userId", paramData.get("userId"));
        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }


    /**
     * (북마크).북마크 등록
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object tchMdulBmkTagSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //kbmkTagList
        List<String> findBmkShareForm = Arrays.asList("tabInfoId", "textbkId");
        List<String> findTagInfoForm = Arrays.asList("tagInfoId");

        //tag_info의 id 조회
        LinkedHashMap<Object, Object> findBkmkMap = AidtCommonUtil.filterToMap(findBmkShareForm, tchBmkMapper.findBmkShare(paramData));

        paramData.put("textbkId", findBkmkMap.get("textbkId"));

        //1. insert tag_info
        int insertBmkTagSaveRslt = tchBmkMapper.insertBmkTagSave(paramData);

        //success
        if(insertBmkTagSaveRslt > 0) {
            //2. insert bkmk_tag_mapng
            //tag_info의 ID가 생성되면 그걸 불러와서 bkmk_tag_mapng에 insert 함
            LinkedHashMap<Object, Object> findTagInfoMap = AidtCommonUtil.filterToMap(findTagInfoForm, tchBmkMapper.findTagInfo(paramData));
            paramData.put("tagInfoId", findTagInfoMap.get("tagInfoId")); //bkmk_tag_mapng에 tag_info의 id insert
            int insertTagInfoRslt = tchBmkMapper.insertBkmkTagMapng(paramData);
            if(insertTagInfoRslt < 1) {
                returnMap.put("resultOK", false); //bkmk_tag_mapng insert 실패
                returnMap.put("resultMsg", "실패");
                return returnMap;
            }
        } else {
            returnMap.put("resultOK", false); //tag_info insert 실패
            returnMap.put("resultMsg", "실패");
            return returnMap;
        }

        returnMap.put("resultOK", true); //성공
        returnMap.put("resultMsg", "성공");
        return returnMap;
    }

    /**
     * (북마크).북마크 태그 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object tchMdulBmkTagModify(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //kbmkTagList
        List<String> findTagInfoForm = Arrays.asList("tagId", "tagNm", "bassTagAt");

        int result1 = tchBmkMapper.updateTagInfo(paramData);
        log.info("result1:{}", result1);

        returnMap = AidtCommonUtil.filterToMap(findTagInfoForm, tchBmkMapper.findTagInfoById(paramData));

        return returnMap;
    }

    /**
     * (북마크).북마크 태그 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object tchMdulBmkTagDelete(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("userId", paramData.get("userId"));

        int result1 = tchBmkMapper.deleteBkmkTagMapngBybkmkIdByTagId(paramData);
        log.info("result1:{}", result1);

        //heum. 북마크에 매핑되어 있는 태그를 끊어 내는 프로그램으로 아래 로직 삭제
        //int insertBmkTagSaveRslt = tchBmkMapper.deleteTagInfoByTagId(paramData);
        //log.info("insertBmkTagSaveRslt:{}", insertBmkTagSaveRslt);

        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }

    public Object createTchMdulBmkTagSave(Map<String, Object> paramData) throws Exception {

        List<String> tagInfoItem = Arrays.asList("tagId", "textbkId", "tagNm", "clorNum", "bassTagAt");

        Map<String, Object> tagInfoMap = tchBmkMapper.findTagInfoById(paramData);

        int result1 = 0;
        if (ObjectUtils.isEmpty(tagInfoMap)) {
            result1 = tchBmkMapper.createTchMdulBmkTagSave(paramData);
            log.info("result1:{}", result1);
        } else {
            paramData.put("id", paramData.get("tagId"));
            result1 = 1;
        }

        int result2 = tchBmkMapper.createTchMdulBmkTagMappingSave(paramData);
        log.info("result2:{}", result2);

        var returnMap = new LinkedHashMap<>();
        if (result1 > 0 && result2 > 0) {
            returnMap.put("tagId", MapUtils.getInteger(paramData, "id"));
            returnMap.put("tagNm", MapUtils.getString(paramData, "tagNm"));
            returnMap.put("clorNum", MapUtils.getInteger(paramData, "clorNum"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            paramData.remove("id");
        } else {
            returnMap.put("tagId", null);
            returnMap.put("tagNm", null);
            returnMap.put("clorNum", null);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        //return AidtCommonUtil.filterToMap(tagInfoItem, tchBmkMapper.findTagSaveTagInfo(paramData));
        return returnMap;
    }

    public Object createTchMdulBmkTagTagsave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        String tagNm = MapUtils.getString(paramData, "tagNm", "");
        int maxLength = 100; // DB 컬럼의 실제 길이
        log.info("tagNm:{}", tagNm);

        if (tagNm.length() > maxLength) {
            log.warn("tag_nm exceeds max length. Input length: {}, Max: {}", tagNm.length(), maxLength);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "태그명은 " + maxLength + "자 이내로 입력해주세요.");
            return returnMap;
        }

        int result1 = tchBmkMapper.createTchMdulBmkTagTagsave(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("tagId", MapUtils.getInteger(paramData, "id"));
            returnMap.put("tagNm", MapUtils.getString(paramData, "tagNm"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            paramData.remove("id");
        } else {
            returnMap.put("tagId", null);
            returnMap.put("tagNm", null);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object tchMdulBmkTagTagdel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result2 = tchBmkMapper.deleteBkmkTagMapng2(paramData);
        log.info("result2:{}", result2);

        int result3 = tchBmkMapper.deleteTagInfo2(paramData);
        log.info("result3:{}", result3);

        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "성공");
        return returnMap;
    }


    public Object tchMdulBmkTagTagmod(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //kbmkTagList
        //List<String> findTagInfoForm = Arrays.asList("tagId", "tagNm", "bassTagAt");
        List<String> findTagInfoForm = Arrays.asList("tagId", "tagNm");

        int result1 = tchBmkMapper.updateTagInfo(paramData);
        log.info("result1:{}", result1);

        Map<Object, Object> resultMap =AidtCommonUtil.filterToMap(findTagInfoForm, tchBmkMapper.findTagInfoById(paramData));
        if(MapUtils.isEmpty(resultMap)) {
            returnMap.put("tagId", paramData.get("tagId"));
            returnMap.put("tagNm", paramData.get("tagNm"));
            //returnMap.put("bassTagAt", paramData.get("bassTagAt"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        } else {
            returnMap.put("tagId", resultMap.get("tagId"));
            returnMap.put("tagNm", resultMap.get("tagNm"));
            //returnMap.put("bassTagAt", resultMap.get("bassTagAt"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchMdulBmkInfo(Map<String, Object> paramData, String uri) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        Map<String, Object> tagInfoMap = tchBmkMapper.findTchMdulBmkInfo(paramData);

        String bmkYn = "N";
        if (ObjectUtils.isNotEmpty(tagInfoMap)) {
            bmkYn = "Y";
            returnMap.put("bkmkId", MapUtils.getInteger(tagInfoMap, "id"));
        } else {
            returnMap.put("bkmkId", "");
        }

        returnMap.put("bmkYn", bmkYn);

        /*
        String tchBmkYn = "N";
        if ("/stnt/mdul/bmk/info".equals(uri)) {
            Map<String, Object> tagInfoMapStnt = tchBmkMapper.findStntMdulBmkInfo(paramData);

            if (tagInfoMapStnt != null && !tagInfoMapStnt.isEmpty()) {
                tchBmkYn = "Y";
            }
            returnMap.put("tchBmkYn", tchBmkYn);
        }

         */

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        /*
        if ("Y".equals(bmkYn)) {

        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        */

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchMdulBmkTagList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> tagItem = Arrays.asList("tagId", "textbkId", "tagNm", "clorNum", "bassTagAt");

        Map<String, Object> currClorNumMap = tchBmkMapper.findTchMdulBmkTagList_currClorNum(paramData);

        returnMap.put("currClorNum", MapUtils.getInteger(currClorNumMap, "clorNum"));
        returnMap.put("TagList", AidtCommonUtil.filterToList(tagItem, tchBmkMapper.findTchMdulBmkTagList_tagList(paramData)));
        return returnMap;
    }

}
