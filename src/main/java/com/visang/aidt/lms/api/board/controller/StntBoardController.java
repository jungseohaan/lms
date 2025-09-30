package com.visang.aidt.lms.api.board.controller;

import com.visang.aidt.lms.api.board.service.StntBoardService;
import com.visang.aidt.lms.api.board.service.TchBoardService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
//@Api(tags = "게시판(과제출제)")
@Tag(name = "(학생)게시판", description = "(학생)게시판")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntBoardController {

    private TchBoardService tchBoardService;
    private StntBoardService stntBoardService;

    //(학생)진행중인과제목록조회 - TchBoardController

    @Loggable
    @RequestMapping(value = "/stnt/board/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생)전체과제목록조회", description = "(학생)전체과제목록조회")
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "userID", description = "학생ID", required = true, schema = @Schema(type = "string", example = "4b5287a5-3b4e-5dca-a75b-e3e13d9f245a"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    @Parameter(name = "keyword", description = "과제검색어", required = false, schema = @Schema(type = "string", example = "평가"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> getBbsList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntBoardService.selectStntBbsList(paramData, pageable);
        String resultMessage = "(학생)전체과제목록조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/stnt/board/detail", method = {RequestMethod.GET})
    @Operation(summary = "(학생)과제상세조회", description = "(학생)과제상세조회")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "bbsTyCode", description = "게시판유형코드", required = true, schema = @Schema(type = "string", example = "4"))
    @Parameter(name = "userID", description = "학생ID", required = true, schema = @Schema(type = "string", example = "4b5287a5-3b4e-5dca-a75b-e3e13d9f245a"))
    public ResponseDTO<CustomBody> getBbsDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = stntBoardService.selectStntBbsDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)과제상세조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/board/new-note/pre", method = {RequestMethod.GET})
    @Operation(summary = "(학생)새게시글작성진입", description = "(학생)새게시글작성진입")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "4G100000214_2025_10440003"))
    @Parameter(name = "nttId", description = "과제ID", required = true, schema = @Schema(type = "string", example = "5"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "4b5287a5-3b4e-5dca-a75b-e3e13d9f245a"))
    public ResponseDTO<CustomBody> clickTchBoardNewNote(
            @RequestParam Map<String, Object> paramData
    )throws Exception {
        Map<Object, Object> resultData = stntBoardService.clickStntBoardNewNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)새게시글작성");
    }

    @Operation(summary = "(학생)파일업로드", description = "NCP Object Storage Multi File Upload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "return http status 200 with url list"),
    })
    @PostMapping(path = "/stnt/board/new-note/fileUpload", consumes = {"multipart/form-data"}, produces = {"application/json"})
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "40337f78-8a70-5513-b251-11e1ab673541"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "nttId", description = "게시판 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> uploadTchBbsTaskFile(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            HttpServletRequest request
    ) throws Exception {

        Map<String, Object> resultData = tchBoardService.uploadTchBbsFiles(paramData, files, request);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)파일업로드");
    }

    //(학생).과제저장하기
    @Loggable
    @RequestMapping(value = "/stnt/board/new-note/save", method = {RequestMethod.POST})
    @Operation(summary = "(학생)과제저장하기", description = "(학생)과제저장하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"bbsId\":\"1\"," +
                            "\"stntNttId\":\"1\"," +
                            "\"stntNttSj\":\"테스트제목1\"," +
                            "\"nttCn\":\"테스트내용1\"," +
                            "\"atchId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntBoardNewNoteSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntBoardService.stntBoardNewNoteSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)과제저장하기");
    }

    //(학생).과제삭제하기
//    @Loggable
//    @RequestMapping(value = "/stnt/board/new-note/del", method = {RequestMethod.POST})
//    @Operation(summary = "(학생)과제삭제하기", description = "(학생)과제삭제하기")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(examples = {
//                    @ExampleObject(name = "파라미터", value = "{" +
//                            "\"userId\":\"40337f78-8a70-5513-b251-11e1ab673541\"," +
//                            "\"claId\":\"4G100000214_2025_10440003\"," +
//                            "\"bbsId\":\"1\"," +
//                            "\"nttId\":\"1\"," +
//                            "\"atchId\":\"1\"" +
//                            "}"
//                    )
//            }
//            ))
//    public ResponseDTO<CustomBody> stntBoardNewNoteDel(
//            @RequestBody Map<String, Object> paramData
//    )throws Exception {
//
//        Map<String, Object> resultData = stntBoardService.stntBoardNewNoteDel(paramData);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)과제저장하기");
//    }




}
