package com.visang.aidt.lms.global.vo.socket;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record SocketExceptionBody(String result, String returnType) {
}
