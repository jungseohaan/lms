package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.lecture.service.TchCrcuCateService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 커리큘럼 분류 API Controller
 */
@RestController
//@Api(tags = "(교사) 커리큘럼 분류 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchCrcuCateController {
    private final TchCrcuCateService tchCrcuCateService;

    //@ApiOperation(value = "분류 생성_수정_삭제", notes = "")
    @RequestMapping(value = "/tch/crcu/cate/create", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuCate(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = tchCrcuCateService.createCrcuCate(paramData);
        resultMessage = "분류 생성";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @RequestMapping(value = "/tch/crcu/cate/modify", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuCateModify(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = tchCrcuCateService.modifyCrcuCate(paramData);
        resultMessage = "분류 수정";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @RequestMapping(value = "/tch/crcu/cate/remove", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuCateRemove(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = tchCrcuCateService.removeCrcuCate(paramData);
        resultMessage = "분류 삭제";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "분류 복제", notes = "")
    @RequestMapping(value = "/tch/crcu/cate/copy", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuCateCopy() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuCateService.createCrcuCateCopy(paramData);
        String resultMessage = "분류 복제";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "동일 Depth 이동", notes = "")
    @RequestMapping(value = "/tch/crcu/cate/move", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuCateMove() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuCateService.modifyCrcuCateMove(paramData);
        String resultMessage = "동일 Depth 이동";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

}
