package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchStdService;
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

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(교사) 교과 API", description = "(교사) 교과 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchStdController {
    private final TchStdService tchStdService;

    @Loggable
    @RequestMapping(value = "/tch/std/list", method = {RequestMethod.GET})
    @Operation(summary = "교과목록조회", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vstea60"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad6adba8f11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "tmprStrgAt", description = "공유완료/설정미완료 구분", required = true, schema = @Schema(type = "string", allowableValues = {"Y", "N"}, defaultValue = "N"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchStdList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.findTchStdList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과목록조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/del", method = {RequestMethod.POST})
    @Operation(summary = "교과삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdDel(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.removeTchStdDel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과삭제");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/create", method = {RequestMethod.POST})
    @Operation(summary = "수업자료 생성", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"aidt3\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772670\"," +
                            "\"textbkId\":1," +
                            "\"stdDatNm\":\"학습자료명1\"," +
                            "\"eamMth\":3," +
                            "\"eamExmNum\":null," +
                            "\"eamGdExmMun\":null," +
                            "\"eamAvUpExmMun\":null," +
                            "\"eamAvExmMun\":null," +
                            "\"eamAvLwExmMun\":null," +
                            "\"eamBdExmMun\":null," +
                            "\"eamScp\":\"4,5\"," +
                            "\"setsId\":\"240\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업자료 생성");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/read-info", method = {RequestMethod.GET})
    @Operation(summary = "교과 자료설정 수정(조회)", description = "")
    @Parameter(name = "stdId", description = "학습자료 ID", required = true, schema = @Schema(type = "string", example = "66"))
    public ResponseDTO<CustomBody> tchStdReadInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.findTchStdReadInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과 자료설정 수정(조회)");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/save", method = {RequestMethod.POST})
    @Operation(summary = "교과 자료설정 수정(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdId\":66," +
                            "\"eamScp\":\"4,5\"," +
                            "\"wrterId\":\"aidt3\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772670\"," +
                            "\"textbkId\":1," +
                            "\"setsId\":\"129\"," +
                            "\"stdDatNm\":\"학습자료명1\"," +
                            "\"textbkTabNm\":\"교과서탭명1\"," +
                            "\"crculId\":1," +
                            "\"bbsSvAt\":\"N\"," +
                            "\"bbsNm\":\"자료명1\"," +
                            "\"tag\":\"태그1\"," +
                            "\"cocnrAt\":\"Y\"," +
                            "\"selTabId\":null," +
                            "\"slfEvlInfo\":{" +
                            "\"userId\":\"aidt3\"," +
                            "\"gbCd\":\"1\"," +
                            "\"wrterId\":\"aidt3\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"1\"," +
                            "\"slfPerEvlNm\":\"테스트1\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":1," +
                            "\"tabId\":null," +
                            "\"taskId\":null," +
                            "\"evlId\":null," +
                            "\"setsId\":\"129\"," +
                            "\"resultDtlId\":null," +
                            "\"tmpltId\":null," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}," +
                            "\"perEvlInfo\":{" +
                            "\"userId\":\"aidt3\"," +
                            "\"gbCd\":\"1\"," +
                            "\"wrterId\":\"aidt3\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"2\"," +
                            "\"slfPerEvlNm\":\"테스트2\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":1," +
                            "\"tabId\":null," +
                            "\"taskId\":null," +
                            "\"evlId\":null," +
                            "\"setsId\":\"129\"," +
                            "\"resultDtlId\":null," +
                            "\"tmpltId\":null," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과 자료설정 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = {"/tch/std/lastpage/save", "/stnt/std/lastpage/save"}, method = {RequestMethod.POST})
    @Operation(summary = "마지막 화면의 정보를 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"vstea60\"," +
                            "\"claId\":\"308ad6adba8f11ee88c00242ac110002\"," +
                            "\"textbkId\":1," +
                            "\"setsId\":\"1\"," +
                            "\"articleId\":\"1433\"," +
                            "\"pageNum\":1," +
                            "\"scrnPageSeCd\":\"1\"," +     //  1: 한쪽보기, 2: 두쪽보기
                            "\"scrnSeCd\":1" +              // 1 : 웹뷰어, 2 : 이북뷰어
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdLastPageSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdLastPageSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막 화면의 정보를 저장");

    }

    @Loggable
    @RequestMapping(value = {"/tch/std/lastpage/call", "/stnt/std/lastpage/call"}, method = {RequestMethod.GET})
    @Operation(summary = "마지막 화면의 정보를 호출", description = "")
    @Parameter(name = "wrterId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vstea60"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad6adba8f11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "scrnSeCd", description = "페이지타입", required = true, schema = @Schema(type = "integer", allowableValues = {"1", "2"}, defaultValue = "1"))
    public ResponseDTO<CustomBody> tchStdLastPageCall(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.findTchStdLastPageCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막 화면의 정보를 호출");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/m-save", method = {RequestMethod.POST})
    @Operation(summary = "수업 마법봉 수정(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdId\":72," +
                            "\"setsId\":\"129\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdMSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdMSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 마법봉 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tab/std/m-save", method = {RequestMethod.POST})
    @Operation(summary = "수업 탭 마법봉 수정(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"tabId\":11111," +
                            "\"setsId\":\"129\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchTabStdMSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchTabStdMSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 탭 마법봉 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/lrn-hist/check", method = {RequestMethod.GET})
    @Operation(summary = "수업 학습이력 존재유무 체크", description = "")
    @Parameter(name = "stdId", description = "학습정보 ID", required = true, schema = @Schema(type = "integer", example = "72"))
    public ResponseDTO<CustomBody> tchStdLrnHistCheck(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.tchStdLrnHistCheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 학습이력 존재유무 체크");

    }

    @Loggable
    @RequestMapping(value = "/tch/tab/std/lrn-hist/check", method = {RequestMethod.GET})
    @Operation(summary = "(탭) 수업 학습이력 존재유무 체크", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "992904"))
    public ResponseDTO<CustomBody> tchTabStdLrnHistCheck(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.tchTabStdLrnHistCheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 학습이력 존재유무 체크");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/use-shared/create", method = {RequestMethod.POST})
    @Operation(summary = "공유완료된 수업자료를 사용해서 수업자료 생성", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"selStdId\":11111," +
                            "\"crculId\":\"129\"," +
                            "\"selTabId\":null" +
                            "}"
                    )
            }
        ))
    public ResponseDTO<CustomBody> tchStdUseSharedCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdUseSharedCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "공유완료된 수업자료를 사용해서 수업자료 생성");

    }

    @Loggable
    @RequestMapping(value = "/tch/std/use-set/create", method = {RequestMethod.POST})
    @Operation(summary = "셋트지를 사용해서 수업자료 생성", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"aidt3\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772670\"," +
                            "\"textbkId\":1," +
                            "\"setsId\":\"725\"," +
                            "\"crculId\":\"30\"," +
                            "\"selTabId\":8205" +
                            "}"
                    )
            }
        ))
    public ResponseDTO<CustomBody> tchStdUseSetCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdUseSetCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "셋트지를 사용해서 수업자료 생성");

    }


    @Loggable
    @RequestMapping(value = "/tch/std/use-mystd/create", method = {RequestMethod.POST})
    @Operation(summary = "내 자료를 사용해서 수업자료 생성", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                           "wrterId": "mathbook3107-t",
                           "claId": "bbcd5234640443678e9e2f025976c236",
                           "textbkId": 1201,
                           "setsId": "115216",
                           "stdDatNm": "1219 15시 13분에 만든 자료",
                           "textbkTabNm": "1219 15시 13분에 만든 자료",
                           "crculId": 48,
                           "eamScp": "48"
                        }
                    """)
            }
    ))
    public ResponseDTO<CustomBody> tchStdUseMystdCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createtchStdUseMystdCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 자료를 사용해서 수업자료 생성");

    }

    @Loggable
    @RequestMapping(value = {"/tch/std/start"}, method = {RequestMethod.POST})
    @Operation(summary = "수업시작시 시간 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vstea60\"," +
                            "\"claId\":\"308ad6adba8f11ee88c00242ac110002\"," +
                            "\"textbkId\":\"1\"," +
                            "\"crculId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdStart(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업시작시 시간 저장");

    }


    @Loggable
    @RequestMapping(value = {"/tch/std/end"}, method = {RequestMethod.POST})
    @Operation(summary = "수업종료시 시간 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vstea60\"," +
                            "\"claId\":\"308ad6adba8f11ee88c00242ac110002\"," +
                            "\"tabId\":1," +
                            "\"textbkId\":\"1\"," +
                            "\"crculId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchStdEnd(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.modifyTchStdEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업종료시 시간 저장");

    }


    @Loggable
    @RequestMapping(value = "/tch/std/lesson/recon/save", method = {RequestMethod.POST})
    @Operation(summary = "수업 재구성 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                 "wrterId": "mathbook3107-t",
                                 "claId": "bbcd5234640443678e9e2f025976c236",
                                 "textbkId": 1201,
                                 "crculId": 48,
                                 "myStdList": [
                                     {
                                         "stdDatNm": "2025-02-10 10:39:07 저장됨",
                                         "eamMth": 2,
                                         "eamExmNum": 1,
                                         "eamGdExmMun": 0,
                                         "eamAvUpExmMun": 0,
                                         "eamAvExmMun": 1,
                                         "eamAvLwExmMun": 0,
                                         "eamBdExmMun": 0,
                                         "eamScp": "35183",
                                         "setsId": "MDEV110026",
                                         "stdId": null,
                                         "deleteAt" : "N"
                                     },
                                     {
                                         "cntsType": 1,
                                         "cntsNm": "cntsNm1",
                                         "cntsExt": "pdf",
                                         "url": "testUrl1",
                                         "fileSeq": 1,
                                         "extLearnCntsId": null,
                                         "deleteAt" : "N"
                                     }
                                 ],
                                 "tabList": [
                                     {
                                         "tabId": 1,
                                         "tabSeq": 1,
                                         "tabNm" : "",
                                         "stdId" : null,
                                         "setsId": "setsId1",
                                         "setsChgAt": "Y",
                                         "exposAt": "N",
                                         "cntsId": null,
                                         "deleteAt" : null
                                     },
                                     {
                                         "tabId": null,
                                         "tabSeq": 2,
                                         "tabNm" : "",
                                         "stdId" : null,
                                         "setsId": "setsId2",
                                         "setsChgAt": "N",
                                         "exposAt": "N",
                                         "cntsId": null,
                                         "deleteAt" : null
                                     },
                                     {
                                         "tabId": null,
                                         "tabSeq": 3,
                                         "tabNm" : "",
                                         "stdId" : null,
                                         "setsId": null,
                                         "setsChgAt": "N",
                                         "exposAt": "N",
                                         "extLearnCntsId",:null,
                                         "fileSeq": 1,
                                         "deleteAt" : null
                                     },
                                     {
                                         "tabId": 2,
                                         "tabSeq": 0,
                                         "tabNm" : "",
                                         "stdId" : 2803,
                                         "setsId": null,
                                         "setsChgAt": "N",
                                         "exposAt": "N",
                                         "cntsId": 1,
                                         "deleteAt" : "Y"
                                     }
                                 ]
                             }
                    """)
            }
    ))
    public ResponseDTO<CustomBody> tchStdLessonReconSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchStdService.createTchStdLessonReconSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 재구성 저장");

    }

}
