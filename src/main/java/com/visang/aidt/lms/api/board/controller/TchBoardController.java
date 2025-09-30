package com.visang.aidt.lms.api.board.controller;

import com.visang.aidt.lms.api.board.service.TchBoardService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
//@Api(tags = "게시판(과제출제)")
@Tag(name = "(교사)게시판", description = "(교사)게시판")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchBoardController {

    private TchBoardService tchBoardService;

    @Loggable
    @RequestMapping(value = {"/tch/board/progress/list","/stnt/board/progress/list"}, method = {RequestMethod.GET})
    @Operation(summary = "(교사)진행중인과제목록조회", description = "(교사)진행중인과제목록조회")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    public ResponseDTO<CustomBody> getBbsProgressList(
            @RequestParam Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.selectTchBbsProgressList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)진행중인과제목록조회");
    }

//    @Loggable
//    @RequestMapping(value = {"/tch/board/progress/list","/stnt/board/progress/list"}, method = {RequestMethod.GET})
//    @Operation(summary = "(교사)진행중인과제목록조회", description = "(교사)진행중인과제목록조회")
//    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
//    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
//    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
//    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
//    public ResponseDTO<CustomBody> getBbsProgressList(
//            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
//    ) throws Exception  {
//        Object resultData = tchBoardService.selectTchBbsProgressList(paramData);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)진행중인과제목록조회");
//
//    }

    @Loggable
    @RequestMapping(value = "/tch/board/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사)전체과제목록조회", description = "(교사)전체과제목록조회")
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    @Parameter(name = "keyword", description = "과제검색어", required = false, schema = @Schema(type = "string", example = "평가"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> getBbsList(
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "bbsTyCode", defaultValue = "") String bbsTyCode,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchBoardService.selectTchBbsList(paramData, pageable);
        String resultMessage = "(교사)전체과제목록조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/board/detail", method = {RequestMethod.GET})
    @Operation(summary = "(교사)과제상세조회", description = "(교사)과제상세조회")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    @Parameter(name = "nttId", description = "과제번호", required = true, schema = @Schema(type = "string", example = "4"))
    public ResponseDTO<CustomBody> getBbsDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = tchBoardService.selectTchBbsDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)과제상세조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/board/new-note/pre", method = {RequestMethod.GET})
    @Operation(summary = "(교사)새게시글작성진입", description = "(교사)새게시글작성진입")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    public ResponseDTO<CustomBody> clickTchBoardNewNote(
            @RequestParam Map<String, Object> paramData,
            HttpServletRequest request
    )throws Exception {
        if (Objects.equals(request.getAttribute("auth.userId"), paramData.get("userId"))) {
            Map<String, Object> resultData = tchBoardService.clickTchBoardNewNote(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)새게시글작성");
        } else {
            throw new AidtException("userId 값이 유효하지 않습니다.");
        }
    }

    @Operation(summary = "(교사,학생)파일업로드", description = "NCP Object Storage Multi File Upload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "return http status 200 with url list"),
    })
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(path = "/tch/board/new-note/fileUpload", consumes = {"multipart/form-data"}, produces = {"application/json"})
    @Parameter(name = "claId", description = "클래스 ID", required = false, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "atchId", description = "첨부ID", required = false, schema = @Schema(type = "int", example = "5"))
    public ResponseDTO<CustomBody> uploadTchBbsTaskFile(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            HttpServletRequest request
    ) throws Exception {

        Map<String, Object> resultData = tchBoardService.uploadTchBbsFiles(paramData, files, request);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사,학생)파일업로드");
    }

    @Loggable
    @RequestMapping(value = "/tch/board/new-note/fileDelete", method = {RequestMethod.POST})
    @Operation(summary = "(교사)파일삭제", description = "(교사)파일삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =  "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"atchId\":\"1\"," +
                            "\"files\":[{\"fileId\":\"1\"}," +
                            "{\"fileId\":\"2\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteTchBbsTaskFile(
            @RequestBody Map<String, Object> paramData,
            HttpServletRequest request
    )throws Exception {
        Object resultData = tchBoardService.deleteTchBbsTaskFile(paramData, request.getHeader("Authorization").toString());
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)파일삭제");
    }

    @Loggable
    @GetMapping(path = "/tch/board/new-note/fileDown", produces = {"application/octet-stream"})
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "fileId", description = "파일ID", required = true, schema = @Schema(type = "int", example = "14"))
    @Operation(summary = "(교사)파일다운", description = "(교사)파일다운")
    public ResponseEntity<Object> downTchBbsFiles(
            @RequestParam Map<String, Object> paramData
    )throws Exception {
        return tchBoardService.downTchBbsFile(paramData);
    }

    //(교사).과제저장하기
    @Loggable
    @RequestMapping(value = "/tch/board/new-note/save", method = {RequestMethod.POST})
    @Operation(summary = "(교사)과제저장하기", description = "(교사)과제저장하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"bbsId\":\"1\"," +
                            "\"nttSj\":\"테스트제목1\"," +
                            "\"nttCn\":\"테스트내용1\"," +
                            "\"ntceBgnde\":\"2025-03-26 05:09:22\"," +
                            "\"ntceEndde\":\"2025-04-02 05:09:22\"," +
                            "\"atchId\":\"1\"," +
                            "\"stntList\":[{\"stdtId\":\"221493b7-2b27-5cbc-aded-ceae6c89f5c2\"}," +
                            "{\"stdtId\":\"74fc2f5d-57e1-5075-bf81-67831e8cad40\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchBoardNewNoteSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.tchBoardNewNoteSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)새게시글작성진입");
    }

    @Loggable
    @RequestMapping(value = "/tch/board/new-note/edit/pre", method = {RequestMethod.GET})
    @Operation(summary = "(교사)게시글수정작성진입", description = "(교사)게시글수정작성진입")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "nttId", description = "과제ID", required = true, schema = @Schema(type = "int", example = "5"))
    public ResponseDTO<CustomBody> clickTchBoardNewNoteEdit(
            @RequestParam Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.clickTchBoardNewNoteEdit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)게시글수정작성진입");
    }

    //(교사).과제수정사항저장
    @Loggable
    @RequestMapping(value = "/tch/board/new-note/update", method = {RequestMethod.POST})
    @Operation(summary = "(교사)과제수정사항저장", description = "(교사)과제수정사항저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"nttId\":\"1\"," +
                            "\"nttSj\":\"테스트제목1\"," +
                            "\"nttCn\":\"테스트내용1\"," +
                            "\"ntceBgnde\":\"2025-03-27 05:09:23\"," +
                            "\"ntceEndde\":\"2025-04-03 05:09:23\"," +
                            "\"atchId\":\"1\"," +
                            "\"stntList\":[{\"stdtId\":\"221493b7-2b27-5cbc-aded-ceae6c89f5c2\"}," +
                           "{\"stdtId\":\"74fc2f5d-57e1-5075-bf81-67831e8cad40\"}]" +
                           "{\"stdtId\":\"221493b7-2b27-5cbc-aded-ceae6c89f5c2\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchBoardNewNoteEditSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.tchBoardNewNoteEditSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)과제수정사항저장");
    }

    //(교사).게시물조회수카운트
    @Loggable
    @RequestMapping(value = "/tch/board/note/cnt", method = {RequestMethod.POST})
    @Operation(summary = "(교사)게시물조회수카운트", description = "(교사)게시물조회수카운트")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"nttId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> updateTchClaBbsCnt(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.tchBoardNewNoteSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)게시물조회수카운트");
    }

    //(교사).과제종료하기
    @Loggable
    @RequestMapping(value = "/tch/board/bbs/end", method = {RequestMethod.POST})
    @Operation(summary = "(교사)과제종료하기", description = "(교사)과제종료하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"nttId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchBoardBbsEnd(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.tchBoardBbsEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)과제삭제");
    }


    //(교사).과제삭제
    @Loggable
    @RequestMapping(value = "/tch/board/bbs/del", method = {RequestMethod.POST})
    @Operation(summary = "(교사)과제삭제", description = "(교사)과제삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"nttId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchBoardBbsDel(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchBoardService.tchBoardBbsDel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)과제삭제");
    }

}
