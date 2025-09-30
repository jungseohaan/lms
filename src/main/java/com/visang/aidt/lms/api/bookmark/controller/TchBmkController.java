package com.visang.aidt.lms.api.bookmark.controller;

import com.visang.aidt.lms.api.bookmark.service.TchBmkService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
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
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * (교사) 북마크 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 북마크 API", description = "(교사) 북마크 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchBmkController {
    private final TchBmkService tchBmkService;

    @Loggable
    @GetMapping(value = {"/tch/mdul/bmk/list","/stnt/mdul/bmk/list"})
    @Operation(summary = "북마크 목록보기", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "scrnSeCd", description = "화면구분", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eBookCallType", description = "이북호출유형", schema = @Schema(type = "string", example = "ALL"))
    @Parameter(name = "ebkId", description = "이북ID", required = false, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "unitNum", description = "단원차시", required = false, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchMdulBmkList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchBmkService.findBkmkList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크 목록보기");
    }

    @Loggable
    @RequestMapping(value = "/tch/mdul/bmk/share", method = {RequestMethod.POST})
    @Operation(summary = "북마크 공유하기", description = "")
    //@Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    //@Parameter(name = "bkmkId", description = "북마크 ID", required = true, schema = @Schema(type = "array", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = {
                        @ExampleObject(name = "파라미터", value = "{" +
                                "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                                "\"bkmkId\":[1,2,3]" +
                                "}"
                        )
                }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkShare(
            //@RequestParam(name = "userId", defaultValue = "") String userId,
            //@RequestParam(name = "bkmkId", defaultValue = "") String[] bkmkId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        try {
            Object resultData = tchBmkService.createShareBmk(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크 공유하기");
        } catch (AidtException e) {
                log.error("북마크 공유 실패 - 비즈니스 로직 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        } catch (DataAccessException e) {
                log.error("북마크 공유 실패 - 데이터베이스 접근 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 공유 처리 중 데이터베이스 오류가 발생했습니다. 관리자에게 문의하세요.");
        } catch (IllegalArgumentException e) {
                log.error("북마크 공유 실패 - 잘못된 파라미터: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "요청 파라미터가 올바르지 않습니다. 입력값을 확인해주세요.");
        } catch (NullPointerException e) {
                log.error("북마크 공유 실패 - null 참조 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되었습니다. 요청 정보를 확인해주세요.");
        } catch (Exception e) {
                log.error("북마크 공유 실패 - 예상치 못한 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 공유 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

    }


    @Loggable
    @RequestMapping(value = "/tch/mdul/bmk/share/clear", method = {RequestMethod.POST})
    @Operation(summary = "북마크 공유 취소하기", description = "")
    //@Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    //@Parameter(name = "bkmkId", description = "북마크 ID", required = true, schema = @Schema(type = "array", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"bkmkId\":282," +
                            "\"tagId\":241" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulBmkShareClear(
            //@RequestParam(name = "userId", defaultValue = "") String userId,
            //@RequestParam(name = "bkmkId", defaultValue = "") String[] bkmkId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        try {
            Object resultData = tchBmkService.createShareBmkClear(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크 공유취소");
        } catch (AidtException e) {
                log.error("북마크 공유 취소 실패 - 비즈니스 로직 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        } catch (DataAccessException e) {
                log.error("북마크 공유 취소 실패 - 데이터베이스 접근 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 공유 취소 처리 중 데이터베이스 오류가 발생했습니다. 관리자에게 문의하세요.");
        } catch (IllegalArgumentException e) {
                log.error("북마크 공유 취소 실패 - 잘못된 파라미터: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "요청 파라미터가 올바르지 않습니다. 입력값을 확인해주세요.");
        } catch (NullPointerException e) {
                log.error("북마크 공유 취소 실패 - null 참조 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되었습니다. 요청 정보를 확인해주세요.");
        } catch (Exception e) {
                log.error("북마크 공유 취소 실패 - 예상치 못한 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 공유 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/save","/stnt/mdul/bmk/save"}, method = {RequestMethod.POST})
    @Operation(summary = "북마크 설정", description = "")
    //@Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    //@Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "44"))
    //@Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "integer", example = "411"))
    //@Parameter(name = "crculId", description = "커리큘럼KEY", required = true, schema = @Schema(type = "string", example = "1"))
    //@Parameter(name = "cocnrAt", description = "공유여부", required = true, schema = @Schema(type = "string", example = "N"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "파라미터", value = "{" +
                                    "\"userId\":\"vsstu1\"," +
                                    "\"scrnSeCd\":1," +
                                    "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                                    "\"textbkId\":1," +
                                    "\"crculId\":1," +
                                    "\"cocnrAt\":\"N\"," +
                                    "\"tabId\":44," +
                                    "\"moduleId\":\"441\"," +
                                    "\"subId\":\"0\"," +
                                    "\"pdfUrl\":\"pdfUrl1\"," +
                                    "\"page\":\"2\"," +
                                    "\"ebkId\":\"1\"," +
                                    "\"unitNum\":0" +
                                "}"
                            )
                    }
        ))
    public ResponseDTO<CustomBody> tchMdulBmkSave(
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            Object resultData = tchBmkService.insertBkmkInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크 설정");
        } catch (AidtException e) {
                log.error("북마크 설정 실패 - 비즈니스 로직 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        } catch (DataAccessException e) {
                log.error("북마크 설정 실패 - 데이터베이스 접근 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 설정 처리 중 데이터베이스 오류가 발생했습니다. 관리자에게 문의하세요.");
        } catch (IllegalArgumentException e) {
                log.error("북마크 설정 실패 - 잘못된 파라미터: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "요청 파라미터가 올바르지 않습니다. 입력값을 확인해주세요.");
        } catch (NullPointerException e) {
                log.error("북마크 설정 실패 - null 참조 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되었습니다. 요청 정보를 확인해주세요.");
        } catch (Exception e) {
                log.error("북마크 설정 실패 - 예상치 못한 오류: {}", CustomLokiLog.errorLog(e));
                return AidtCommonUtil.makeResultFail(paramData, null, "북마크 공유 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/delete","/stnt/mdul/bmk/delete"}, method = {RequestMethod.POST})
    @Operation(summary = "북마크 삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = {
                        @ExampleObject(name = "파라미터", value = "{" +
                                "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                                "\"bkmkId\":[1,2,3]" +
                                "}"
                        )
                }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkDelete(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchBmkService.tchMdulBmkDelete(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크삭제");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/modify","/stnt/mdul/bmk/tag/modify"}, method = {RequestMethod.POST})
    @Operation(summary = "북마크태그수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "파라미터", value = "{" +
                                    "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                                    "\"tagId\":1," +
                                    "\"tagNm\":\"t_nm_test\"," +
                                    "\"clorNum\":\"1\"" +
                                    "}"

                            )
                    }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkTagModify(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
            Object resultData = tchBmkService.tchMdulBmkTagModify(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크태그수정");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/delete","/stnt/mdul/bmk/tag/delete"}, method = {RequestMethod.POST})
    @Operation(summary = "북마크태그삭제", description = "")
//    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
//    @Parameter(name = "bkmkId", description = "북마크 ID", required = true, schema = @Schema(type = "array", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"bkmkId\":\"1\"," +
                            "\"tagId\":1" +
                            "}"
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkTagDelete(
//        @RequestParam(name = "userId", defaultValue = "") String userId,
//        @RequestParam(name = "bkmkId", defaultValue = "") String bkmkId,
//        @RequestParam(name = "tabId", defaultValue = "") String tabId,
//        @RequestParam(name = "tagNm", defaultValue = "") String tagNm,
//        @RequestParam(name = "bassTagAt", defaultValue = "") String bassTagAt,
            @RequestBody Map<String, Object> paramData
    )throws Exception {
            Object resultData = tchBmkService.tchMdulBmkTagDelete(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크태그삭제");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/save","/stnt/mdul/bmk/tag/save"}, method = {RequestMethod.POST})
      @Operation(summary = "북마크태그등록", description = "")
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                      content = @Content(examples = {
                              @ExampleObject(name = "파라미터", value = "{" +
                                      "\"userId\":\"vsstu1\"," +
                                      "\"bkmkId\":1," +
                                      "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                                      "\"tagNm\":\"t_nm_test\"," +
                                      "\"clorNum\":\"3\"" +
                                  "}"
                              )
                      }
      ))
      public ResponseDTO<CustomBody> tchMdulBmkTagSave(
              @RequestBody Map<String, Object> paramData
      )throws Exception {
              Object resultData = tchBmkService.createTchMdulBmkTagSave(paramData);
              return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크태그등록");
      }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/tagsave","/stnt/mdul/bmk/tag/tagsave"}, method = {RequestMethod.POST})
    @Operation(summary = "태그(등록)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(examples = {
                  @ExampleObject(name = "파라미터", value = "{" +
                          "\"userId\":\"vsstu1\"," +
                          "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                          "\"textbkId\":1," +
                          "\"tagNm\":\"t_nm_test\"," +
                          "\"clorNum\":\"3\"" +
                      "}"
                  )
          }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkTagTagsave(
          @RequestBody Map<String, Object> paramData
    )throws Exception {
              Object resultData = tchBmkService.createTchMdulBmkTagTagsave(paramData);
              return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크태그등록");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/tagdel","/stnt/mdul/bmk/tag/tagdel"}, method = {RequestMethod.POST})
    @Operation(summary = "태그(삭제)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vsstu1\"," +
                            "\"tagId\":1" +
                            "}"
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkTagTagdel(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchBmkService.tchMdulBmkTagTagdel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "태그(삭제)");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/tagmod","/stnt/mdul/bmk/tag/tagmod"}, method = {RequestMethod.POST})
        @Operation(summary = "태그(수정)", description = "")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        content = @Content(examples = {
                                @ExampleObject(name = "파라미터", value = "{" +
                                        "\"userId\":\"vsstu1\"," +
                                        "\"tagId\":1," +
                                        "\"tagNm\":\"t_nm_test\"," +
                                        "\"clorNum\":\"1\"" +
                                    "}"
                                )
                        }
        ))
        public ResponseDTO<CustomBody> tchMdulBmkTagTagmod(
                @RequestBody Map<String, Object> paramData
        )throws Exception {
                Object resultData = tchBmkService.tchMdulBmkTagTagmod(paramData);
                return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크태그수정");
        }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/info", "/stnt/mdul/bmk/info"}, method = {RequestMethod.GET})
    @Operation(summary = "북마크 (모듈별) 정보", description = "")
    @Parameter(name = "userId", description = "대상자 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "tabId", description = "탭ID", required = true, schema = @Schema(type = "string", example = "44"))
    @Parameter(name = "moduleId", description = "모듈ID", required = true, schema = @Schema(type = "string", example = "441"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "crculId", description = "컬리큘럼KEY", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "scrnSeCd", description = "화면구분", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> stntMdulBmkInfo(
            HttpServletRequest request,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{
            Object resultData = tchBmkService.findTchMdulBmkInfo(paramData, request.getRequestURI());
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "북마크 (모듈별) 정보");
    }

    @Loggable
    @RequestMapping(value = {"/tch/mdul/bmk/tag/list", "/stnt/mdul/bmk/tag/list"}, method = {RequestMethod.GET})
    @Operation(summary = "태그(목록)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathreal151-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "373"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "5a0a89a258bd48968a4eedcc229e2b04"))
    public ResponseDTO<CustomBody> tchMdulBmkTagList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{
            Object resultData = tchBmkService.findTchMdulBmkTagList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "태그(목록)");
    }

}
