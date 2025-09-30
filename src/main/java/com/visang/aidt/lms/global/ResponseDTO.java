package com.visang.aidt.lms.global;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import com.visang.aidt.lms.global.vo.CustomBody;
import com.visang.aidt.lms.global.vo.ErrorCode;
import com.visang.aidt.lms.global.vo.ExceptionBody;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * 사용 예시
 *
 * ResponseDTO.of()
 *      .header(null)                       // optional 헤더 정의가 필요할때
 *      .success()                          // success(), fail()
 *      .resultCode(HttpStatus.OK)          // 응답 상태 코드
 *      .paramData(testDTO)                 // 요청 데이터 - Object
 *      .resultData(resultData)             // 결과 응답 데이터 - Object
 *      .resultMessage("It's sample API")   // 응답 메세지
 *      .build();
 * @param <T> CustomBody , ExceptionBody
 */

// ResponseEntity 커스텀을 위해 HttpEntity 상속하여 커스텀
public class ResponseDTO<T> extends HttpEntity<T> {

    private ResponseDTO(@Nullable MultiValueMap<String, String> headers, T body) {
        super(body, headers);
    }

    // 정적 팩토리 메서드 기본 생성자
    public static HeaderBuilder of() {
        return new HeaderBuilder();
    }


    // 헤더 커스텀
    @JsonIgnoreType
    public static class HeaderBuilder {

        private HttpHeaders headers;

        private HeaderBuilder() {
        }

        // 헤더 커스텀이 필요할 경우
        public HeaderBuilder header(@Nullable HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        // 헤더 커스텀이 필요 없을 경우 + 성공 응답
        public BodyBuilder success() {
            return new BodyBuilder(true, headers);
        }

        // 헤더 커스텀이 필요 없을 경우 + 실패 응답
        public BodyBuilder fail() {
            return new BodyBuilder(false, headers);
        }

        // Exception Handler 에서 사용 예정
        public ExceptionBuilder exception() {
            return new ExceptionBuilder(headers);
        }

        // Socket Exception Handler 에서 사용 예정
        public SocketExceptionBuilder socketException() {
            return new SocketExceptionBuilder(headers);
        }
    }

    // 정상 응답 Body - Builder Pattern
    @JsonIgnoreType
    public static class BodyBuilder {

        private MultiValueMap<String, String> headers;

        private boolean success;
        private String resultMessage;
        private HttpStatus resultCode;
        private Object paramData;
        private Object resultData;

        private String sTime; // 요청 시간
        private String eTime; // 응답 시간
        private String hash;  // 응답 해시
        private String currentTime;

        private BodyBuilder(boolean success, MultiValueMap<String, String> headers) {
            this.success = success;
            this.headers = headers;
        }

        public BodyBuilder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }

        public BodyBuilder resultCode(HttpStatus resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public BodyBuilder paramData(Object paramData) {
            this.paramData = paramData;
            return this;
        }

        public BodyBuilder resultData(Object resultData) {
            this.resultData = resultData;
            return this;
        }

        public BodyBuilder sTime(String sTime) {
            this.sTime = sTime;
            return this;
        }

        public BodyBuilder eTime(String eTime) {
            this.eTime = eTime;
            return this;
        }

        public BodyBuilder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public BodyBuilder currentTime(String currentTime) {
            this.currentTime = currentTime;
            return this;
        }

        public ResponseDTO<CustomBody> build() {
            return new ResponseDTO<>(
                    headers, new CustomBody(success, resultMessage, resultCode.value(), paramData, resultData, sTime, eTime, hash,currentTime)
            );
        }
    }

    // Exception Handle 을 위한 Body - Builder Pattern
    @JsonIgnoreType
    public static class ExceptionBuilder {

        private MultiValueMap<String, String> headers;

        private String path;
        private String name;
        private ErrorCode code;
        private String message;

        private ExceptionBuilder(MultiValueMap<String, String> headers) {
            this.headers = headers;
        }

        public ExceptionBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ExceptionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ExceptionBuilder code(ErrorCode code) {
            this.code = code;
            return this;
        }

        public ExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResponseDTO<ExceptionBody> build() {
            return new ResponseDTO<>(headers, new ExceptionBody(path, name, code, message));
        }
    }

    // Exception Handle 을 위한 Body - Builder Pattern
    @JsonIgnoreType
    public static class SocketExceptionBuilder {

        private MultiValueMap<String, String> headers;

        private String result;
        private String returnType;

        private SocketExceptionBuilder(MultiValueMap<String, String> headers) {
            this.headers = headers;
        }

        public SocketExceptionBuilder result(String result) {
            this.result = result;
            return this;
        }


        public SocketExceptionBuilder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public ResponseDTO<SocketExceptionBody> build() {
            return new ResponseDTO<>(headers, new SocketExceptionBody(result, returnType));
        }
    }

}
