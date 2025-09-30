package com.visang.aidt.lms.global.vo.socket;

import lombok.Getter;

@Getter
public enum SocketErrorCode {

    SUCCESS("0", "성공"),
    FAIL("999", "Parameter 누락, Exception 등 비정상 수행");

    private String code;
    private String description;

    SocketErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
