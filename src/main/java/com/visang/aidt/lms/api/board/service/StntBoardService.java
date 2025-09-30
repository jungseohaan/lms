package com.visang.aidt.lms.api.board.service;

import com.visang.aidt.lms.api.board.mapper.StntBoardMapper;
import com.visang.aidt.lms.api.board.mapper.TchBoardMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.String;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor

public class StntBoardService {
    private final TchBoardMapper tchBoardMapper;
    private final StntBoardMapper stntBoardMapper;

    /**
     * (학생).전체과제목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntBbsList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        // Response Parameters
        List<String> stntBbsItem = Arrays.asList(
            "no", "bbsId", "claId","textbkId", "wrterId", "progressAt", "nttId", "nttSj", "ntceBgnde", "ntceEndde", "submAt");

        List<Map> evalList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = stntBoardMapper.selectStntBbsListAll(pagingParam);
        //select
        List<Map> resultList = new ArrayList<>();
        List<LinkedHashMap<Object, Object>> stntBbsList = new ArrayList<>();

        if(!entityList.isEmpty()) {
            total = Long.valueOf(entityList.get(0).get("fullCount").toString());
            stntBbsList = AidtCommonUtil.filterToList(stntBbsItem, entityList);
            for(LinkedHashMap temp : stntBbsList) {
                resultList.add(temp);
            }
        }

        //code_gb_cd - task_stts_cd(과제상태 1:예정, 2:진행 중, 3:완료)
        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("bbsList",stntBbsList);
        returnMap.put("page",page);
        return returnMap;
    }

    /**
     * (학생).과제상세조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object selectStntBbsDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //과제상세
        List<String> tchBbsDetailForm = Arrays.asList("nttSj", "nttCn", "ntceBgnde", "ntceEndde", "progressAt");
        LinkedHashMap<Object, Object> tchBbsDetail = AidtCommonUtil.filterToMap(tchBbsDetailForm, tchBoardMapper.selectTchBbsDetail(paramData));

        //선생님첨부파일내용
        List<String> tchBbsAtchForm = Arrays.asList("fileId", "fileSn", "fileNm", "filePath");
        List<LinkedHashMap<Object, Object>> tchBbsAtchList = AidtCommonUtil.filterToList(tchBbsAtchForm, tchBoardMapper.selectTchBbsTchFileList(paramData));

        //학생과제내용
        List<String> stntBbsAtchForm = Arrays.asList("atchId","stntNttSj","stntNttCn", "submAt", "submDt");
        LinkedHashMap<Object, Object> stntBbsAtchInfo = AidtCommonUtil.filterToMap(stntBbsAtchForm,  stntBoardMapper.selectStntBbsStntFileInfo(paramData));

        List<String> stntBbsAtchFileForm = Arrays.asList("fileId", "fileSn", "fileNm", "filePath");
        List<LinkedHashMap<Object, Object>> stntBbsAtchList = AidtCommonUtil.filterToList(stntBbsAtchFileForm, stntBoardMapper.selectStntBbsStntFileList(paramData));

        stntBbsAtchInfo.put("stntFileList", stntBbsAtchList);

        tchBoardMapper.updateTchClaBbsCnt(paramData); //조회카운트+1

        tchBbsDetail.put("tchFileList", tchBbsAtchList); //선생님첨부파일내용
        tchBbsDetail.put("stntTask", stntBbsAtchInfo); //학생과제정보와 첨부파일

        return tchBbsDetail;
    }

    /**
     * (학생).새게시글작성진입
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<Object, Object> clickStntBoardNewNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //작성하던 글이 있는지 없는지 확인
        List<String> stntBbsAtchForm = Arrays.asList("nttSj", "stntNttSj", "stntNttCn", "submAt", "progressAt", "atchId");
        LinkedHashMap<Object, Object> stntBbsAtchInfo = AidtCommonUtil.filterToMap(stntBbsAtchForm,  stntBoardMapper.selectStntBbsStntFileInfo(paramData));

        List<String> stntBbsAtchFileForm = Arrays.asList("fileId", "fileSn", "fileNm", "filePath");
        List<LinkedHashMap<Object, Object>> stntBbsAtchList = AidtCommonUtil.filterToList(stntBbsAtchFileForm, stntBoardMapper.selectStntBbsStntFileList(paramData));

        stntBbsAtchInfo.put("fileList", stntBbsAtchList);

        return stntBbsAtchInfo;
    }


    /**
     * (학생).과제저장하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> stntBoardNewNoteSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);
        int claBbsCheck = tchBoardMapper.selectClaBbsCheck(paramData);
        if(claBbsCheck == 0){
            returnMap.put("resultOk", false);
            returnMap.put("resultMessage", "삭제된 평가");
            return returnMap;
        }
        /* atch_id로 상세파일에 파일이 몇개 있는지 확인 후 file_sn 설정 */
        List<Map> selClaAtchDtl = tchBoardMapper.selectClaAtchDetail(paramData);

        //상세파일이 존재하면 fileSN 업데이트
        int updateClaAtchY = 0;
        if(selClaAtchDtl.size() > 0) {
            int fileSn = 1;
            for(Map<String, Object> temp : selClaAtchDtl) {
                Map<String, Object> tempParam =new HashMap<>();
                tempParam.put("fileId", temp.get("fileId"));
                tempParam.put("fileSn", fileSn);
                tempParam.put("userId", paramData.get("userId"));
                int updateFileSn = tchBoardMapper.updateClaAtchDtlFileSn(tempParam);
                if(updateFileSn >0) {
                    fileSn++;
                }
            }

            //설정 후 첨부파일 사용변경으로 update
            updateClaAtchY = tchBoardMapper.updateClaAtchY(paramData);
        }

        /* 학생과제제출테이블인 cla_bbs_stnt는 선생님이 과제제출과 동시에 생성되기 때문에 모두 수정으로 들어간다. */
        //subm_at - Y
        //subm_dt - new
        //atch_id - up
        //mdfr, mdfy_dt update

        /* bbs테이블에 과제정보 insert하기 */
        int updateStntBbsCnt = stntBoardMapper.stntBoardBbsEnd(paramData);

        returnMap.put("resultOk", true);

        return returnMap;
    }

    /**
     * (학생).과제삭제하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
//    public Map<String, Object> stntBoardNewNoteDel(Map<String, Object> paramData) throws Exception {
//        Map<String, Object> returnMap = new HashMap<>();
//        returnMap.put("resultOk", false);
//
//        /* atch_id로 상세파일에 파일이 몇개 있는지 확인 후 file_sn 설정 */
//        List<Map> selClaAtchDtl = tchBoardMapper.selectClaAtchDetail(paramData);
//
//        //상세파일이 존재하면 fileSN 업데이트
//        int updateClaAtchY = 0;
//        if(selClaAtchDtl.size() > 0) {
//            int fileSn = 1;
//            for(Map<String, Object> temp : selClaAtchDtl) {
//                Map<String, Object> tempParam =new HashMap<>();
//                tempParam.put("fileId", temp.get("fileId"));
//                tempParam.put("fileSn", fileSn);
//                tempParam.put("userId", paramData.get("userId"));
//                int updateFileSn = tchBoardMapper.updateClaAtchDtlFileSn(tempParam);
//                if(updateFileSn >0) {
//                    fileSn++;
//                }
//            }
//
//            //설정 후 첨부파일 사용변경으로 update
//            updateClaAtchY = tchBoardMapper.updateClaAtchY(paramData);
//        }
//
//        /* 학생과제제출테이블인 cla_bbs_stnt는 선생님이 과제제출과 동시에 생성되기 때문에 모두 수정으로 들어간다. */
//        //subm_at - Y
//        //subm_dt - new
//        //atch_id - up
//        //mdfr, mdfy_dt update
//
//        /* bbs테이블에 과제정보 insert하기 */
//        int updateStntBbsCnt = stntBoardMapper.stntBoardBbsEnd(paramData);
//
//        returnMap.put("resultOk", true);
//
//        return returnMap;
//    }
}

