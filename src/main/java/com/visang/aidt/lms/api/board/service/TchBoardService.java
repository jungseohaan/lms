package com.visang.aidt.lms.api.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.board.mapper.TchBoardMapper;
import com.visang.aidt.lms.api.library.dao.FileDao;
import com.visang.aidt.lms.api.library.dto.FileDto;
import com.visang.aidt.lms.api.library.dto.FileLogDto;
import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.utility.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchBoardService {
    private final TchBoardMapper tchBoardMapper;
    private final FileService fileService;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    private long MAX_FILE_SIZE = 1000 * 1024 * 1024; // 1000mb
    private final FileDao fileDao;

    @Value("${key.salt.main}")
    private String keySaltMain;


    /**
     * (교사).진행중인과제목록조회
     *
     * @param paramData 입력 파라메터
     * @return List
     */
    public Map<String, Object> selectTchBbsProgressList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> tchBbsInProgressItem = Arrays.asList("nttId", "nttSj");
        //게시판유형코드 bbs_ty_code : 4


        //과제마스터테이블 조회 - 없으면 생성
        if(MapUtils.isEmpty(tchBoardMapper.selectTchBbsMaster(paramData))) {
            tchBoardMapper.insertTchClaBbsMasterPreEnter(paramData);
        }

        Map<String,Object> userInfo = tchBoardMapper.selectUserInfo(paramData);

        paramData.put("userSeCd", String.valueOf(userInfo.get("userSeCd")));

        //진행중인과제 목록 조회
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(tchBbsInProgressItem, tchBoardMapper.selectTchBbsProgressList(paramData));
        returnMap.put("resultList", resultList);

        return returnMap;
    }

    /**
     * (교사) 전체과제목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchBbsList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        // Response Parameters
        List<String> tchBbsItem = Arrays.asList(
            "no", "bbsId", "claId","textbkId", "wrterId", "progressAt", "nttId","nttSj", "ntceBgnde", "ntceEndde", "totalNum", "submitNum");

        List<Map> evalList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = tchBoardMapper.selectTchBbsListAll(pagingParam);
        //select
        List<Map> resultList = new ArrayList<>();
        List<LinkedHashMap<Object, Object>> tchBbsList = new ArrayList<>();

        if(!entityList.isEmpty()) {
            total = Long.valueOf(entityList.get(0).get("fullCount").toString());
            tchBbsList = AidtCommonUtil.filterToList(tchBbsItem, entityList);
            for(LinkedHashMap temp : tchBbsList) {
                resultList.add(temp);
            }
        }

        //code_gb_cd - task_stts_cd(과제상태 1:예정, 2:진행 중, 3:완료)
        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("tchBbsList",tchBbsList);
        returnMap.put("page",page);
        return returnMap;
    }

    /**
     * (교사).과제상세조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Object selectTchBbsDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //과제상세
        List<String> tchBbsDetailForm = Arrays.asList("nttSj", "nttCn", "ntceBgnde", "ntceEndde", "progressAt", "totalNum", "submitNum", "notSubmitNum", "rgtrDt");
        //선생님첨부파일내용
        List<String> tchBbsAtchForm = Arrays.asList("atchId", "fileId", "fileSn", "fileNm", "filePath");
        //학생현황
        List<String> stntBbsAtchForm = Arrays.asList("stdtId", "num", "submAt", "submDt", "mdfyDt", "files");

        LinkedHashMap<Object, Object> tchBbsDetail = AidtCommonUtil.filterToMap(tchBbsDetailForm, tchBoardMapper.selectTchBbsDetail(paramData));

        if (tchBbsDetail.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("error", "유효하지 않은 과제 ID 입니다.");

            return returnMap;
        }
        List<LinkedHashMap<Object, Object>> tchBbsAtchList = AidtCommonUtil.filterToList(tchBbsAtchForm, tchBoardMapper.selectTchBbsTchFileList(paramData));
        List<LinkedHashMap<Object, Object>> stntBbsAtchList = AidtCommonUtil.filterToList(stntBbsAtchForm, tchBoardMapper.selectTchBbsStntFileList(paramData));

        if(ObjectUtils.isNotEmpty(stntBbsAtchList)) {
            for(int i=0;i<stntBbsAtchList.size(); i++) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Object tempObj = objectMapper.readValue(stntBbsAtchList.get(i).get("files").toString(), Object.class);

                    List<?> tempList = (List<?>) tempObj;

                    //첫번째리스트의 fileId값이 null이면 List에 null을 담아 보냄. String json List를 Object로 변환하면서 생긴문제.
                    List result = new ArrayList();
                    if (!tempList.isEmpty() && tempList.get(0) instanceof Map) {
                        Map<?, ?> firstItem = (Map<?, ?>) tempList.get(0);
                        if (firstItem.get("fileId") == null) {

                            stntBbsAtchList.get(i).put("files", result);
                        } else {
                            stntBbsAtchList.get(i).put("files", tempObj);
                        }
                    } else {
                        stntBbsAtchList.get(i).put("files", result);
                    }
                } catch(com.fasterxml.jackson.core.JsonProcessingException e) {
                    log.error("JSON 파싱 오류: {}", CustomLokiLog.errorLog(e));
                } catch(ClassCastException e) {
                    log.error("타입 캐스팅 오류: {}", CustomLokiLog.errorLog(e));
                } catch(Exception e) {
                    log.error("파일 정보 처리 중 오류: {}", CustomLokiLog.errorLog(e));
                }
            }
        }

        returnMap.put("taskInfo", tchBbsDetail);        //과제상세
        returnMap.put("tchFileList", tchBbsAtchList);   //선생님첨부파일내용
        returnMap.put("stntTaskList", stntBbsAtchList); //학생현황

        return returnMap;
    }

    /**
     * (교사).새게시글작성진입
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> clickTchBoardNewNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        //aidt_lms.cla_bbs ntt_id 시퀀스따기(교사용)
        //use_at : 사용여부 -> I : 작성중으로 insert
//        int resultCnt = tchBoardMapper.insertTchClaBbsPreEnter(paramData);
//
//        if(resultCnt>0) {
//            returnMap.put("bbsId", paramData.get("bbsId"));
//            returnMap.put("resultOk", true);
//            System.err.println("생성된 BBS ID: " + paramData.get("bbsId"));
//        }
//
//        paramData.remove("bbsId");

        //bbsId조회
        Map<String ,Object> bbsMap = tchBoardMapper.selectTchBbsMaster(paramData);
        if(MapUtils.isEmpty(bbsMap)) {
            returnMap.put("bbsId", "");
        } else {
            returnMap.put("bbsId", bbsMap.get("bbsId"));
        }

        //학생리스트 조회하여 보내주기
        List<String> stntForm = Arrays.asList("stdtId");
        List<LinkedHashMap<Object, Object>> stntList = AidtCommonUtil.filterToList(stntForm, tchBoardMapper.selectClsStntList(paramData));
        returnMap.put("resultOk", true);
        returnMap.put("stntList", stntList);
        return returnMap;
    }


    /**
     * (교사).과제저장하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchBoardNewNoteSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        boolean resultOk = false;
        String errorMsg = "";
        try {
            /* atch_id로 상세파일에 파일이 몇개 있는지 확인 후 file_sn 설정 */
            List<Map> selClaAtchDtl = tchBoardMapper.selectClaAtchDetail(paramData);

            //상세파일이 존재하면 fileSN 업데이트
            int updateClaAtchY = 0;
            if(selClaAtchDtl.size() > 0) {
                int fileSn = 1;
                for(Map<String, Object> temp : selClaAtchDtl) {
                    Map<String, Object> tempParam =new HashMap<>();
                    tempParam.put("fileId", temp.get("fileId").toString());
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

            /* bbs테이블에 과제정보 insert하기 */
            int tchClaBbs = tchBoardMapper.insertTchClaBbs(paramData);

            //학생리스트  cla_bbs_stnt에 저장 -> 테이블생성
            String stntListObject = paramData.get("stntList").toString();
            List<Map<String, Object>> stntList = AidtCommonUtil.objectStringToListMap(stntListObject);

            if(!stntList.isEmpty()) {
                paramData.put("stntList", stntList);
                int insertStntTask = tchBoardMapper.insertClaBbsStntList(paramData);
            }

            returnMap.put("nttId", paramData.get("nttId"));
            resultOk = true;
        } catch (DataAccessException e) {
            errorMsg = "Database access error in tchBoardNewNoteSave: " + CustomLokiLog.errorLog(e);
            log.error("Database access error in tchBoardNewNoteSave: {}", CustomLokiLog.errorLog(e));
            tchBoardMapper.delTchBoardBbs(paramData);
            tchBoardMapper.deleteStnt(paramData);
        } catch (IllegalArgumentException e) {
            errorMsg = "Illegal argument in tchBoardNewNoteSave: {}: " + CustomLokiLog.errorLog(e);
            log.error("Illegal argument in tchBoardNewNoteSave: {}", CustomLokiLog.errorLog(e));
            tchBoardMapper.delTchBoardBbs(paramData);
            tchBoardMapper.deleteStnt(paramData);
        } catch (NullPointerException e) {
            errorMsg = "Null pointer exception in tchBoardNewNoteSave: " + CustomLokiLog.errorLog(e);
            log.error("Null pointer exception in tchBoardNewNoteSave: {}", CustomLokiLog.errorLog(e));
            tchBoardMapper.delTchBoardBbs(paramData);
            tchBoardMapper.deleteStnt(paramData);
        } catch (Exception e) {
            errorMsg = "Unexpected error in tchBoardNewNoteSave: " + CustomLokiLog.errorLog(e);
            log.error("Unexpected error in tchBoardNewNoteSave: {}", CustomLokiLog.errorLog(e));
            tchBoardMapper.delTchBoardBbs(paramData);
            tchBoardMapper.deleteStnt(paramData);
        } finally {
            if(!resultOk) {
                returnMap.put("errorMsg", errorMsg);
            }
            paramData.remove("nttId");
            paramData.remove("stntList");
            returnMap.put("resultOk", resultOk);
        }

        return returnMap;
    }

    /**
     * (교사).과제수정진입
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> clickTchBoardNewNoteEdit(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        boolean resultOk = false;

        //aidt_lms.cla_bbs ntt_id 시퀀스따기(교사용)
        //use_at : 사용여부 -> I : 작성중으로 insert
//        int resultCnt = tchBoardMapper.insertTchClaBbsPreEnter(paramData);
//
//        if(resultCnt>0) {
//            returnMap.put("bbsId", paramData.get("bbsId"));
//            returnMap.put("resultOk", true);
//            System.err.println("생성된 BBS ID: " + paramData.get("bbsId"));
//        }
//
//        paramData.remove("bbsId");

        try {
            //과제제목, 과제내용, 파일첨부내용 보내주기
            List<String> nttItem = Arrays.asList("nttSj", "nttCn", "ntceBgnde", "ntceEndde", "atchId", "files");
            LinkedHashMap<Object, Object> nttMap = AidtCommonUtil.filterToMap(nttItem, tchBoardMapper.selectTchBbsInfTcnFileInfo(paramData));

            //jsonString to jsonObject
            if (ObjectUtils.isNotEmpty(nttMap)) {
                ObjectMapper objectMapper = new ObjectMapper();
                resultOk = true;
                if (nttMap.get("files") != null) {
                    try {
                        Object tempObj = objectMapper.readValue(nttMap.get("files").toString(), Object.class);
                        nttMap.put("files", tempObj);
                    } catch (DataAccessException e) {
                        log.error("Database access error in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
                    } catch (IllegalArgumentException e) {
                        log.error("Illegal argument in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
                    } catch (NullPointerException e) {
                        log.error("Null pointer exception in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
                    } catch (Exception e) {
                        log.error("Unexpected error in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
                    }
                }
            }


            //파일첨부내용
            List<String> tchBbsAtchFileForm = Arrays.asList("fileId", "fileSn", "fileNm", "filePath");
            List<LinkedHashMap<Object, Object>> tchBbsAtchList = AidtCommonUtil.filterToList(tchBbsAtchFileForm, tchBoardMapper.selectTchBbsInfTcnFileList(paramData));

            nttMap.put("tchFileList", tchBbsAtchList);

            //학생리스트 조회하여 보내주기
            List<String> stntForm = Arrays.asList("stdtId", "stdtTrgt");
            List<LinkedHashMap<Object, Object>> stntList = AidtCommonUtil.filterToList(stntForm, tchBoardMapper.selectClsStntSltList(paramData));

            returnMap.put("resultOk", resultOk);
            returnMap.put("nttInfo", nttMap);
            returnMap.put("stntList", stntList);
        } catch (DataAccessException e) {
            log.error("Database access error in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("error", "데이터베이스 조회 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("Unexpected error in clickTchBoardNewNoteEdit: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("error", "처리 중 오류가 발생했습니다.");
        }
        return returnMap;
    }

    /**
     * (교사).과제수정사항저장하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchBoardNewNoteEditSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        /* atch_id로 상세파일에 파일이 몇개 있는지 확인 후 file_sn 다시 설정 */
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

        /* bbs테이블에 과제정보 update하기 */
        int tchClaBbs = tchBoardMapper.updateTchClaBbs(paramData);

        //학생리스트  cla_bbs_stnt에 저장 -> 테이블생성
        String stntListObject = paramData.get("stntList").toString();
        List<Map<String, Object>> editStntList = AidtCommonUtil.objectStringToListMap(stntListObject); //수정된학생리스트
        //기존학생리스트
        List<Map> orgStntList = tchBoardMapper.selectClaBbsStnt(paramData);


        if(editStntList.size() > 0) {
            //없어진학생 삭제
            int delStnt = tchBoardMapper.deleteStnt(paramData);

            //새로생긴학생 insert
            int insertStntTask = tchBoardMapper.insertClaBbsStntList(paramData);
        }


        returnMap.put("resultOk", true);

        return returnMap;
    }

    /**
     * (교사).게시물조회수카운트
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> updateTchClaBbsCnt(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        /* 카운트 +1 */
        int cnt = tchBoardMapper.updateTchClaBbsCnt(paramData);

        if(cnt > 0) {
            returnMap.put("resultOk", true);
        }

        return returnMap;
    }

    /**
     * (교사).과제종료
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchBoardBbsEnd(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        int cnt = tchBoardMapper.tchBoardBbsEnd(paramData);

        if(cnt > 0) {
            returnMap.put("resultOk", true);
        }

        return returnMap;
    }

    /**
     * (교사).과제삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchBoardBbsDel(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        int cnt = tchBoardMapper.delTchBoardBbs(paramData);

        if(cnt > 0) {
            returnMap.put("resultOk", true);
        }

        return returnMap;
    }

    /**
     * 파일업로드 메소드 모음 부분
     */

    /**
     * (교사).파일업로드
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> uploadTchBbsFiles(Map<String, Object> paramData, List<MultipartFile> files, HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //heum
        boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String filePath = "";

		if (isWin) {
			filePath = "C:/filetest" + File.separator + paramData.get("userId") + File.separator + LocalDate.now().format(formatter); //로컬용
		} else {
			filePath = nasPath + paramData.get("userId") + File.separator + LocalDate.now().format(formatter); //서버용
		}
        Map<Object, Object> resultMap = null;
        Exception exception = null;
        try {
            //파일 업로드
            resultMap = this.uploadFile(files, filePath, paramData, request);
            if(resultMap.containsKey("e")) {
                exception = (Exception) resultMap.get("e");
                throw exception;
            }

            returnMap.put("atchId", resultMap.get("atchId"));
            returnMap.put("fileList", resultMap.get("fileList"));
        } catch (DataAccessException e) {
            log.error("Database access error in uploadTchBbsFiles: {}", CustomLokiLog.errorLog(e));
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in uploadTchBbsFiles: {}", CustomLokiLog.errorLog(e));
        } catch (NullPointerException e) {
            log.error("Null pointer exception in uploadTchBbsFiles: {}", CustomLokiLog.errorLog(e));
        } catch (Exception e) {
            log.error("Unexpected error in uploadTchBbsFiles: {}", CustomLokiLog.errorLog(e));
        } finally {
            if(resultMap != null) {
                if(exception instanceof IllegalArgumentException) {
                    returnMap.put("resultMsg", exception.getMessage());
                }
                returnMap.put("resultOk", resultMap.get("resultOk"));
                returnMap.put("resultMsg", resultMap.get("resultMsg"));
            }
        }
//
        return returnMap;
    }



    /**
     * 과제 파일 업로드
     *
     * @param files
     * @param uploadPath
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> uploadFile(List<MultipartFile> files, String uploadPath, Map<String, Object> paramData, HttpServletRequest request) throws Exception {

        //heum
        boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");

    	if (!isWin) {
            if (StringUtils.startsWith(uploadPath, "/") == false) {
                uploadPath = "/" + uploadPath;
            }
		}


        var returnMap = new LinkedHashMap<>();

        String resultMsg = "";
        File tempFile = null; // 임시 파일 저장용
        File movedFile = null; // 이동된 파일 저장용
        String userId = paramData.get("userId").toString();


        boolean resultOk = false;
//        JsonArray arr = new JsonArray();
        try {

            //aidt_lms.cla_atch에서 atch_id 채번하기
            int atchKeyCnt = 0;
            if(ObjectUtils.isEmpty(paramData.get("atchId"))) {
                atchKeyCnt = tchBoardMapper.insertClaAtchForKey(paramData);
            }

            List<Map<String, Object>> fileList = new ArrayList<>();
            int fileIdx = 0;
            // 업로드 경로 지정
            uploadPath = FileUtil.normalizeUploadPath(uploadPath);
            String tempPath = isWin
                    ? "C:/filetest" + File.separator + "temp" + File.separator
                    : nasPath + "/temp/";

            // 파일 경로 생성
            FileUtil.mkdirs(tempPath);
            FileUtil.mkdirs(uploadPath);

            for (MultipartFile file : files) {
                validateFile(file);
                String saveFileName = FileUtil.getSaveFileName(file.getOriginalFilename());
                tempFile = new File(tempPath + saveFileName);

                file.transferTo(tempFile);

                if (!FileTypeValidator.isAllowedFile(tempFile)) {
                    FileUtil.deleteFile(tempFile);
                    throw new IllegalArgumentException("허용되지 않은 파일 형식: " + file.getOriginalFilename());
                }


                String copyFile = uploadPath + "/" + saveFileName;
                movedFile = FileUtil.moveFile(tempFile, copyFile);

                paramData.put("fileStrePath", uploadPath + "/");
                paramData.put("streFileNm", saveFileName);
                paramData.put("orignlFileNm", file.getOriginalFilename());
                paramData.put("fileExtsn", FileUtil.getFileExtension(file.getOriginalFilename()));

                //aidt_lms.cla_atch_detail 파일 상세 인서트
                int claAtchDetailCnt = tchBoardMapper.insertClaAtchDetail(paramData);

                if(claAtchDetailCnt > 0) {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("fileId", paramData.get("fileId"));
                    tempMap.put("orignlFileNm", paramData.get("orignlFileNm"));
                    fileList.add(tempMap);
                    resultOk = true;
                    returnMap.put("atchId", paramData.get("atchId"));
                }
            }

            resultMsg = "파일 업로드 성공";
            returnMap.put("fileList", fileList);
        } catch (DataAccessException e) {
            log.error("Database access error in uploadFile: {}", CustomLokiLog.errorLog(e));
            resultMsg = "파일 업로드 실패: 데이터베이스 처리 중 오류가 발생했습니다.";
            log.error(resultMsg);
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
            returnMap.put("e", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in uploadFile: {}", CustomLokiLog.errorLog(e));
            resultMsg = "파일 업로드 실패: 잘못된 인수가 전달되었습니다.";
            log.error(resultMsg);
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
            returnMap.put("e", e);
        } catch (NullPointerException e) {
            log.error("Null pointer exception in uploadFile: {}", CustomLokiLog.errorLog(e));
            resultMsg = "파일 업로드 실패: 필수 객체가 null입니다.";
            log.error(resultMsg);
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
            returnMap.put("e", e);
        } catch (Exception e) {
            log.error("Unexpected error in uploadFile: {}", CustomLokiLog.errorLog(e));
            resultMsg = "파일 업로드 실패: " + e.getMessage();
            log.error(resultMsg);

            // 업로드 실패 시, 생성된 파일이 있다면 삭제
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
            returnMap.put("e", e);
        } finally {
            paramData.remove("Authorization");
            paramData.remove("fileStrePath");
            paramData.remove("streFileNm");
            paramData.remove("orignlFileNm");
            paramData.remove("fileExtsn");
            paramData.remove("fileId");
            paramData.remove("atchId");

            returnMap.put("resultOk", resultOk);
            returnMap.put("resultMsg", resultMsg);
        }

        //사용자 접근 로그 커스텀 메시지
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_MSG, arr.toString(), RequestAttributes.SCOPE_REQUEST);
//        }

        return returnMap;
    }



    /**
     * 파일 단건 다운로드
     * @return
     * @throws Exception
     */
    public ResponseEntity<Object> downTchBbsFile(Map<String, Object> paramData) {
        Map<String, String> response = new HashMap<>();

        ResponseEntity<Object> responseEntity = null;
        try {
            Map<String, Object> resultMap = tchBoardMapper.selectBbsFileDownInfo(paramData);

            if (ObjectUtils.isEmpty(resultMap)) {
                response.put("message", "파일 다운로드 실패: 파일 정보가 없습니다.");
                responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            } else {
                String fileUrl = resultMap.get("fileStrePath").toString();
                String fileName = resultMap.get("streFileNm").toString();

                String saveFileName = resultMap.get("streFileNm").toString();
                String originalFileName = resultMap.get("fileNm").toString();

                //heum
                boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");
                if (!isWin) {
                    if (!StringUtils.startsWith(fileUrl, "/")) {
                        fileUrl = "/" + fileUrl;
                    }
                } else {
                    if (StringUtils.startsWith(fileUrl, "/")) {
                        fileUrl = StringUtils.stripStart(fileUrl, "/");
                    }
                }

                String filePath = fileUrl + saveFileName;
                Path safePath = fileService.resolveSafePath(filePath); // 안전 경로 변환

                // 파일 정보
                //Path filePath = Paths.get(fileUrl).resolve(saveFileName).normalize();

                //System.out.println("####################  filePath " +filePath );
                Resource resource = new UrlResource(safePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replace("+", "%20");
                    //String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
                    String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

                    responseEntity = new ResponseEntity<>(resource, headers, HttpStatus.OK);
                } else {
                    response.put("message", "파일 다운로드 실패: 파일을 찾을 수 없습니다.");

                    responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                }
            }
        } catch (DataAccessException e) {
            log.error("Database access error in downTchBbsFile: {}", CustomLokiLog.errorLog(e));
            response.put("message", "DB 에러");
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in downTchBbsFile: {}", CustomLokiLog.errorLog(e));
            response.put("message", "잘못된 요청입니다.");
            responseEntity = ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (NullPointerException e) {
            log.error("Null pointer exception in downTchBbsFile: {}", CustomLokiLog.errorLog(e));
            response.put("message", "Null pointer exception in downTchBbsFile : " + e.getMessage());
            responseEntity = ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Unexpected error in downTchBbsFile: {}", CustomLokiLog.errorLog(e));
            response.put("message", "Unexpected error : " + e.getMessage());
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
        return responseEntity;
    }

    /**
     * 파일 다건 다운로드
     * @return
     * @throws Exception
     */
    public ResponseEntity<Object> downTchBbsFiles(Map<String, Object> paramData) throws Exception {
        Map<String, String> response = new HashMap<>();
        String userId = null;
 /*
        try {
            String fileListObject = paramData.get("files").toString();
            List<Map<String, Object>> fileList = AidtCommonUtil.objectStringToListMap(fileListObject);
            paramData.put("fileList", fileList);

            List<Map> resultList = tchBoardMapper.selectBbsFileDownList(paramData);

            for(Map<String, Object> temp : resultList) {
                String fileUrl = temp.get("fileStrePath").toString();
                String fileName = temp.get("streFileNm").toString();


                if (fileList == null) {
                    response.put("message", "파일 다운로드 실패: 파일 정보가 없습니다.");

                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                }


                // 다운로드 로그
                FileLogDto fileLogDto = setFileLogDto(fileDto, FileUtil.getRemoteIP(request), requestSource);
                fileLogDto.setUserId(userId);
                fileDao.insertDownloadLog(fileLogDto);

                String saveFileName = fileDto.getSaveFileName();
                String originalFileName = fileDto.getFileName();

                // 파일 정보
                Path filePath = Paths.get(fileUrl).resolve(saveFileName).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    // 체크섬 비교
                    String checksum = fileDto.getChecksum();
                    if (StringUtils.isEmpty(checksum)) {
                        throw new IOException("파일 다운로드 실패: checksum 정보가 누락되었습니다.");
                    }

                    String fileChecksum = null;
                    // checksum이 32자일 경우 MD5 / 64자일 경우 SHA256
                    if (checksum.length() == 32) {
                        fileChecksum = FileUtil.getMD5Checksum(filePath.toString());
                    } else {
                        fileChecksum = FileUtil.getSHA256Checksum(filePath.toString());
                    }

                    if (StringUtils.equals(checksum, fileChecksum) == false) {
                        throw new IOException("파일 다운로드 실패: 파일이 손상되었습니다.");
                    }

                    String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replace("+", "%20");
                    String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

                    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
                } else {
                    response.put("message", "파일 다운로드 실패: 파일을 찾을 수 없습니다.");

                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                }
            }
        } catch (Exception e) {
            throw e;
        }*/

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(response);
    }

    public Object deleteTchBbsTaskFile(Map<String, Object> paramData, String jwtToken) throws Exception {
        var returnMap = new LinkedHashMap<>();

//        String userId = null;
        try {
//            if (StringUtils.isEmpty(jwtToken)) {
//                log.error("JWT 토큰 값이 누락되었습니다.");
//                throw new AuthFailedException("JWT 토큰 값이 누락되었습니다.");
//            } else {
//                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
//                userId = claims.get("id", String.class);
//            }
//            if (StringUtils.isEmpty(userId)) {
//                throw new AuthFailedException("JWT 토큰 값 오류 - 사용자 정보가 없습니다.");
//            }


           // File file = new File(url);
           // boolean result = FileUtil.deleteFileReturn(file);


          //  return new ResponseEntity<>(HttpStatus.OK);

            String fileListObject = paramData.get("files").toString();
            List<Map<String, Object>> fileList = AidtCommonUtil.objectStringToListMap(fileListObject);
            paramData.put("fileList", fileList);
            tchBoardMapper.deleteClaAtchDtlFile(paramData);

            paramData.remove("fileList");
            returnMap.put("resultOK", true);

        } catch (DataAccessException e) {
            log.error("Database access error in deleteTchBbsTaskFile: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in deleteTchBbsTaskFile: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in deleteTchBbsTaskFile: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in deleteTchBbsTaskFile: {}", CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            throw e;
        }
        return returnMap;
    }

    public Map<String, Object> deleteFiles() {
        Map<String, Object> resultMap = new HashMap<>();
        List<FileDto> deletedFiles = new ArrayList<>();
        List<FileDto> failedFiles = new ArrayList<>();
        List<FileDto> fileDtoList = fileDao.selectFileInfoList();
        int count = 0;

        try {
            if (fileDtoList != null && !fileDtoList.isEmpty()) {
                for (FileDto fileDto : fileDtoList) {
                    try {


                        // 기존 로직
                        /*String filePath = fileDto.getFilePath();
                        File file = new File(filePath);
                        boolean result = FileUtil.deleteFileReturn(file);*/
                        //CSAP
                        String filePath = fileDto.getFilePath();
                        Path safePath = fileService.resolveSafePath(filePath); // 안전 경로 변환
                        boolean result = FileUtil.deleteFileReturn(safePath.toFile());

                        if (result) {
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String delTime = now.format(formatter);

                            fileDto.setDelYn("Y");
                            fileDto.setDelDt(delTime);
                            deletedFiles.add(fileDto);
                            count++;
                        } else {
                            failedFiles.add(fileDto);
                        }
                    } catch (DataAccessException e) {
                        log.error("Database access error in deleteFiles: {}", CustomLokiLog.errorLog(e));
                        failedFiles.add(fileDto);
                    } catch (IllegalArgumentException e) {
                        log.error("Illegal argument in deleteFiles: {}", CustomLokiLog.errorLog(e));
                        failedFiles.add(fileDto);
                    } catch (NullPointerException e) {
                        log.error("Null pointer exception in deleteFiles: {}", CustomLokiLog.errorLog(e));
                        failedFiles.add(fileDto);
                    } catch (Exception e) {
                        log.error("Unexpected error in deleteFiles: {}", CustomLokiLog.errorLog(e));
                        failedFiles.add(fileDto);
                    }
                }
            }
        } catch (DataAccessException e) {
            log.error("Database access error in deleteFiles: {}", CustomLokiLog.errorLog(e));
            resultMap.put("failDc", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in deleteFiles: {}", CustomLokiLog.errorLog(e));
            resultMap.put("failDc", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in deleteFiles: {}", CustomLokiLog.errorLog(e));
            resultMap.put("failDc", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in deleteFiles: {}", CustomLokiLog.errorLog(e));
            resultMap.put("failDc", e.getMessage());
            throw e;
        } finally {
            if (!deletedFiles.isEmpty()) {
                fileDao.updateFileInfoList(deletedFiles);
            }

            log.info("파일 삭제 성공 cnt : {} / 파일 삭제 실패 cnt : {}", deletedFiles.size(), failedFiles.size());
        }

        resultMap.put("resultOk", true);
        resultMap.put("btchExcnRsltCnt", count);

        return resultMap;
    }

    /**
     * 파일 객체 세팅
     *
     * @param file
     * @param saveFileName
     * @param filePath
     * @param regId
     * @param requestSource
     * @return
     */
    private FileDto setFileDto(MultipartFile file, String saveFileName, String filePath, String regId, String requestSource, String prsInfoYn) throws Exception {

        //if (StringUtils.startsWith(nasPath, "/") == false && StringUtils.startsWith(filePath, "/")) {
        filePath = StringUtils.removeStart(filePath, "/");
        //}
        if (prsInfoYn == null || "".equals(prsInfoYn)) {
            prsInfoYn = "N";
        }
        String originFileName = file.getOriginalFilename();
        String ext = FileUtil.getFileExtension(originFileName);
        FileDto fileDto = new FileDto();
        fileDto.setFileName(originFileName);
        fileDto.setSaveFileName(saveFileName);
        fileDto.setFilePath(filePath);
        fileDto.setFileExtension(ext);
        fileDto.setFileSize(file.getSize());
        fileDto.setRgtr(regId);
        fileDto.setRequestSource(requestSource);
        //String checksum = FileUtil.getSHA256Checksum(filePath + "/" + saveFileName);
        String checksum = FileUtil.getHmacSHA256Checksum(filePath + "/" + saveFileName, keySaltMain);
        fileDto.setChecksum(checksum);
        fileDto.setPrsInfoYn(prsInfoYn);

        // 파일 중복 안되도록 uuid를 붙인다 (파일 경로로 data 조회 필요 - 학생 간 파일 공유 등)
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        originFileName = StringUtils.substringBeforeLast(originFileName, ".");
        originFileName = originFileName + "(" + StringUtils.replace(uuid, "-", "") + ")." + ext;
        fileDto.setFileName(originFileName);

        /* // 파일 중복 체크 (애초에 중복 안되도록 uuid를 붙인다)
        String uuid = fileDao.selectExistsFileUuid(fileDto);
        if (StringUtils.isNotEmpty(uuid)) {
            originFileName = StringUtils.substringBeforeLast(originFileName, ".");
            originFileName = originFileName + "(" + StringUtils.replace(uuid, "-", "") + ")." + ext;
            fileDto.setFileName(originFileName);
        }*/

        return fileDto;
    }

    /**
     * 파일 로그 객체 세팅
     *
     * @param fileDto
     * @return
     */
    private FileLogDto setFileLogDto(FileDto fileDto, String accessIp, String requestSource) {
        FileLogDto fileLogDto = new FileLogDto();
        fileLogDto.setFileIdx(fileDto.getFileIdx());
        fileLogDto.setUserId(fileDto.getRgtr());
        fileLogDto.setAccessIp(accessIp);
        fileLogDto.setRequestSource(requestSource);
        if(fileDto != null) {
            fileLogDto.setFileName(fileDto.getFileName());
        }
        return fileLogDto;
    }

    /**
     * 파일 유효성 검사
     *
     * @param file
     * @throws FileNotFoundException
     */
    private void validateFile(MultipartFile file) throws FileNotFoundException {
        if (file.isEmpty()) {
            throw new FileNotFoundException("파일이 없습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
        if (!FileUtil.isAllowedExtension(file)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }
    }

}
