package com.visang.aidt.lms.api.socket.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.NoSuchElementException;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserDiv {
    T("T", "선생님"),
    S("S", "학생"),
    /*A("A", "보조 선생님"),*/
    P("P", "부모님")
    /*NPT("N-PT", "부모님, 선생님 아님")*/;
    private final String code;
    private final String description;
    public static UserDiv of(@NotBlank(message = "UserDiv.from() code 는 필수 값 입니다.") final String code) {
        return Arrays.stream(UserDiv.values())
                .filter( t -> t.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코드입니다."));
    }
    public String getCode() {
        return code;
    }
    @Override
    public String toString() {
        return getCode();
    }
}
