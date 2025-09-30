package com.visang.aidt.lms.api.utility.exceptionhandler;

import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.socket.SocketErrorCode;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import static com.visang.aidt.lms.api.utility.utils.CustomLokiLog.*;

/**
 * socket error response 형식은 다르기 때문에 분리
 * socket 관련 basePackage 정의 필요
 */

@Slf4j
@Order(Integer.MIN_VALUE)
@RestControllerAdvice(basePackages = "com.visang.aidt.lms.api.socket")
class SocketExceptionHandler extends AbstractExceptionHandler<SocketExceptionBody> {

    /**
     * Sql Exception Handling
     * @param sqlException SQLException
     * @return ResponseDTO<SocketExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SQLException.class)
    protected ResponseDTO<SocketExceptionBody> sqlException(SQLException sqlException, HttpServletRequest httpServletRequest) {
        return sqlExceptionTemplate(sqlException);
    }

    /**
     * JDBC Template Exception Handling
     * @param dataAccessException DataAccessException
     * @return ResponseDTO<SocketExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = DataAccessException.class)
    protected ResponseDTO<SocketExceptionBody> dataAccessException(DataAccessException dataAccessException, HttpServletRequest httpServletRequest) {
        return sqlExceptionTemplate(dataAccessException);
    }

    /**
     * Other Exception Handling
     * @param exception Exception
     * @return ResponseDTO<SocketExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    protected ResponseDTO<SocketExceptionBody> exception(Exception exception, HttpServletRequest httpServletRequest) {
        log.error(errorLog(exception));
        return exceptionTemplate(exception);
    }

    /**
     * RequestParam Missing Exception handler
     * @param exception MissingServletRequestParameterException
     * @return ResponseDTO<ExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseDTO<SocketExceptionBody> missingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest httpServletRequest) {
        log.error(parameterErrorLog(exception));
        return exceptionTemplate(exception);
    }

    /**
     * RequestParam Type MissMatch Exception Handler
     * @param exception MethodArgumentTypeMismatchException
     * @return ResponseDTO<SocketExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseDTO<SocketExceptionBody> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest httpServletRequest) {
        log.error(typeMissMatchErrorLog(exception));
        return exceptionTemplate(exception);
    }

    private ResponseDTO<SocketExceptionBody> sqlExceptionTemplate(Exception ex) {

        log.error(sqlErrorLog(ex));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseDTO.of()
                .header(headers)
                .socketException()
                .result(SocketErrorCode.FAIL.getCode())
                .returnType(ex.getCause().getMessage())
                .build();
    }

    private ResponseDTO<SocketExceptionBody> exceptionTemplate(Exception ex) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseDTO.of()
                .header(headers)
                .socketException()
                .result(SocketErrorCode.FAIL.getCode())
                .returnType(ex.getMessage())
                .build();
    }
}
