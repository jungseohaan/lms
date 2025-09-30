package com.visang.aidt.lms.api.utility.exceptionhandler;

import com.visang.aidt.lms.global.ResponseDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

abstract class AbstractExceptionHandler<T> {

   /**
     * Sql Exception Handling
     * @param sqlException SQLException
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
   protected abstract ResponseDTO<T> sqlException(SQLException sqlException, HttpServletRequest httpServletRequest);

   /**
     * JDBC Template Exception Handling
     * @param dataAccessException DataAccessException
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
   protected abstract ResponseDTO<T> dataAccessException(DataAccessException dataAccessException, HttpServletRequest httpServletRequest);

   /**
     * Other Exception Handling
     * @param exception Exception
     * @param httpServletRequest HttpServletRequest
     * @return ResponseDTO<ExceptionBody>
     */
   protected abstract ResponseDTO<T> exception(Exception exception, HttpServletRequest httpServletRequest);

   /**
     * RequestParam Missing Exception handler
     * @param exception HttpServletRequest
     * @param httpServletRequest MissingServletRequestParameterException
     * @return ResponseDTO<ExceptionBody>
     */
   protected abstract ResponseDTO<T> missingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest httpServletRequest);

   /**
     * RequestParam Type MissMatch Exception Handler
     * @param exception MethodArgumentTypeMismatchException
     * @return ResponseDTO<SocketExceptionBody>
     */
   protected abstract ResponseDTO<T> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest httpServletRequest);
}
