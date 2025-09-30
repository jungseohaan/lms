package com.visang.aidt.lms.api.openApi.common.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseDto {
    String resultCode;
    Map<String, Object> resultData;

    private ResponseDto(String resultCode, Map<String, Object> resultData) {
        this.resultCode = resultCode;
        this.resultData = resultData;
    }

    public static ResponseDto returnSuccess(Map<String, Object> resultData) {
        return new ResponseDto("success", resultData);
    }

    // Static method for failure response
    public static ResponseDto returnFail() {
        return new ResponseDto("fail", null);
    }
}
