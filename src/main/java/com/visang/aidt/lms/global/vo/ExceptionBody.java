package com.visang.aidt.lms.global.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record ExceptionBody(String path, String name, ErrorCode code, String message) {
}
