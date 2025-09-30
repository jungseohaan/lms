package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchLesnRscService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * (교사) 수업자료 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 수업자료 API", description = "(교사) 수업자료 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchLesnRscController {
    private final TchLesnRscService tchLesnRscService;

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 수업자료 > 수업자료 목록조회(공유 자료실)", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1201"))
    @Parameter(name = "category", description = "검색구분: 게임(game), 교과자료(textbook), 평가(question-paper), 과제(homework)", required = false, schema = @Schema(type = "string", allowableValues = {"game", "textbook", "question-paper", "homework"}, example = "textbook"))
    @Parameter(name = "keyword", description = "검색키워드", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "curriIdList", description = "교과정보", required = false, schema = @Schema(type = "string", example = "915,917,921"))
    @Parameter(name = "difyIdList", description = "난이도", required = false, schema = @Schema(type = "string", example = "MD04,MD05"))
    @Parameter(name = "creatorIdList", description = "저작자", required = false, schema = @Schema(type = "string", example = "freelancer04,cbstest8"))
    @Parameter(name = "sortGbCd", description = "정렬조건", required = false, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3", "4"}, example = "1"))
    @Parameter(name = "scrapAt", description = "스크랩 여부", required = false, schema = @Schema(type = "string", allowableValues = {"Y", "N"}, example = "N"))
    @Parameter(name = "userId", description = "교사ID", required = false, schema = @Schema(type = "string", example = "mathbook2726-t"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscList(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "curriIdList", defaultValue = "") String curriIdList,
            @RequestParam(name = "difyIdList", defaultValue = "") String difyIdList,
            @RequestParam(name = "creatorIdList", defaultValue = "") String creatorIdList,
            @RequestParam(name = "sortGbCd", defaultValue = "1") Integer sortGbCd,
            @RequestParam(name = "scrapAt", defaultValue = "") String scrapAt,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("textbkId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        paramData.put("curriIdList", AidtCommonUtil.strToLongList(curriIdList));
        paramData.put("difyIdList", AidtCommonUtil.strToStringList(difyIdList));
        paramData.put("creatorIdList", AidtCommonUtil.strToStringList(creatorIdList));

        Object resultData = tchLesnRscService.findLesnRscList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업자료 > 수업자료 목록조회(공유 자료실)");

    }
// 내자료실 사용안함.
//    @Loggable
//    @RequestMapping(value = "/tch/lesn-rsc/my/list", method = {RequestMethod.GET})
//    @Operation(summary = "(교사) 수업자료 > 수업자료 목록조회(나의 자료실)", description = "")
//    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "16"))
//    @Parameter(name = "category", description = "검색구분: 게임(game), 교과자료(textbook), 평가(question-paper), 과제(homework)", required = false, schema = @Schema(type = "string", allowableValues = {"game", "textbook", "question-paper", "homework"}, example = "textbook"))
//    @Parameter(name = "keyword", description = "검색키워드", required = false, schema = @Schema(type = "string", example = ""))
//    @Parameter(name = "curriIdList", description = "교과정보", required = false, schema = @Schema(type = "string", example = "915,917,921"))
//    @Parameter(name = "difyIdList", description = "난이도", required = false, schema = @Schema(type = "string", example = "MD04,MD05"))
//    @Parameter(name = "myuid", description = "내아이디", required = true, schema = @Schema(type = "string", example = "cbstest8"))
//    @Parameter(name = "sortGbCd", description = "정렬조건", required = false, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, example = "1"))
//    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
//    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
//    @Parameter(name = "pageable", hidden = true)
//    public ResponseDTO<CustomBody> tchLesnRscMyList(
//            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
//            @RequestParam(name = "category", defaultValue = "") String category,
//            @RequestParam(name = "keyword", defaultValue = "") String keyword,
//            @RequestParam(name = "curriIdList", defaultValue = "") String curriIdList,
//            @RequestParam(name = "difyIdList", defaultValue = "") String difyIdList,
//            @RequestParam(name = "myuid", defaultValue = "") String myuid,
//            @RequestParam(name = "sortGbCd", defaultValue = "1") Integer sortGbCd,
//            @PageableDefault(size = 10) Pageable pageable,
//            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
//    )throws Exception {
//
//        List<String> requiredParams = Arrays.asList("textbkId", "myuid");
//        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);
//
//        paramData.put("userId", paramData.get("myuid")); // 내스크랩 하트 표시를 위한 용도임. scrapAt 처리용.
//        paramData.put("curriIdList", AidtCommonUtil.strToLongList(curriIdList));
//        paramData.put("difyIdList", AidtCommonUtil.strToStringList(difyIdList));
//        paramData.put("creatorVisangYn", "N");
//
//        Object resultData = tchLesnRscService.findLesnRscList(paramData, pageable);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업자료 > 수업자료 목록조회(나의 자료실)");
//
//    }


    //@ApiOperation(value = "수업 자료 임시 저장_전체삭제_선택삭제", notes = "")
    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/info", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 수업자료 > 수업자료 상세조회", description = "")
    @Parameter(name = "setsId", description = "Sets ID", required = true, schema = @Schema(type = "string", example = "MSTG89094"))
    @Parameter(name = "userId", description = "교사ID", required = false, schema = @Schema(type = "string", example = "mathbook2726-t"))
    public ResponseDTO<CustomBody> tchLesnRscInfo(
            @RequestParam(name = "setsId") String setsId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("setsId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchLesnRscService.findLesnRscInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업자료 > 수업자료 상세조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 수업자료 > 모듈(콘텐츠) 목록 조회", description = "")
    @Parameter(name = "articleType", description = "구분: 개념(concept), 문항(question), 활동(movement)", required = false, schema = @Schema(type = "string", allowableValues = {"concept", "question", "movement"}, example = "question"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "curriIdList", description = "학습맵 정보", required = false, schema = @Schema(type = "string", example = "915, 917, 921"))
    @Parameter(name = "difyIdList", description = "난이도", required = false, schema = @Schema(type = "string", example = "MD04,MD05"))
    @Parameter(name = "creatorIdList", description = "출처(저작자)", required = false, schema = @Schema(type = "string", example = "cbstest8,inference"))
    @Parameter(name = "sortGbCd", description = "정렬 조건(1:인기순, 2:최신순, 3:스크랩순, 4:이름순)", required = false, schema = @Schema(type = "int", allowableValues = {"1", "2", "3", "4"}, example = "4"))
    @Parameter(name = "scrapAt", description = "스크랩 여부", required = false, schema = @Schema(type = "string", allowableValues = {"Y", "N"}, example = "N"))
    @Parameter(name = "userId", description = "교사 ID", required = false, schema = @Schema(type = "string", example = "mathbook2726-t"))
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "subjectAbilityList", description = "교과역량", required = false, schema = @Schema(type = "string", example = "communi,connection,inference,processing,solving"))
    @Parameter(name = "evaluationAreaList", description = "평가영역", required = false, schema = @Schema(type = "string", example = "0054,0055,0056,0057,0058"))
    @Parameter(name = "questionTypeList", description = "컨텐츠 유형", required = false, schema = @Schema(type = "string", example = "chqz,cnqz,esqz,mcqz,ocqz,oiqz,oxqz,saqz,scqz,tfqz"))
    @Parameter(name = "excRecentDay", description = "최근 출제문항 제외(일자)", required = false, schema = @Schema(type = "integer", example = "60"))
    @Parameter(name = "autoEamAt", description = "자동 출제 여부", required = false, schema = @Schema(type = "string", allowableValues = {"Y", "N"}, example = "N"))
    @Parameter(name = "eamGdExmNum", description = "출제 문항수(상)", required = false, schema = @Schema(type = "integer", example = "5" ))
    @Parameter(name = "eamAvUpExmNum", description = "출제 문항수(중상)", required = false, schema = @Schema(type = "integer", example = "5" ))
    @Parameter(name = "eamAvExmNum", description = "출제 문항수(중)", required = false, schema = @Schema(type = "integer", example = "5" ))
    @Parameter(name = "eamAvLwExmNum", description = "출제 문항수(중하)", required = false, schema = @Schema(type = "integer", example = "5" ))
    @Parameter(name = "eamBdExmNum", description = "출제 문항수(하)", required = false, schema = @Schema(type = "integer", example = "5" ))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscMdulList(
            @RequestParam(name = "articleType", defaultValue = "") String articleType,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "curriIdList", defaultValue = "") String curriIdList,
            @RequestParam(name = "difyIdList", defaultValue = "") String difyIdList,
            @RequestParam(name = "creatorIdList", defaultValue = "") String creatorIdList,
            @RequestParam(name = "sortGbCd", defaultValue = "2") int sortGbCd,
            @RequestParam(name = "scrapAt", defaultValue = "") String scrapAt,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "subjectAbilityList", defaultValue = "") String subjectAbilityList,
            @RequestParam(name = "evaluationAreaList", defaultValue = "") String evaluationAreaList,
            @RequestParam(name = "questionTypeList", defaultValue = "") String questionTypeList,
            @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        paramData.put("curriIdList", AidtCommonUtil.strToLongList(curriIdList));
        paramData.put("difyIdList", AidtCommonUtil.strToStringList(difyIdList));
        paramData.put("creatorIdList", AidtCommonUtil.strToStringList(creatorIdList));
        paramData.put("subjectAbilityList", AidtCommonUtil.strToStringList(subjectAbilityList));
        paramData.put("evaluationAreaList", AidtCommonUtil.strToStringList(evaluationAreaList));
        paramData.put("questionTypeList", AidtCommonUtil.strToStringList(questionTypeList));

        Map<String, Object> resultData = tchLesnRscService.findMdulList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업자료 > 모듈(콘텐츠) 목록 조회");

    }

    // 내자료실 사용 안함
//    @Loggable
//    @RequestMapping(value = "/tch/lesn-rsc/mdul/my/list", method = {RequestMethod.GET})
//    @Operation(summary = "(교사) 수업자료 > 모듈(콘텐츠) 나의 목록 조회", description = "")
//    @Parameter(name = "articleType", description = "구분: 개념(concept), 문항(question), 활동(movement)", required = false, schema = @Schema(type = "string", allowableValues = {"concept", "question", "movement"}, example = "question"))
//    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
//    @Parameter(name = "curriIdList", description = "학습맵 정보", required = false, schema = @Schema(type = "string", example = "915, 917, 921"))
//    @Parameter(name = "difyIdList", description = "난이도", required = false, schema = @Schema(type = "string", example = "MD04,MD05"))
//    @Parameter(name = "sortGbCd", description = "정렬 조건(1:인기순, 2:최신순, 3:스크랩순)", required = false, schema = @Schema(type = "int", allowableValues = {"1", "2", "3"}, example = "2"))
//    @Parameter(name = "scrapAt", description = "스크랩 여부", required = false, schema = @Schema(type = "string", allowableValues = {"Y", "N"}, example = "N"))
//    @Parameter(name = "myuid", description = "내 아이디", required = true, schema = @Schema(type = "string", example = "cbstest8"))
//    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "cbstest8"))
//    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "16"))
//    @Parameter(name = "subjectAbilityList", description = "교과역량", required = false, schema = @Schema(type = "string", example = "communi,connection,inference,processing,solving"))
//    @Parameter(name = "evaluationAreaList", description = "평가영역", required = false, schema = @Schema(type = "string", example = "0054,0055,0056,0057,0058"))
//    @Parameter(name = "questionTypeList", description = "컨텐츠 유형", required = false, schema = @Schema(type = "string", example = "chqz,cnqz,esqz,mcqz,ocqz,oiqz,oxqz,saqz,scqz,tfqz"))
//    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
//    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
//    @Parameter(name = "pageable", hidden = true)
//    public ResponseDTO<CustomBody> tchLesnRscMdulMyList(
//            @RequestParam(name = "articleType", defaultValue = "") String articleType,
//            @RequestParam(name = "keyword", defaultValue = "") String keyword,
//            @RequestParam(name = "curriIdList", defaultValue = "") String curriIdList,
//            @RequestParam(name = "difyIdList", defaultValue = "") String difyIdList,
//            @RequestParam(name = "sortGbCd", defaultValue = "2") int sortGbCd,
//            @RequestParam(name = "scrapAt", defaultValue = "") String scrapAt,
//            @RequestParam(name = "myuid", defaultValue = "") String myuid,
//            @RequestParam(name = "userId", defaultValue = "") String userId,
//            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
//            @RequestParam(name = "subjectAbilityList", defaultValue = "") String subjectAbilityList,
//            @RequestParam(name = "evaluationAreaList", defaultValue = "") String evaluationAreaList,
//            @RequestParam(name = "questionTypeList", defaultValue = "") String questionTypeList,
//            @PageableDefault(size = 10) Pageable pageable,
//            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
//    )throws Exception {
//
//        paramData.put("curriIdList", AidtCommonUtil.strToLongList(curriIdList));
//        paramData.put("difyIdList", AidtCommonUtil.strToStringList(difyIdList));
//        paramData.put("subjectAbilityList", AidtCommonUtil.strToStringList(subjectAbilityList));
//        paramData.put("evaluationAreaList", AidtCommonUtil.strToStringList(evaluationAreaList));
//        paramData.put("questionTypeList", AidtCommonUtil.strToStringList(questionTypeList));
//        paramData.put("creatorVisangYn", "N");
//
//        Map<String, Object> resultData = tchLesnRscService.findMdulList(paramData, pageable);
//        String resultMessage = "(교사) 수업자료 > 모듈(콘텐츠) 나의 목록 조회";
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
//
//    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul/info", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 수업자료 > 모듈(콘텐츠) 상세 조회", description = "")
    @Parameter(name = "id", description = "모듈 id", required = true, schema = @Schema(type = "string", example = "MSTG698383"))
    @Parameter(name = "userId", description = "교사 id", required = false, schema = @Schema(type = "string", example = "mathbook2726-t"))
    public ResponseDTO<CustomBody> tchLesnRscMdulInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        //TODO: 세션에서 아이디 가져오기 <--- front 에서 가져 오는것이 맞나???? - 일단 myuid 는 무시하고 id로만 갖고오게.
//        paramData.put("myuid", 2);

        Map<String, Object> resultData = tchLesnRscService.findMdulInfo(paramData);
        String resultMessage = "(교사) 수업자료 > 모듈(콘텐츠) 상세 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/scrp/save", method = {RequestMethod.POST})
    @Operation(summary = "스크랩(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"mathbook2726-t\"," +
                            "\"dtaCd\":2," +
                            "\"dtaId\":218943" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveTchLesnrscScrp(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchLesnRscService.saveTchLesnrscScrp(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스크랩(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/scrp/list", method = {RequestMethod.GET})
    @Operation(summary = "스크랩(호출)", description = "스크랩(호출)")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "mathbook2726-t"))
    public ResponseDTO<CustomBody> getTchLesnrscScrpList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.getTchLesnrscScrpList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스크랩(호출)");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/delete", method = {RequestMethod.POST})
    @Operation(summary = "수업 자료실 세트지 삭제", description = "수업 자료실 세트지 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"mathbook2726-t\"," +
                            "\"setsId\":\"MSTG89094\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteTchLesnrscSet(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.updateTchLesnrscSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 자료실 세트지 (삭제)");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul/delete", method = {RequestMethod.POST})
    @Operation(summary = "수업 자료실 모듈(콘텐츠) 삭제", description = "수업 자료실 모듈(콘텐츠) 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"mathbook2726-t\"," +
                            "\"articleId\":\"MSTG698383\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteTchLesnrscMdul(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.updateTchLesnrscMdul(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 자료실 모듈(콘텐츠) 삭제");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/exam-scope", method = {RequestMethod.GET})
    @Operation(summary = "수업 자료 조회 (출제 범위 보기)", description = "셋트지의 출제 범위를 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "setsId", description = "셋트지 ID", required = true, schema = @Schema(type = "string", example = "MSTG89094"))
    public ResponseDTO<CustomBody> findTchLesnrscExamscope(
            @RequestParam(name = "textbkId", defaultValue = "1201") int textbkId,
            @RequestParam(name = "setsId", defaultValue = "MSTG89094") String setsId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.getTchLesnrscExamscopeList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 수업 자료실 > 수업 자료 조회 (출제 범위 보기)");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul/exam-scope", method = {RequestMethod.GET})
    @Operation(summary = "콘텐츠 조회 (출제 범위 보기)", description = "콘텐츠의 출제 범위를 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "articleId", description = "모듈(아티클) ID", required = true, schema = @Schema(type = "string", example = "MSTG698384"))
    public ResponseDTO<CustomBody> findTchLesnrscMdulExamscope(
            @RequestParam(name = "textbkId", defaultValue = "1201") int textbkId,
            @RequestParam(name = "articleId", defaultValue = "MSTG698384") String articleId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.getTchLesnrscMdulExamscopeList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 수업 자료실 > 콘텐츠 조회 (카드)");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/search-filter/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 수업 자료실 > 수업 자료 목록조회 > 검색 필터", description = "난이도 / 출처 목록을 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook2726-t"))
    public ResponseDTO<CustomBody> findTchLesnrscSearchFilterInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.findTchLesnrscSearchFilterInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 수업 자료실 > 수업 자료 목록조회 > 검색 필터");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul/search-filter/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터", description = "교과역량 / 평가영역 / 콘텐츠 유형 / 난이도 / 출처 목록을 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook2726-t"))
    public ResponseDTO<CustomBody> findTchLesnrscMdulSearchFilterInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.findTchLesnrscMdulSearchFilterInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터");


    }


//    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "16"))
//    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "emaone1-t"))
//    @Parameter(name = "creatorId", description = "생성자 ID", required = true, schema = @Schema(type = "string", example = "cbstest5"))

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/bmk/create", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터 : 북마크생성", description = "출처에 대해 북마크 생성.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "textbkId": "1201",
                                "wrterId": "mathreal213-t",
                                "creatorId": "mathreal105-t"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchLesnrscMdulSearchFilterBookmark(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.createTchLesnrscMdulSearchFilterBookmark(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 검색 필터 : 북마크생성");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/bmk/delete", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터 : 북마크 삭제", description = "출처에 대해 북마크 삭제.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "textbkId": "16",
                                "wrterId": "mathreal213-t",
                                "creatorId": "mathreal105-t"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteTchLesnrscMdulSearchFilterBookmark(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.deleteTchLesnrscMdulSearchFilterBookmark(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 검색 필터 : 북마크 삭제");


    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/scrp/delete", method = {RequestMethod.POST})
    @Operation(summary = "스크랩(해제)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engedu2-t\"," +
                            "\"dtaCd\":2," +
                            "\"dtaId\":218937" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteTchLesnRscScrp(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchLesnRscService.deleteTchLesnRscScrp(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스크랩 해제");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/unit-rcmd/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 수업 자료실 > 수업자료 만들기 - 현재 생성 중인 수업자료 유형과 동일한 유형의 자료들만 조회", description = "수업자료 만들기 - 현재 생성 중인 수업자료 유형과 동일한 유형의 자료들만 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook2726-t"))
    @Parameter(name = "category", description = "구분", required = false, schema = @Schema(type = "string", allowableValues = {"category", "textbook", "question-paper", "homework"}, example = "textbook"))
    public ResponseDTO<CustomBody> TchLesnrscUnitRcmdList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchLesnRscService.findTchLesnrscUnitRcmdList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 수업 자료실 > 수업자료 만들기 - 현재 생성 중인 수업자료 유형과 동일한 유형의 자료들만 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/article/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 수업자료 > 수업자료 셋트지에 대한 아티클 목록조회", description = "")
    @Parameter(name = "setsId", description = "Sets ID", required = true, schema = @Schema(type = "string", example = "MSTG89094"))
    public ResponseDTO<CustomBody> tchLesnRscArticleList(
            @RequestParam(name = "setsId") String setsId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        List<String> requiredParams = Arrays.asList("setsId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchLesnRscService.findArticleListBySetId(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업자료 > 수업자료 셋트지에 대한 아티클 목록조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/rec-list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 교과서 재구성 > 수업 도움 자료 > 추천", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1152"))
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "37" ))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "12"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscRecList(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "crculId", defaultValue = "") Integer crculId,
            @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("textbkId","userId","crculId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchLesnRscService.findLesnRscRecList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 재구성 > 수업 도움 자료 > 추천");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/my-scrap/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 교과서 재구성 > 수업 도움 자료 > 내 스크랩", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1152"))
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "12"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscMyScrapList(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("textbkId","userId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchLesnRscService.findLesnRscMyScrapList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 재구성 > 수업 도움 자료 > 내 스크랩");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/my-rsc/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 교과서 재구성 > 수업 도움 자료 > 내 자료", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1152"))
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "923fc2a963504687a752484dda402f92"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "12"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchMyLesnRscList(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("textbkId","userId","claId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchLesnRscService.findMyLesnRscList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 재구성 > 수업 도움 자료 > 내 자료");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul-rec/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 추천 모듈(콘텐츠) 목록 조회", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "923fc2a963504687a752484dda402f92"))
    @Parameter(name = "articleIdList", description = "아티클 ID 목록", required = true, schema = @Schema(type = "string", example = "125684,6457,12525"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "12"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscMdulRecList(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "articleIdList", defaultValue = "") String articleIdList,
            @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        paramData.put("articleIdList", AidtCommonUtil.strToStringList(articleIdList));

        Object resultData = tchLesnRscService.findLesnRscMdulRecList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 추천 모듈(콘텐츠) 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/mdul-rec/list/eng", method = {RequestMethod.GET})
    @Operation(summary = "(교사) (영어)추천 모듈(콘텐츠) 목록 조회", description = "")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "923fc2a963504687a752484dda402f92"))
    @Parameter(name = "articleIdList", description = "아티클 ID 목록", required = true, schema = @Schema(type = "string", example = "125684,6457,12525"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "12"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> tchLesnRscMdulRecListForEng(
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "articleIdList", defaultValue = "") String articleIdList,
            @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        // paramData.put("articleIdList", AidtCommonUtil.strToStringList(articleIdList));
        // int sizeNo = MapUtils.getIntValue(paramData, "size", 10) ;

        Object resultData = tchLesnRscService.findLesnRscMdulRecListForEng(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 추천 모듈(콘텐츠) 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/rcmd/list", method = {RequestMethod.GET})
    @Operation(summary = "추천 수업 자료 조회", description = "수업 자료실 > 추천 수업 자료 조회")
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1152"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook322-t"))
    public ResponseDTO<CustomBody> tchLesnRscList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchLesnRscService.findLesnRscRcmdList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "추천 수업 자료 조회");

    }

}
