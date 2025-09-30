package com.visang.aidt.lms.api.mathexplore.controller;

import com.visang.aidt.lms.api.mathexplore.service.MathExploreService;
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
 * (학생) 수학 탐험대 최고 점수 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 수학 탐험대 최고 점수 API", description = "(학생) 수학 탐험대 최고 점수 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MathExploreController {
    private final MathExploreService mathExploreService;

    @RequestMapping(value = {"/stnt/mdul/math/rank/list"}, method = {RequestMethod.GET}, produces="application/json; charset=UTF8")
    @Operation(summary = "학급별 랭킹 보기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"cnt\":10," +
                            "}"
                    )
            }
            ))

    public ResponseDTO<CustomBody> mathClaRankList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = mathExploreService.selectClaRankList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학급별 랭킹 보기");

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

    @RequestMapping(value = {"/tch/mdul/math/game/rank/list"}, method = {RequestMethod.GET}, produces="application/json; charset=UTF8")
    @Operation(summary = "게임별 랭킹 보기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"gameId\":\"em09_422\"," +
                            "\"regDt\":\"2024-01-17\"," +
                            "\"cnt\":10," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathGameRankList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = mathExploreService.selectGameRankList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "게임별 랭킹 보기");

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

    @RequestMapping(value = {"/stnt/mdul/math/score/save"}, method = {RequestMethod.POST}, produces="application/json; charset=UTF8")
    @Operation(summary = "(학생) 점수 등록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"gameId\":\"em09_422\"," +
                            "\"hgScr\":500," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathScrSave(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            Object resultData = mathExploreService.saveScr(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "점수 등록");

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

    @RequestMapping(value = {"/stnt/mdul/math/rank"}, method = {RequestMethod.GET}, produces="application/json; charset=UTF8")
    @Operation(summary = "(학생) 학급별 나의 최고 기록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"cnt\":10," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathClaMyRank(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = mathExploreService.findClaMyBestScr(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학급별 나의 최고 기록");

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

    @RequestMapping(value = {"/stnt/mdul/math/game/rank"}, method = {RequestMethod.GET}, produces="application/json; charset=UTF8")
    @Operation(summary = "(학생) 게임별 나의 최고 기록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"stdtId\":\"vsstu1\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"gameId\":\"em09_422\"," +
                            "\"cnt\":10," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> mathGameMyRank(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = mathExploreService.findGameMyBestScr(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "게임별 나의 최고 기록");

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
