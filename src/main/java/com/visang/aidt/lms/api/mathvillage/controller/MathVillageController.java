package com.visang.aidt.lms.api.mathvillage.controller;

import com.visang.aidt.lms.api.mathvillage.service.MathVillageService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

/**
 * 수학마을 학습 단계 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 학습 단계별 학습 완료 학생 아이디 리스트 API", description = "(교사) 학습 단계별 학습 완료 학생 아이디 리스트 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MathVillageController {

    private final MathVillageService mathVillageService;

    @RequestMapping(value = {"/tch/mdul/math/step/list"}, method = {RequestMethod.GET})
    @Operation(summary = "(교사) 학습 단계별 학습 완료 학생 아이디 리스트", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"unitId\":\"10\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathRankList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            paramData.put("stdCpAt", "Y");
            Object resultData = mathVillageService.selectTchCompletedList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습 단계별 학습 완료 학생 리스트 보기");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/step/start"}, method = {RequestMethod.POST})
    @Operation(summary = "(학생) 학습 시작 등록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"unitId\":\"10\"," +
                            "\"stdCd\":1," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> startStepSave(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            paramData.put("stdCpAt", "N");
            Object resultData = mathVillageService.insertStep(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습 시작 등록");

        } catch (SQLIntegrityConstraintViolationException e) {
            log.error("DB 무결성 제약 위반");
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터 무결성 오류가 발생했습니다.");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/step/end"}, method = {RequestMethod.POST})
    @Operation(summary = "(학생) 학습 상태 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"unitId\":\"10\"," +
                            "\"stdCd\":1," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> finishStepMod(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            paramData.put("stdCpAt", "Y");
            Object resultData = mathVillageService.updateStep(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습 상태 수정");

        } catch (SQLIntegrityConstraintViolationException e) {
            log.error("DB 무결성 제약 위반");
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터 무결성 오류가 발생했습니다.");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/rpt/img/save"}, method = {RequestMethod.POST})
    @Operation(summary = "(학생) 수학 마을 리포트 그림 활동 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"unitId\":\"10\"," +
                            "\"sessId\":\"10\"," +
                            "\"dataUrl\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAADElEQVQImWNgoBMAAABpAAFEI8ARAAAAAElFTkSuQmCC\"," +
                            "\"dataTxt\":\"가나다라바바사\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveRptActvImage(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            paramData.put("qitemAt", "N");
            Object resultData = mathVillageService.insertImage(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수학마을 > 그림 활동 저장");

        } catch (IllegalArgumentException e) {
            log.warn("요청 파라미터 오류: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "요청 데이터가 유효하지 않습니다. (" + e.getMessage() + ")");

        } catch (SQLIntegrityConstraintViolationException e) {
            log.error("DB 무결성 제약 위반");
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터 무결성 오류가 발생했습니다.");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/rpt/qitem/save"}, method = {RequestMethod.POST}, produces="application/json; charset=UTF8")
    @Operation(summary = "(학생) 수학 마을 리포트 문항 풀이 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"unitId\":\"10\"," +
                            "\"sessId\":\"10\"," +
                            "\"qitemNo\":1," +
                            "\"icAt\":\"Y\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveRptQitem(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            paramData.put("qitemAt", "Y");
            Object resultData = mathVillageService.insertResult(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수학마을 > 준비하기 결과 저장");

        } catch (SQLIntegrityConstraintViolationException e) {
            log.error("DB 무결성 제약 위반");
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터 무결성 오류가 발생했습니다.");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/rpt/img"}, method = {RequestMethod.GET})
    @Operation(summary = "수학마을 리포트 그림 활동 조회", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"unitId\":\"10\"," +
                            "\"sessId\":\"10\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathVilGetRptImage(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            paramData.put("qitemAt", "N");
            Object resultData = mathVillageService.selectActvImage(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수학마을 리포트 > 그림 활동");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }

    @RequestMapping(value = {"/stnt/mdul/math/rpt/qitem"}, method = {RequestMethod.GET})
    @Operation(summary = "수학마을 리포트 문항 풀이 결과 조회", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"unitId\":\"10\"," +
                            "\"sessId\":\"10\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathVilGetRptResult(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            paramData.put("qitemAt", "Y");
            Object resultData = mathVillageService.selectResultList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수학마을 리포트 > 준비하기");

        } catch (PersistenceException | MyBatisSystemException e) {
            log.error("MyBatis 처리 오류");
            return AidtCommonUtil.makeResultFail(paramData, null, "DB 처리 중 오류가 발생했습니다.");

        } catch (NullPointerException e) {
            log.error("NullPointerException 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 데이터가 누락되어 처리에 실패했습니다.");

        } catch (Exception e) {
            log.error("알 수 없는 오류 발생");
            return AidtCommonUtil.makeResultFail(paramData, null, "알 수 없는 오류가 발생했습니다.");
        }
    }
}
