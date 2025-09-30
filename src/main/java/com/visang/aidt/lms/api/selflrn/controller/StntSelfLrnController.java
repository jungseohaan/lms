package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.StntSelfLrnService;
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

import java.net.URLDecoder;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "(학생) 스스로학습 API", description = "스스로학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntSelfLrnController {

    private final StntSelfLrnService stntSelfLrnService;

    // 학습자료 존재유무 체크
    @Loggable
    @GetMapping({"/stnt/self-lrn/std-info/check","/stnt/self-lrn/std-info/check/eng"})
    @Operation(summary = "학습자료 존재유무 체크", description = "학습자료 존재유무 체크")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> findStntSelfLrnStdInfoCheck(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @RequestParam(name = "textbkId", defaultValue = "1") String textbkId,
            @RequestParam(name = "claId", defaultValue = "1") String claId
    ) throws Exception {

        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("userId", userId);
        paramData.put("textbkId", textbkId);
        paramData.put("claId", claId);

        Boolean isExist = this.stntSelfLrnService.findStntSelfLrnStdInfoCheck(paramData);

        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("stdInfoExistYn", (isExist ? "Y" : "N"));

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습자료 존재유무 체크");

    }

    // 자기주도학습 단원 목록 조회
    @Loggable
    @GetMapping({"/stnt/self-lrn/chapter/list"})
    @Operation(summary = "자기주도학습 단원 목록 조회", description = "자기주도학습 단원 목록 조회")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    public ResponseDTO<CustomBody> findStntSelfLrnChapterList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.stntSelfLrnService.findStntSelfLrnChapterList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 단원 목록 조회");

    }

    // 자기주도학습 단원 개념 목록 조회
    @Loggable
    @GetMapping("/stnt/self-lrn/chapter/concept/list")
    @Operation(summary = "자기주도학습 단원 개념 목록 조회", description = "자기주도학습 단원 개념 목록 조회")
    @Parameter(name = "userId", description = "사용자 ID", required = true)
    @Parameter(name = "textbkId", description = "교과서 ID", required = true)
    @Parameter(name = "metaId", description = "단원 ID", required = true)
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> findStntSelfLrnChapterConceptList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.stntSelfLrnService.findStntSelfLrnChapterConceptList(paramData, pageable);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 단원 개념 목록 조회");

    }

    // 자기주도학습 생성
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/create" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 생성", description = "자기주도학습 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "emaone1-s2",
                    "textbkId" : "16",
                    "claId" : "736a1cf1e0af43b2853610d939b503db",
                    "stdCd" : "2",
                    "stdNm" : "선택학습",
                    "stdUsdId" : "1450",
                    "unitNum" : "1",
                    "lvlId" : "MD05",
                    "lvlNm" : "하"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreate(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId", "textbkId","stdCd","stdNm","unitNum","lvlId","lvlNm");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnService.saveStntSelfLrnCreate(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 생성");

    }


    // 자기주도학습 답안 제출
    @Loggable
    @RequestMapping( value = {"/stnt/self-lrn/submit/answer","/stnt/self-lrn/submit/answer/eng"} , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 답안 제출", description = "자기주도학습 답안 제출")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "slfResultId" : "1",
                    "subMitAnw" : "2,3",
                    "subMitAnwUrl" : "www.naver.com",
                    "errata" : "2",
                    "aiTutUseAt" : "Y",
                    "hdwrtCn" : "1",
                    "hntUseAt" : "Y"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnSubmitAnswer(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("slfResultId","subMitAnw","errata","aiTutUseAt");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Map resultData = stntSelfLrnService.saveStntSelfLrnSubmitAnswer(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 답안 제출");

    }

    // 자기주도학습 종료 하기
    @Loggable
    @RequestMapping(value =  {"/stnt/self-lrn/end","/stnt/self-lrn/end/eng"} , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 종료 하기", description = "자기주도학습 종료 하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "slfId" : "1",
                    "edAt" : "Y"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnEnd( @RequestBody Map<String, Object> paramData) throws Exception {


        Map resultData = stntSelfLrnService.saveStntSelfLrnEnd(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 종료 하기");

    }

    // 자기주도학습 결과 보기
    @Loggable
    @GetMapping("/stnt/self-lrn/result/summary")
    @Operation(summary = "자기주도학습 결과 보기", description = "자기주도학습 결과 보기")
    @Parameter(name = "slfId", description = "학습 ID", required = true)
    public ResponseDTO<CustomBody> findStntSelfLrnResultSummary(
            @RequestParam(name = "slfId", defaultValue = "1") String slfId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("slfId", slfId);


        Object resultData = this.stntSelfLrnService.findStntSelfLrnResultSummary(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 결과 보기");

    }

    // 학습내역 목록조회
    @Loggable
    @GetMapping({"/stnt/self-lrn/lrn/list","/stnt/self-lrn/lrn/list/eng"})
    @Operation(summary = "스스로 학습 목록조회", description = "스스로 학습 목록조회")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1241-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "309ab3bd0d2a47e289d9aaac902ead8e"))
    @Parameter(name = "condition", description = "검색 유형", required = false)
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false)
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> findStntSelfLrnList(
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntSelfLrnService.findStntSelfLrnList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로 학습 목록조회");

    }


    @Loggable
    @GetMapping({"/stnt/self-lrn/dash-board/graph", "/stnt/self-lrn/dash-board/graph/eng"})
    @Operation(summary = "스스로 학습 그래프 조회", description = "스스로 학습 그래프 조회")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1241-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "309ab3bd0d2a47e289d9aaac902ead8e"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> findStntSelfLrnDashBoardGraphList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntSelfLrnService.findStntSelfLrnDashBoardGraphList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로 학습 그래프 조회");
    }

    // 학습내역 결과보기
    @Loggable
    @GetMapping({"/stnt/self-lrn/lrn/result","/stnt/self-lrn/lrn/result/eng"})
    @Operation(summary = "학습내역 결과보기", description = "학습내역 결과보기")
    @Parameter(name = "slfId", description = "학습 ID", required = true)
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> findStntSelfLrnResultList(
            @RequestParam(name = "slfId", defaultValue = "1") String slfId,
            @RequestParam(name = "textbkId", defaultValue = "1") int textbkId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("slfId", slfId);
        paramData.put("textbkId", textbkId);


        Object resultData = this.stntSelfLrnService.findStntSelfLrnResultList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습내역 결과보기");

    }


    //모듈 학습내역 결과 조회
    @Loggable
    @GetMapping("/stnt/self-lrn/lrn/result-info")
    @Operation(summary = "모듈 학습내역 결과 조회", description = "모듈 학습내역 결과 조회")
    @Parameter(name = "slfResultId", description = "결과정보 ID", required = true)
    public ResponseDTO<CustomBody> findStntSelfLrnMudlResultSummaryList(
            @RequestParam(name = "slfResultId", defaultValue = "1") String slfId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("slfResultId", slfId);


        Object resultData = this.stntSelfLrnService.findStntSelfLrnMudlResultSummaryList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "모듈 학습내역 결과 조회");

    }

    //자기주도학습 응시 확인 저장
    @Loggable
    @RequestMapping( value = "/stnt/self-lrn/recheck" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 응시 확인 저장", description = "학생이 모듈(문항) 클릭해서 확인 할때 마다 자동으로 재확인(학습시작일) 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                    {
                        "slfResultId" : "1"
                    }
                """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnRecheck(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("slfResultId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Map resultData = stntSelfLrnService.saveStntSelfLrnRecheck(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 응시 확인 저장");

    }

    // 자기주도학습 (오답시) 유사문항 받기
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/similar-question/receive" , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 (오답시) 유사문항 받기", description = "자기주도학습 (오답시) 유사문항 받기")
    @Parameter(name = "slfResultId", description = "결과정보 ID", required = true)
    public ResponseDTO<CustomBody> saveStntSelfLrnReceive(
            @RequestParam(name = "slfResultId", defaultValue = "1") String slfResultId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("slfResultId", slfResultId);

        Object resultData = stntSelfLrnService.saveStntSelfLrnReceive(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 (오답시) 유사문항 받기");

    }

    @Loggable
    @GetMapping("/stnt/self-lrn/usd-low/kwg/list")
    @Operation(summary = "AI 학습 (단원-학습이해도 낮은 지식요인 최대 3개 조회)", description = "선택된 단원의 학습이해도가 낮은 지식요인 3개 조회")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "emaone1-s2"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "16"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "736a1cf1e0af43b2853610d939b503db"))
    @Parameter(name = "unitNum", description = "단원 번호", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> stntSelflrnUsdlowKwgList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.stntSelfLrnService.stntSelflrnUsdlowKwgList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 학습");

    }

    @Loggable
    @GetMapping("/stnt/self-lrn/std-cpt/list")
    @Operation(summary = "자기주도학습-학습역량 표시", description = "자기주도학습-학습역량 표시")
    @Parameter(name = "codeGbCd", description = "코드구분CD", required = true, schema = @Schema(type = "string", example = "std-cpt"))
    public ResponseDTO<CustomBody> selectStdCptList(
            @RequestParam(name = "codeGbCd", defaultValue = "std-cpt") String codeGbCd
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("codeGbCd",codeGbCd);
        Object resultData = this.stntSelfLrnService.selectStdCptList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습-학습역량 표시");

    }

    @Loggable
    @GetMapping("/stnt/self-lrn/std-mth/list")
    @Operation(summary = "자기주도학습-학습방법 및 내용조회", description = "자기주도학습-학습방법 및 내용조회")
    @Parameter(name = "codeGbCd", description = "코드구분CD", required = true, schema = @Schema(type = "string", example = "L/C"))
    public ResponseDTO<CustomBody> selectStdMthList(
            @RequestParam(name = "codeGbCd", defaultValue = "L/C") String codeGbCd
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();

        String cnvtCodeGbCd = URLDecoder.decode(codeGbCd, "UTF-8");
        paramData.put("codeGbCd","std-cpt-"+cnvtCodeGbCd);
        Object resultData = this.stntSelfLrnService.selectStdMthList(paramData);


        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습-학습방법 및 내용조회");

    }

    // 자기주도학습 활동난이도조회
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/act-lvl/list", method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습-활동난이도 표시", description = "자기주도학습-활동난이도 표시")
    @Parameter(name = "codeGbCd", description = "코드구분CD", required = false)
    public ResponseDTO<CustomBody> findStntSelfLrnActLvlList(
            @RequestParam(name = "codeGbCd", defaultValue = "act-lvl", required = false) String codeGbCd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        LinkedHashMap<String, Object> resultData = stntSelfLrnService.findStntSelfLrnActLvlList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습-활동난이도 표시");

    }

    // 자기주도학습 답안입력방식조회
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/anw-ipt-ty/list", method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습-답안입력방식 표시", description = "자기주도학습-답안입력방식 표시")
    @Parameter(name = "codeGbCd", description = "코드구분CD", required = false)
    public ResponseDTO<CustomBody> findStntSelfLrnAnwIptTyList(
            @RequestParam(name = "codeGbCd", defaultValue = "anw-ipt-ty", required = false) String codeGbCd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        LinkedHashMap<String, Object> resultData = stntSelfLrnService.findStntSelfLrnAnwIptTyList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습-답안입력방식 조회");

    }

    // 자기주도학습-활동유형 표시
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/act-ty/list", method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습-활동유형 표시", description = "자기주도학습-활동유형 표시")
    @Parameter(name = "codeGbCd", description = "코드구분CD", required = false)
    public ResponseDTO<CustomBody> findStntSelfLrnActTyList(
            @RequestParam(name = "codeGbCd", defaultValue = "act-ty", required = false) String codeGbCd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        LinkedHashMap<String, Object> resultData = stntSelfLrnService.findStntSelfLrnActTyList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습-활동유형 표시");

    }

    // 자기주도학습 단원 목록 조회-영어
    @Loggable
    @RequestMapping(value =  {"/stnt/lesn/start/eng","/stnt/self-lrn/chapter/eng"} , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 단원 목록 조회-영어2", description = "자기주도학습 단원 목록 조회-영어")
    @Parameter(name = "userId", description = "사용자 ID", required = true)
    @Parameter(name = "textbkId", description = "교과서 ID", required = true)
    public ResponseDTO<CustomBody> findStntSelfLrnEngChapterList(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @RequestParam(name = "textbkId", defaultValue = "1") String textbkId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("userId", userId);
        paramData.put("textbkId", textbkId);

        Object resultData = this.stntSelfLrnService.findStntSelfLrnChapterList2(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 단원 목록 조회-영어");

    }


    // 자기주도학습 단원 목록 조회-영어
    @Loggable
    @RequestMapping(value =  {"/stnt/self-lrn/chapter/list/eng"} , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 단원 목록 조회-영어3", description = "자기주도학습 단원 목록 조회-영어")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    public ResponseDTO<CustomBody> findStntSelfLrnEngChapterList3(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.stntSelfLrnService.findStntSelfLrnChapterList3(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 단원 목록 조회-영어");

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 목록 조회하기
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/my-word/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 목록 조회하기", description = "나의 단어장 목록 조회하기")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    public ResponseDTO<CustomBody> selectStntSelfLrnMyWordList(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntSelfLrnService.selectStntSelfLrnMyWordList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "나의 단어장 목록 조회하기");

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 플래시카드 start
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/my-word/flash/start", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 플래시카드 start", description = "플래시카드 start")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "myWordId", description = "선택 단어ID", required = false)
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntSelfLrnMyWordFlashStart(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @RequestParam(name = "myWordId", defaultValue = "1") String myWordId,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntSelfLrnService.selectStntSelfLrnMyWordFlashStart(paramData, pageable);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "플래시카드 start");

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 테스트 start
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/my-word/exam/start", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 테스트 start", description = "나의 단어장 테스트 start")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "myWordId", description = "선택 단어ID", required = false)
    @Parameter(name = "examCd", description = "시험유형", required = false)
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntSelfLrnMyWordExamStart(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @RequestParam(name = "myWordId", defaultValue = "1") String myWordId,
            @RequestParam(name = "examCd", defaultValue = "1") String examCd,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntSelfLrnService.selectStntSelfLrnMyWordExamStart(paramData, pageable);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "나의 단어장 테스트 start");

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 발음연습하기 start
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/my-word/articulation/start", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 발음연습하기 start", description = "발음연습하기 start")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "myWordId", description = "선택 단어ID", required = false)
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntSelfLrnMyWordArticulationStart(
            @RequestParam(name = "userId", defaultValue = "vsstu1") String userId,
            @RequestParam(name = "myWordId", defaultValue = "1") String myWordId,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData

    ) throws Exception {
        Object resultData = stntSelfLrnService.selectStntSelfLrnMyWordArticulationStart(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "발음연습하기");
    }

    //AI SW학습선택
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/ai-edit/init/eng", method = {RequestMethod.GET})
    @Operation(summary = "AI SW학습선택", description = "AI SW학습선택")
    public ResponseDTO<CustomBody> selectStntSelfLrnMyWordArticulationStart(
            Map<String, Object> paramData
    ) throws Exception {
        Object resultData = stntSelfLrnService.selectAiEditInitEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI SW학습선택");
    }

    // AI SW 저장
    @Loggable
    @RequestMapping( value = {"/stnt/self-lrn/ai-edit/save/eng"} , method = {RequestMethod.POST})
    @Operation(summary = "AI SW 저장", description = "AI SW 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "1",
                    "aiStdCd" : "2",
                    "wrtSents" : "www",
                    "aiEdit" : "2",
                    "aiEditAnlsResult" : "Y"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveAiEditSaveEng(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId","aiStdCd","wrtSents","aiEdit");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Map resultData = stntSelfLrnService.saveAiEditSaveEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI SW 저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/ai-edit/result/eng", method = {RequestMethod.GET})
    @Operation(summary = "AI SW 결과", description = "AI SW 결과")
    @Parameter(name = "id", description = "AI 첨삭 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> selectAiEditResultEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
       Object resultData = stntSelfLrnService.selectAiEditResultEng(paramData);
       return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI SW 결과");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/createAll/elementary" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - 모아 풀기 (초등)", description = "자기주도학습 - 모아 풀기 (초등)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "mathreal151-s1",
                    "textbkId" : "373",
                    "claId" : "5a0a89a258bd48968a4eedcc229e2b04",
                    "stdCd" : "2",
                    "stdNm" : "1단원 > 모아 풀기",
                    "unitNum" : 1,
                    "metaId" : 870
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateAllElementary(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId","textbkId","claId","stdCd","stdNm","unitNum","metaId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnService.saveStntSelfLrnCreateAll(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - 모아 풀기 (초등)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/createAll/high" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - 모아 풀기 (중고등)", description = "자기주도학습 - 모아 풀기 (중고등)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "mathreal151-s1",
                    "textbkId" : "373",
                    "claId" : "5a0a89a258bd48968a4eedcc229e2b04",
                    "stdCd" : "2",
                    "stdNm" : "1단원 > 모아 풀기",
                    "unitNum" : 1,
                    "metaId" : 870
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateAllHigh(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId","textbkId","claId","stdCd","stdNm","unitNum","metaId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        // textbkId 값으로 학교 구분 불가
        paramData.put("curriSchool","high");
        Object resultData = stntSelfLrnService.saveStntSelfLrnCreateAll(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - 모아 풀기 (중고등)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/create/studymap/elementary" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - 지식요인 선택 (초등)", description = "자기주도학습 - 지식요인 선택 (초등)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "mathreal151-s1",
                    "textbkId" : "373",
                    "claId" : "5a0a89a258bd48968a4eedcc229e2b04",
                    "stdCd" : "2",
                    "stdNm" : "1단원 > 소수와 합성수",
                    "unitNum" : 1,
                    "metaId" : 870,
                    "kwgMainId" : 916,
                    "stdUsdId" : "1450",
                    "usdScr" : 44
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateElementary(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId", "textbkId","claId","stdCd","stdNm","unitNum","metaId","kwgMainId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnService.saveStntSelfLrnCreateStudymap(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - 지식요인 선택 (초등)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/create/studymap/high" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - 지식요인 선택 (중고등)", description = "자기주도학습 - 지식요인 선택 (중고등)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "mathreal151-s1",
                    "textbkId" : "373",
                    "claId" : "5a0a89a258bd48968a4eedcc229e2b04",
                    "stdCd" : "2",
                    "stdNm" : "1단원 > 소수와 합성수",
                    "unitNum" : 1,
                    "metaId" : 870,
                    "kwgMainId" : 916,
                    "stdUsdId" : "1450",
                    "usdScr" : 44
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateElementaryHigh(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId", "textbkId","claId","stdCd","stdNm","unitNum","metaId","kwgMainId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        // textbkId 값으로 학교 구분 불가
        paramData.put("curriSchool","high");
        Object resultData = stntSelfLrnService.saveStntSelfLrnCreateStudymap(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - 지식요인 선택 (중고등)");

    }
}
