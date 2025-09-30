package com.visang.aidt.lms.api.integration.vo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PublishBoxEnum {

    PUBLISH_INFO_NOT_FOUND("발행 정보가 존재하지 않습니다."),
    STUDENT_ALREADY_REGISTERED("이미 등록된 학생들이 존재합니다"),
    TASK_ALREADY_ENDED("과제가 이미 종료되었습니다."),
    STUDENT_ADD_SUCCESS("학생 추가에 성공하였습니다."),
    EVALUATION_ALREADY_STARTED_OR_ENDED("평가가 이미 시작되었거나 종료되었습니다."),
    CANNOT_MODIFY_TIME_IN_ENDED_STATE("종료 상태이므로 응시 시간을 수정할 수 없습니다."),
    CANNOT_MODIFY_TIME_NOT_IN_SCHEDULED_STATE("예정 상태가 아니므로 응시 시간을 수정할 수 없습니다."),
    DIRECT_SCORING_PUBLISHED("직접 채점 발행입니다."),
    CANNOT_DELETE_ONGOING_PUBLICATION("응시가 진행 중인 발행이 존재합니다. 삭제할 수 없습니다."),
    CANNOT_MODIFY_USE_HINT("힌트 사용 여부를 수정할 수 없습니다"),
    ERROR_DURING_PUBLICATION_DELETION("발행물 삭제 중 오류가 발생하였습니다."),
    NEED_CASE_BY("케이스 번호가 필요합니다");

    private final String message;

    PublishBoxEnum(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }
}
