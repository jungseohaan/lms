package com.visang.aidt.lms.api.utility.exceptionhandler;

import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.ExceptionBody;
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
 * localException 보다 후 순위에 적용되는 AbstractExceptionHandler
 * Order(int) int 값이 높을 수록 후 순위 default Integer.MAX_VALUE
 */

@Slf4j
@Order
@RestControllerAdvice(basePackages = "com.visang.aidt.lms.api")
class GlobalExceptionHandler extends AbstractExceptionHandler<ExceptionBody> {

    /**
     * Sql Exception Handling
     * @param sqlException SQLException
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SQLException.class)
    public ResponseDTO<ExceptionBody> sqlException(SQLException sqlException, HttpServletRequest httpServletRequest) {
        return sqlExceptionTemplate(sqlException, httpServletRequest);
    }

    /**
     * JDBC Template Exception Handling
     * @param dataAccessException DataAccessException
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = DataAccessException.class)
    protected ResponseDTO<ExceptionBody> dataAccessException(DataAccessException dataAccessException, HttpServletRequest httpServletRequest) {
        return sqlExceptionTemplate(dataAccessException, httpServletRequest);
    }

    /**
     * Other Exception Handling
     * @param exception Exception
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    protected ResponseDTO<ExceptionBody> exception(Exception exception, HttpServletRequest httpServletRequest) {
        log.error(errorLog(exception));
        return exceptionTemplate(exception, httpServletRequest);
    }

    /**
     * RequestParam Missing Exception handler
     * @param exception HttpServletRequest
     * @param httpServletRequest MissingServletRequestParameterException
     * @return ResponseDTO<ExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseDTO<ExceptionBody> missingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest httpServletRequest) {
        log.error(parameterErrorLog(exception));
        return exceptionTemplate(exception, httpServletRequest);
    }

    /**
     * RequestParam Type MissMatch Exception Handler
     * @param exception MethodArgumentTypeMismatchException
     * @return ResponseDTO<SocketExceptionBody>
     */
    @Override
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseDTO<ExceptionBody> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest httpServletRequest) {
        log.error(typeMissMatchErrorLog(exception));
        return exceptionTemplate(exception, httpServletRequest);
    }

    private ResponseDTO<ExceptionBody> sqlExceptionTemplate(Exception exception, HttpServletRequest httpServletRequest) {

        log.error(sqlErrorLog(exception));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseDTO.of()
                .header(headers)
                .exception()
                .path(httpServletRequest.getRequestURI())
                .name(exception.getClass().getSimpleName())
                .message(exception.getCause().getMessage())
                .build();
    }

    private ResponseDTO<ExceptionBody> exceptionTemplate(Exception exception, HttpServletRequest httpServletRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseDTO.of()
                .header(headers)
                .exception()
                .path(httpServletRequest.getRequestURI())
                .name(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build();
    }
}
