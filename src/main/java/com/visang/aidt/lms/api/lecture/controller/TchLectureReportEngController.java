package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.TchLectureReportEngService;
import com.visang.aidt.lms.api.lecture.service.TchLectureReportService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@RestController
@Tag(name = "(교사) 수업리포트 API [영어]", description = "(교사) 수업리포트 API [영어]")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchLectureReportEngController {
    private final TchLectureReportService tchLectureReportService;
    private final TchLectureReportEngService tchLectureReportEngService;

    @Loggable
    @RequestMapping(value = "/tch/report/lecture/result/list/eng", method = {RequestMethod.GET})
    @Operation(summary = "[영어] (교사) 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "vstea1" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "1964" ))
    public ResponseDTO<CustomBody> tchReportLectureResultList(
            @RequestParam(name = "userId",   defaultValue = "") String userId,
            @RequestParam(name = "claId",   defaultValue = "") String claId,
            @RequestParam(name = "textbkId",   defaultValue = "") int textbkId,
            @RequestParam(name = "crculId",   defaultValue = "") int crculId,
            @RequestParam(name = "tabId",   defaultValue = "0") int tabId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchLectureReportEngService.findReportLectureResultList(paramData);
            String resultMessage = "[영어] (교사) 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /* 미사용 */
//    @Loggable
//    @RequestMapping(value = "/tch/report/lecture/result/detail/mdul/eng", method = {RequestMethod.GET})
//    @Operation(summary = "[영어] (교사) 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)", description = "데이터가 없어서 검증불가. 데이터 입력 후 전체적으로 재점검 필요.")
//    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "4" ))
//    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "3131" ))
//    @Parameter(name = "dtaIemId", description = "(선택된) 모듈 ID", required = false, schema = @Schema(type = "string", example = "1" ))
//    public ResponseDTO<CustomBody> tchEngReportLectureResultDetailMdul(
//            @RequestParam(name = "crculId",   defaultValue = "0") int crculId,
//            @RequestParam(name = "tabId",   defaultValue = "0") int tabId,
//            @RequestParam(name = "dtaIemId",   defaultValue = "0") String dtaIemId,
//            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
//    ) throws Exception {
//            Object resultData = tchLectureReportEngService.findReportLectureResultDetailMdulList(paramData);
//            String resultMessage = "[영어] (교사) 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)";
//            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
//    }

}
