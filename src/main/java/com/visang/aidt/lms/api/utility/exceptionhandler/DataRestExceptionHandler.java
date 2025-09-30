package com.visang.aidt.lms.api.utility.exceptionhandler;

import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.ExceptionBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import static com.visang.aidt.lms.api.utility.utils.CustomLokiLog.*;

@Slf4j
@ControllerAdvice(basePackageClasses= RepositoryRestExceptionHandler.class)
public class DataRestExceptionHandler {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    protected ResponseDTO<ExceptionBody> exception(Exception ex, HttpServletRequest httpServletRequest) {

        log.error(dataRestLog(ex, httpServletRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseDTO.of()
                .exception()
                .path(httpServletRequest.getRequestURI())
                .name(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .build();
    }

}
